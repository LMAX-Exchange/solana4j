package com.lmax.solana4j.testclient.api;

public interface ProgramAccountResponse
{
    String getPubKey();

    AccountInfo getAccount();
}
