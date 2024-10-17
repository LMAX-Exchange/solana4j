package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionInstruction.AccountReference;

final class SolanaAccountReference implements AccountReference
{
    private final PublicKey account;
    private final boolean isSigner;
    private final boolean isWriter;
    private final boolean isExecutable;

    SolanaAccountReference(
            final PublicKey account,
            final boolean isSigner,
            final boolean isWriter,
            final boolean isExecutable)
    {
        this.account = account;
        this.isSigner = isSigner;
        this.isWriter = isWriter;
        this.isExecutable = isExecutable;
    }

    @Override
    public PublicKey account()
    {
        return account;
    }

    @Override
    public boolean isSigner()
    {
        return isSigner;
    }

    @Override
    public boolean isWriter()
    {
        return isWriter;
    }

    @Override
    public boolean isExecutable()
    {
        return isExecutable;
    }
}
