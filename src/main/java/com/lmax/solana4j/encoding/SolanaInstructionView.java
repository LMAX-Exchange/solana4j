package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageVisitor.InstructionView;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

final class SolanaInstructionView implements InstructionView
{
    private final int program;
    private final List<Integer> accountIndexes;

    private final ByteBuffer data;

    SolanaInstructionView(final int program, final List<Integer> accounts, final ByteBuffer data)
    {
        this.program = program;
        this.accountIndexes = accounts;
        this.data = data;
    }

    @Override
    public int programIndex()
    {
        return program;
    }

    @Override
    public PublicKey program(final List<PublicKey> transactionAccounts)
    {
        return transactionAccounts.get(program);
    }

    @Override
    public List<Integer> accountIndexes()
    {
        return accountIndexes;
    }

    @Override
    public List<PublicKey> accounts(final List<PublicKey> transactionAccounts)
    {
        return accountIndexes.stream().map(transactionAccounts::get).collect(Collectors.toList());
    }

    @Override
    public ByteBuffer data()
    {
        return data.duplicate();
    }
}
