package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.MessageBuilderV0;
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

final class SolanaMessageBuilderV0 implements MessageBuilderV0
{
    private final MessageBuilder parent;
    private final ByteBuffer buffer;
    private SolanaAccount payer;
    private SolanaBlockhash recentBlockhash;

    private final List<TransactionInstruction> instructions = new ArrayList<>();
    private List<AddressLookupTable> accountLookups = new ArrayList<>();

    SolanaMessageBuilderV0(final MessageBuilder parent, final ByteBuffer buffer)
    {
        this.parent = requireNonNull(parent);
        this.buffer = buffer;
    }

    @Override
    public MessageBuilderV0 instructions(final Consumer<TransactionBuilder> builder)
    {
        builder.accept(new SolanaTransactionBuilder((SolanaMessageBuilder) parent, instructions));
        return this;
    }

    @Override
    public MessageBuilderV0 instructions(final List<TransactionInstruction> instructions)
    {
        this.instructions.addAll(instructions);
        return this;
    }

    @Override
    public MessageBuilderV0 payer(final PublicKey account)
    {
        payer = (SolanaAccount) account;
        return this;
    }

    @Override
    public MessageBuilderV0 recent(final Blockhash blockhash)
    {
        recentBlockhash = (SolanaBlockhash) blockhash;
        return this;
    }

    @Override
    public MessageBuilderV0 lookups(final List<AddressLookupTable> accountLookups)
    {
        this.accountLookups = accountLookups;
        return this;
    }

    @Override
    public SealedMessageBuilder seal() throws BufferOverflowException
    {
        if (this.payer == null)
        {
            throw new IllegalStateException("Solana transaction incomplete; payer has not been specified.");
        }

        final Accounts accounts = SolanaAccounts.create(instructions, payer, accountLookups);

        final var writer = new SolanaMessageWriterV0(recentBlockhash, instructions, accounts);

        writer.write(buffer);
        buffer.flip();

        final ByteBuffer sealedBuffer = buffer.duplicate();

        return new SolanaSealedMessageBuilder(sealedBuffer);
    }
}
