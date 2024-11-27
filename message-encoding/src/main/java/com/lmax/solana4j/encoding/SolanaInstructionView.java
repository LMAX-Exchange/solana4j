package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageVisitor.InstructionView;

import java.nio.ByteBuffer;
import java.util.List;

class SolanaInstructionView implements InstructionView
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
    public List<Integer> accountIndexes()
    {
        return accountIndexes;
    }

    @Override
    public ByteBuffer data()
    {
        return data.duplicate();
    }
}
