package com.lmax.solana4j;

import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Message;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.nio.ByteBuffer;

public final class Solana
{
    private Solana()
    {
    }


    public static final int MAX_MESSAGE_SIZE = SolanaEncoding.MAX_MESSAGE_SIZE;

    public static MessageBuilder builder(final ByteBuffer buffer)
    {
        return SolanaEncoding.builder(buffer);
    }

    public static SignedMessageBuilder forSigning(final ByteBuffer message)
    {
        return SolanaEncoding.forSigning(message);
    }

    public static Message read(final ByteBuffer message)
    {
        return SolanaEncoding.read(message);
    }

    public static PublicKey account(final byte[] bytes)
    {
        return SolanaEncoding.account(bytes);
    }

    public static ProgramDerivedAddress programDerivedAddress(final PublicKey owner, final PublicKey programId)
    {
        return SolanaEncoding.programDerivedAddress(owner, programId);
    }

    public static AssociatedTokenAddress associatedTokenAddress(final PublicKey owner, final PublicKey mint, final PublicKey tokenProgramAccount)
    {
        return SolanaEncoding.associatedTokenAddress(owner, mint, tokenProgramAccount);
    }

    public static Blockhash blockhash(final byte[] bytes)
    {
        return SolanaEncoding.blockhash(bytes);
    }

    public static Slot slot(final long slot)
    {
        return SolanaEncoding.slot(slot);
    }
}
