package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageBuilderLegacy;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SealedMessageBuilder;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

final class SolanaMessageBuilderLegacy implements MessageBuilderLegacy
{
    private final SolanaMessageBuilder parent;
    private final ByteBuffer buffer;
    private SolanaAccount payer;
    private SolanaBlockhash recent;

    private final List<TransactionInstruction> instructions = new ArrayList<>();

    SolanaMessageBuilderLegacy(final SolanaMessageBuilder parent, final ByteBuffer buffer)
    {
        this.parent = requireNonNull(parent);
        this.buffer = buffer;
    }

    @Override
    public MessageBuilderLegacy payer(final PublicKey account)
    {
        payer = (SolanaAccount) account;
        return this;
    }

    @Override
    public MessageBuilderLegacy recent(final Blockhash blockhash)
    {
        recent = (SolanaBlockhash) blockhash;
        return this;
    }

    @Override
    public MessageBuilderLegacy instructions(final Consumer<TransactionBuilder> builder)
    {
        builder.accept(new SolanaTransactionBuilder(parent, instructions));
        return this;
    }

    /**
     * Writes the ordered accounts and instructions into {@link this.buffer}, then flips the buffer (i.e, prepares it for reading).
     * Normally, flipping the buffer is the responsibility of the caller that passed it in, we decided to do it in here for convenience.
     */
    @Override
    public SealedMessageBuilder seal() throws BufferOverflowException
    {
        if (this.payer == null)
        {
            throw new IllegalStateException("Solana transaction incomplete; payer has not been specified.");
        }

        final var accounts = SolanaAccounts.create(instructions, payer);

        final var writer = new SolanaMessageWriterLegacy(accounts, this.recent, this.instructions);

        writer.write(buffer);
        buffer.flip();

        final ByteBuffer sealedBuffer = buffer.duplicate();
        return new SolanaSealedMessageBuilder(sealedBuffer);
    }
}
