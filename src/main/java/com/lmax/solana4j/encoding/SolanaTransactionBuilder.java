package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.InstructionBuilder;
import com.lmax.solana4j.api.InstructionBuilderBase;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.MessageInstructionBuilder;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

final class SolanaTransactionBuilder implements TransactionBuilder
{
    private final SolanaMessageBuilder messageBuilder;
    private final List<TransactionInstruction> instructions;

    SolanaTransactionBuilder(final SolanaMessageBuilder messageBuilder, final List<TransactionInstruction> instructions)
    {
        this.messageBuilder = messageBuilder;
        this.instructions = instructions;
    }

    @Override
    public TransactionBuilder append(final Consumer<InstructionBuilderBase> consumer)
    {
        final MessageInstructionBuilder ib = new SolanaMessageInstructionBuilderBase();
        consumer.accept(ib);
        ib.build();
        return this;
    }

    @Override
    public TransactionBuilder append(final TransactionInstruction instruction)
    {
        instructions.add(instruction);
        return this;
    }

    final class SolanaMessageInstructionBuilderBase implements MessageInstructionBuilder
    {
        private final List<SolanaAccountReference> references = new ArrayList<>();
        private SolanaAccount program;

        private int datasize;
        private Consumer<ByteBuffer> data;

        @Override
        public InstructionBuilderBase account(final PublicKey account, final boolean signs, final boolean writes)
        {
            requireNonNull(account);
            references.add(new SolanaAccountReference(account, signs, writes, false));
            return this;
        }

        @Override
        public InstructionBuilderBase program(final PublicKey account)
        {
            program = (SolanaAccount) account;
            return this;
        }

        @Override
        public InstructionBuilderBase data(final int datasize, final Consumer<ByteBuffer> writer)
        {
            this.datasize = datasize;
            data = writer;
            return this;
        }

        @Override
        public MessageBuilder build()
        {
            // the order of these references matters
            final List<SolanaAccountReference> unmodifiableReferences = Collections.unmodifiableList(references);
            instructions.add(new SolanaTransactionInstruction(unmodifiableReferences, program, datasize, data));
            return messageBuilder;
        }
    }

    static final class SolanaInstructionBuilderBase implements InstructionBuilder
    {
        private final List<SolanaAccountReference> references = new ArrayList<>();
        private SolanaAccount program;

        private int datasize;
        private Consumer<ByteBuffer> data;

        @Override
        public InstructionBuilderBase account(final PublicKey account, final boolean signs, final boolean writes)
        {
            requireNonNull(account);
            references.add(new SolanaAccountReference(account, signs, writes, false));
            return this;
        }

        @Override
        public InstructionBuilderBase program(final PublicKey account)
        {
            program = (SolanaAccount) account;
            return this;
        }

        @Override
        public InstructionBuilderBase data(final int datasize, final Consumer<ByteBuffer> writer)
        {
            this.datasize = datasize;
            data = writer;
            return this;
        }

        @Override
        public TransactionInstruction build()
        {
            // the order of these references matters
            final List<SolanaAccountReference> unmodifiableReferences = Collections.unmodifiableList(references);
            return new SolanaTransactionInstruction(unmodifiableReferences, program, datasize, data);
        }
    }
}
