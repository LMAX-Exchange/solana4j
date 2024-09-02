package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.InnerInstructionBuilder;
import com.lmax.solana4j.api.InnerInstructions;
import com.lmax.solana4j.api.InnerTransactionBuilder;
import com.lmax.solana4j.api.InstructionBuilderBase;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

class SolanaInnerTransactionBuilder implements InnerTransactionBuilder
{
    private final List<TransactionInstruction> innerInstructions = new ArrayList<>();
    private PublicKey payer = Solana.account(new byte[32]);

    @Override
    public InnerTransactionBuilder instructions(final Consumer<TransactionBuilder> builder)
    {
        final SolanaInnerInstructionsBuilder innerInstructionsBuilder = new SolanaInnerInstructionsBuilder();
        builder.accept(innerInstructionsBuilder);
        return this;
    }

    @Override
    public InnerTransactionBuilder payer(final PublicKey payer)
    {
        this.payer = payer;
        return this;
    }

    @Override
    public InnerInstructions build()
    {
        final ByteBuffer innerTransactionBytes = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        final Accounts accounts = SolanaAccounts.create(innerInstructions, payer);
        final SolanaMessageWriterLegacy writer = new SolanaMessageWriterLegacy(accounts, new SolanaBlockhash(new byte[32]), innerInstructions);

        writer.write(innerTransactionBytes);

        return new SolanaInnerInstructions(innerInstructions, innerTransactionBytes);
    }

    private class SolanaInnerInstructionsBuilder implements TransactionBuilder
    {
        @Override
        public TransactionBuilder append(final Consumer<InstructionBuilderBase> builder)
        {
            builder.accept(innerInstructionBuilder);
            innerInstructionBuilder.addInstruction();
            return this;
        }

        private final InnerInstructionBuilder innerInstructionBuilder = new InnerInstructionBuilder()
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
            public void addInstruction()
            {
                innerInstructions.add(new SolanaTransactionInstruction(references, program, datasize, data));
            }
        };
    }
}
