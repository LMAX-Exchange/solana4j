package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.api.TransactionBuilderBase;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

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

    public AddressLookupTableProgram createLookupTable(final ProgramDerivedAddress programDerivedAddress, final PublicKey authority, final PublicKey payer, final Slot recentSlot)
    {
        tb.append(ib -> ib
                .program(PROGRAM_ACCOUNT)
                .account(programDerivedAddress.address(), false, true)
                .account(authority, true, false)
                .account(payer, true, false)
                .account(SystemProgram.SYSTEM_PROGRAM_ACCOUNT, false, false)
                .data(13, bb ->
                        {
                            bb.order(ByteOrder.LITTLE_ENDIAN).putInt(CREATE_LOOKUP_TABLE_INSTRUCTION);
                            recentSlot.write(bb);
                            bb.put((byte) programDerivedAddress.nonce());
                        }
                )
        );

        return this;
    }

    public AddressLookupTableProgram extendLookupTable(final PublicKey lookupTable, final PublicKey authority, final PublicKey payer, final List<PublicKey> addresses)
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

        return this;
    }

    public static ProgramDerivedAddress deriveAddress(final PublicKey authority, final Slot slot)
    {
        return SolanaEncoding.deriveProgramAddress(List.of(authority.bytes(), slot.bytes()), AddressLookupTableProgram.PROGRAM_ACCOUNT);
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

        return SolanaEncoding.addressLookupTable(lookupTableAddress, addresses);
    }
}
