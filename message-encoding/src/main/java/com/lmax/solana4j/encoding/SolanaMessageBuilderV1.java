package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageBuilderV1;
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

final class SolanaMessageBuilderV1 implements MessageBuilderV1
{
    private final SolanaMessageBuilder parent;
    private final ByteBuffer buffer;
    private SolanaAccount payer;
    private SolanaBlockhash recent;

    private final List<TransactionInstruction> instructions = new ArrayList<>();

    private int configMask = 0;
    private long priorityFee;
    private int computeUnitLimit;
    private int loadedAccountsDataSizeLimit;
    private int requestedHeapSize;

    SolanaMessageBuilderV1(final SolanaMessageBuilder parent, final ByteBuffer buffer)
    {
        this.parent = requireNonNull(parent);
        this.buffer = buffer;
    }

    @Override
    public MessageBuilderV1 instructions(final Consumer<TransactionBuilder> builder)
    {
        builder.accept(new SolanaTransactionBuilder(parent, instructions));
        return this;
    }

    @Override
    public MessageBuilderV1 prebuiltInstructions(final List<TransactionInstruction> instructions)
    {
        this.instructions.addAll(instructions);
        return this;
    }

    @Override
    public MessageBuilderV1 payer(final PublicKey account)
    {
        payer = (SolanaAccount) account;
        return this;
    }

    @Override
    public MessageBuilderV1 recent(final Blockhash blockhash)
    {
        recent = (SolanaBlockhash) blockhash;
        return this;
    }

    @Override
    public MessageBuilderV1 priorityFee(final long lamports)
    {
        configMask |= SolanaMessageWriterV1.CONFIG_PRIORITY_FEE;
        this.priorityFee = lamports;
        return this;
    }

    @Override
    public MessageBuilderV1 computeUnitLimit(final int units)
    {
        configMask |= SolanaMessageWriterV1.CONFIG_COMPUTE_UNIT_LIMIT;
        this.computeUnitLimit = units;
        return this;
    }

    @Override
    public MessageBuilderV1 loadedAccountsDataSizeLimit(final int bytes)
    {
        configMask |= SolanaMessageWriterV1.CONFIG_LOADED_ACCOUNTS_DATA_SIZE;
        this.loadedAccountsDataSizeLimit = bytes;
        return this;
    }

    @Override
    public MessageBuilderV1 requestedHeapSize(final int bytes)
    {
        configMask |= SolanaMessageWriterV1.CONFIG_REQUESTED_HEAP_SIZE;
        this.requestedHeapSize = bytes;
        return this;
    }

    @Override
    public SealedMessageBuilder seal() throws BufferOverflowException
    {
        if (this.payer == null)
        {
            throw new IllegalStateException("Solana transaction incomplete; payer has not been specified.");
        }

        final var accounts = SolanaAccounts.create(instructions, payer);

        final var writer = new SolanaMessageWriterV1(
                recent,
                instructions,
                accounts,
                configMask,
                priorityFee,
                computeUnitLimit,
                loadedAccountsDataSizeLimit,
                requestedHeapSize);

        writer.write(buffer);
        buffer.flip();

        final ByteBuffer sealedBuffer = buffer.duplicate();
        return new SolanaSealedMessageBuilder(sealedBuffer);
    }
}
