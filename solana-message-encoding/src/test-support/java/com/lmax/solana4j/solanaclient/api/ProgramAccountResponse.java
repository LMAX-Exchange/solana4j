package com.lmax.solana4j.solanaclient.api;

public interface ProgramAccountResponse
{
    String getPubKey();

    AccountInfo getAccount();
}
