package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.api.TransactionBuilderBase;
import net.i2p.crypto.eddsa.math.Curve;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

public final class AddressLookupTableProgram
{
    private static final byte[] ADDRESS_LOOKUP_TABLE_PROGRAM = Base58.decode("AddressLookupTab1e1111111111111111111111111");
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(ADDRESS_LOOKUP_TABLE_PROGRAM);

    private static final int CREATE_LOOKUP_TABLE_INSTRUCTION = 0;
    private static final int EXTEND_LOOKUP_TABLE_INSTRUCTION = 2;

    private static final int LOOKUP_TABLE_META_SIZE = 56;

    private final TransactionBuilderBase tb;

    public static AddressLookupTableProgram factory(final TransactionBuilderBase tb)
    {
        return new AddressLookupTableProgram(tb);
    }

    private AddressLookupTableProgram(final TransactionBuilderBase tb)
    {
        this.tb = tb;
    }

    public void createAddressLookupTableInstruction(final AddressWithBumpSeed addressWithBumpSeed, final PublicKey authority, final PublicKey payer, final Slot recentSlot)
    {
        tb.append(ib -> ib
                .program(PROGRAM_ACCOUNT)
                .account(Solana.account(addressWithBumpSeed.getLookupTableAddress()), false, true)
                .account(authority, true, false)
                .account(payer, true, false)
                .account(SystemProgram.SYSTEM_PROGRAM_ACCOUNT, false, false)
                .data(13, bb ->
                        {
                            bb.order(ByteOrder.LITTLE_ENDIAN).putInt(CREATE_LOOKUP_TABLE_INSTRUCTION);
                            recentSlot.write(bb);
                            bb.put((byte) addressWithBumpSeed.getBumpSeed());
                        }
                )
        );
    }

    public void extendAddressLookupTable(final PublicKey lookupTable, final PublicKey authority, final PublicKey payer, final List<PublicKey> addresses)
    {
        tb.append(ib -> ib
                .program(PROGRAM_ACCOUNT)
                .account(lookupTable, false, true)
                .account(authority, true, false)
                .account(payer, true, false)
                .account(SystemProgram.SYSTEM_PROGRAM_ACCOUNT, false, false)
                .data(12 + (addresses.size() * 32), bb ->
                      {
                          bb.order(ByteOrder.LITTLE_ENDIAN).putInt(EXTEND_LOOKUP_TABLE_INSTRUCTION);
                          bb.putLong(addresses.size());
                          addresses.forEach(address -> address.write(bb));
                      }
                )
        );
    }

    public static AddressLookupTable deserializeAddressLookupTable(final PublicKey lookupTableAddress, final byte[] lookupAddressData)
    {
        final int serializedAddressesLength = lookupAddressData.length - LOOKUP_TABLE_META_SIZE;

        assert serializedAddressesLength >= 0;
        assert serializedAddressesLength % 32 == 0;

        final int numberOfSerializedAddresses = serializedAddressesLength / 32;

        final ByteBuffer lookupTable = ByteBuffer.allocate(lookupAddressData.length);
        lookupTable.put(lookupAddressData);
        lookupTable.position(LOOKUP_TABLE_META_SIZE);

        final List<PublicKey> addresses = new ArrayList<>();
        for (int i = 0; i < numberOfSerializedAddresses; i++)
        {
            final byte[] account = new byte[32];
            lookupTable.get(account, 0, 32);
            final PublicKey address = Solana.account(account);
            addresses.add(address);
        }

        return new SolanaAddressLookupTable(lookupTableAddress, addresses);
    }


    public static AddressWithBumpSeed deriveLookupTableAddress(final PublicKey authority, final Slot slot)
    {
        final Curve curve = ED_25519_CURVE_SPEC.getCurve();
        int bumpSeed = 255;

        byte[] programAddress = new byte[32];
        try
        {
            while (bumpSeed > 0)
            {
                final ByteBuffer slotBuffer = ByteBuffer.allocate(8);
                slotBuffer.order(ByteOrder.LITTLE_ENDIAN);
                slot.write(slotBuffer);

                final ByteBuffer seeds = ByteBuffer.allocate(41);
                authority.write(seeds);
                seeds.order(ByteOrder.LITTLE_ENDIAN);
                seeds.put(slotBuffer.flip());

                seeds.put(getNextSeed(bumpSeed));

                final ByteBuffer programId = ByteBuffer.allocate(32);
                PROGRAM_ACCOUNT.write(programId);

                programAddress = createProgramAddress(seeds, programId);
                curve.createPoint(programAddress, false);
                bumpSeed--;
            }
        }
        catch (final RuntimeException re)
        {
            return new AddressWithBumpSeed(programAddress, bumpSeed);
        }

        throw new RuntimeException("Could not find a program address off the curve.");
    }

    private static byte[] createProgramAddress(final ByteBuffer seeds, final ByteBuffer programId)
    {
        final MessageDigest messageDigest = Sha256Hash.newDigest();

        messageDigest.update(seeds.flip());
        messageDigest.update(programId.flip());
        messageDigest.update("ProgramDerivedAddress".getBytes(StandardCharsets.UTF_8));

        return messageDigest.digest();
    }

    private static byte[] getNextSeed(final int currentSignedByteValue)
    {
        final byte[] seed = new byte[1];

        seed[0] = (byte) (currentSignedByteValue);

        return seed;
    }

}
