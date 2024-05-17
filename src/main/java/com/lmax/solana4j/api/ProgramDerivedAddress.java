package com.lmax.solana4j.api;

public interface ProgramDerivedAddress
{
    PublicKey address();

    PublicKey owner();

    PublicKey programId();

    int nonce();
}
