package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.InnerInstructions;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.List;

class SolanaInnerInstructions implements InnerInstructions
{
    private final List<TransactionInstruction> innerInstructions;
    private final ByteBuffer innerTransactionBytes;

    SolanaInnerInstructions(final List<TransactionInstruction> innerInstructions, final ByteBuffer innerTransactionBytes)
    {
        this.innerInstructions = innerInstructions;
        this.innerTransactionBytes = innerTransactionBytes;
    }

    @Override
    public List<TransactionInstruction> getInstructions()
    {
        return innerInstructions;
    }

    @Override
    public byte[] getInnerTransactionBytes()
    {
        return innerTransactionBytes.array();
    }
}
