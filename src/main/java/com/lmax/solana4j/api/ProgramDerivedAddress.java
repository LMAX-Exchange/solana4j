package com.lmax.solana4j.api;

public interface ProgramDerivedAddress
{
    PublicKey address();

    PublicKey programId();

    int nonce();
}
