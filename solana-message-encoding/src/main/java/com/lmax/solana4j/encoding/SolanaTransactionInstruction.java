package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

final class SolanaTransactionInstruction implements TransactionInstruction
{
    private final List<SolanaAccountReference> accounts;
    private final SolanaAccount program;

    private final int datasize;
    private final Consumer<ByteBuffer> data;

    SolanaTransactionInstruction(
            final List<SolanaAccountReference> accounts,
            final SolanaAccount program,
            final int datasize,
            final Consumer<ByteBuffer> data)
    {
        this.accounts = accounts;
        this.program = program;
        this.datasize = datasize;
        this.data = data;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<AccountReference> accountReferences()
    {
        return (List) accounts;
    }

    @Override
    public SolanaAccount program()
    {
        return program;
    }

    @Override
    public int datasize()
    {
        return datasize;
    }

    @Override
    public Consumer<ByteBuffer> data()
    {
        return data;
    }

}
