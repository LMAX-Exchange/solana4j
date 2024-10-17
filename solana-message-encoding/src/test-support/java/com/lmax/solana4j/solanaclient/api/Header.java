package com.lmax.solana4j.solanaclient.api;

public interface Header
{
    int getNumReadonlySignedAccounts();

    int getNumReadonlyUnsignedAccounts();

    int getNumRequiredSignatures();
}
