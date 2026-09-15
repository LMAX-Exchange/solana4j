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
        if (lamports < 0)
        {
            throw new IllegalStateException("Solana transaction invalid; priority fee must not be negative.");
        }
        configMask |= SolanaMessageWriterV1.CONFIG_PRIORITY_FEE;
        this.priorityFee = lamports;
        return this;
    }

    @Override
    public MessageBuilderV1 computeUnitLimit(final int units)
    {
        if (units < 1)
        {
            throw new IllegalStateException("Solana transaction invalid; compute unit limit must be positive.");
        }
        configMask |= SolanaMessageWriterV1.CONFIG_COMPUTE_UNIT_LIMIT;
        this.computeUnitLimit = units;
        return this;
    }

    @Override
    public MessageBuilderV1 loadedAccountsDataSizeLimit(final int bytes)
    {
        if (bytes < 0)
        {
            throw new IllegalStateException("Solana transaction invalid; loaded accounts data size limit must not be negative.");
        }
        configMask |= SolanaMessageWriterV1.CONFIG_LOADED_ACCOUNTS_DATA_SIZE;
        this.loadedAccountsDataSizeLimit = bytes;
        return this;
    }

    @Override
    public MessageBuilderV1 requestedHeapSize(final int bytes)
    {
        if (bytes % 1024 != 0 || bytes < SolanaMessageWriterV1.MIN_REQUESTED_HEAP_SIZE || bytes > SolanaMessageWriterV1.MAX_REQUESTED_HEAP_SIZE)
        {
            throw new IllegalStateException("Solana transaction invalid; requested heap size must be a multiple of 1024 between " +
                    SolanaMessageWriterV1.MIN_REQUESTED_HEAP_SIZE + " and " + SolanaMessageWriterV1.MAX_REQUESTED_HEAP_SIZE + ".");
        }
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

        if (this.recent == null)
        {
            throw new IllegalStateException("Solana transaction incomplete; recent blockhash has not been specified.");
        }

        if ((configMask & SolanaMessageWriterV1.CONFIG_COMPUTE_UNIT_LIMIT) == 0)
        {
            throw new IllegalStateException("Solana transaction incomplete; compute unit limit has not been specified.");
        }

        if ((configMask & SolanaMessageWriterV1.CONFIG_LOADED_ACCOUNTS_DATA_SIZE) == 0)
        {
            throw new IllegalStateException("Solana transaction incomplete; loaded accounts data size limit has not been specified.");
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

        if (buffer.limit() > SolanaEncoding.MAX_V1_MESSAGE_SIZE)
        {
            throw new IllegalStateException("Solana transaction invalid; V1 messages must be at most " + SolanaEncoding.MAX_V1_MESSAGE_SIZE + " bytes.");
        }

        final ByteBuffer sealedBuffer = buffer.duplicate();
        return new SolanaSealedMessageBuilder(sealedBuffer);
    }
}
