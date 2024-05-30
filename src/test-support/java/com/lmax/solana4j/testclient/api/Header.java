package com.lmax.solana4j.testclient.api;

public interface Header
{
    int getNumReadonlySignedAccounts();

    int getNumReadonlyUnsignedAccounts();

    int getNumRequiredSignatures();
}
