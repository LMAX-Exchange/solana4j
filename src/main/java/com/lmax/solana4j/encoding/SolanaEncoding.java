package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.Message;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public final class SolanaEncoding
{
    // TODO: javadoc comment
    public static final int MAX_MESSAGE_SIZE = 1280 - 40 - 8;

    public static MessageBuilder builder(final ByteBuffer buffer)
    {
        return new SolanaMessageBuilder(buffer);
    }

    public static SignedMessageBuilder forSigning(final ByteBuffer message)
    {
        return new SolanaSignedMessageBuilder(message);
    }

    public static Message read(final ByteBuffer message)
    {
        return new SolanaMessageReader(message).read();
    }

    public static PublicKey account(final byte[] bytes)
    {
        return new SolanaAccount(bytes);
    }

    public static ProgramDerivedAddress deriveProgramAddress(final List<byte[]> seeds, final PublicKey programId)
    {
        return SolanaProgramDerivedAddress.deriveProgramAddress(seeds, programId);
    }

    public static AssociatedTokenAddress deriveAssociatedTokenAddress(final PublicKey owner, final PublicKey mint, final PublicKey tokenProgramAccount)
    {
        return SolanaAssociatedTokenAddress.deriveAssociatedTokenAddress(owner, mint, tokenProgramAccount);
    }

    public static Blockhash blockhash(final byte[] bytes)
    {
        return new SolanaBlockhash(bytes);
    }

    public static Slot slot(final long slot)
    {
        final ByteBuffer recentSlotBuffer = ByteBuffer.allocate(8);
        recentSlotBuffer.order(ByteOrder.LITTLE_ENDIAN);
        recentSlotBuffer.putLong(slot);

        return new SolanaSlot(recentSlotBuffer.array());
    }

    public static Destination destination(final PublicKey destination, final long amount)
    {
        return new SolanaDestination(destination, amount);
    }

    public static AddressLookupTable addressLookupTable(final PublicKey lookupTableAddress, final List<PublicKey> addressLookups)
    {
        return new SolanaAddressLookupTable(lookupTableAddress, addressLookups);
    }
}
