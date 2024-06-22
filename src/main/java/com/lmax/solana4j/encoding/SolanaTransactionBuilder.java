package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.InstructionBuilder;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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
    public TransactionBuilder append(final Consumer<InstructionBuilder> consumer)
    {
        final SolanaInstructionBuilder ib = new SolanaInstructionBuilder();
        consumer.accept(ib);
        ib.build();
        return this;
    }

    private class SolanaInstructionBuilder implements InstructionBuilder
    {
        private final List<SolanaAccountReference> references = new ArrayList<>();
        private SolanaAccount program;

        private int datasize;
        private Consumer<ByteBuffer> data;
        private SolanaAccountReference programReference;

        @Override
        public InstructionBuilder account(final PublicKey account, final boolean signs, final boolean writes)
        {
            requireNonNull(account);
            references.add(new SolanaAccountReference(account, signs, writes, false));
            return this;
        }

        @Override
        public InstructionBuilder program(final PublicKey account)
        {
            program = (SolanaAccount) account;
            return this;
        }

        @Override
        public InstructionBuilder data(final int datasize, final Consumer<ByteBuffer> writer)
        {
            this.datasize = datasize;
            data = writer;
            return this;
        }

        @Override
        public MessageBuilder build()
        {
            instructions.add(new SolanaTransactionInstruction(references, program, datasize, data));
            return messageBuilder;
        }
    }
}
