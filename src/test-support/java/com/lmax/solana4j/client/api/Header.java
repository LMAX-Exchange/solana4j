package com.lmax.solana4j.client.api;

public interface Header
{
    int getNumReadonlySignedAccounts();

    int getNumReadonlyUnsignedAccounts();

    int getNumRequiredSignatures();
}
