package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AddressWithBumpSeed;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.util.List;

public class V0TransactionFactory implements TransactionFactory
{
    @Override
    public String createAddressLookupTable(final AddressWithBumpSeed addressWithBumpSeed,
                                           final TestKeyPair authority,
                                           final TestKeyPair payer,
                                           final Slot recentSlot,
                                           final com.lmax.solana4j.api.Blockhash blockhash,
                                           final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> AddressLookupTableProgram.factory(builder)
                        .createAddressLookupTableInstruction(
                                addressWithBumpSeed,
                                authority.getSolana4jPublicKey(),
                                payer.getSolana4jPublicKey(),
                                recentSlot)
                )
                .payer(payer.getSolana4jPublicKey())
                .lookups(addressLookupTables)
                .seal()
                .signed()
                .by(authority.getSolana4jPublicKey(), new BouncyCastleSigner(authority.getPrivateKeyBytes()))
                .by(payer.getSolana4jPublicKey(), new BouncyCastleSigner(payer.getPrivateKeyBytes()))
                .build();

        return base58encode(buffer);
    }

    @Override
    public String extendAddressLookupTable(final PublicKey lookupAddress,
                                           final TestKeyPair authority,
                                           final TestKeyPair payer,
                                           final List<PublicKey> addressesToAdd,
                                           final Blockhash blockhash,
                                           final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> AddressLookupTableProgram.factory(builder)
                        .extendAddressLookupTable(
                                lookupAddress,
                                authority.getSolana4jPublicKey(),
                                payer.getSolana4jPublicKey(),
                                addressesToAdd)
                )
                .payer(payer.getSolana4jPublicKey())
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);

        signedMessageBuilder.by(authority.getSolana4jPublicKey(), new BouncyCastleSigner(authority.getPrivateKeyBytes()));
        signedMessageBuilder.by(payer.getSolana4jPublicKey(), new BouncyCastleSigner(payer.getPrivateKeyBytes()));
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    private static String base58encode(final ByteBuffer bytes)
    {
        return Base58.encode(ByteBufferPrimitiveArray.copy(bytes));
    }
}
