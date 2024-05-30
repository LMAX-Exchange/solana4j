package com.lmax.solana4j.testclient.api;

import java.util.List;


public interface TransactionData
{
    Message getMessage();

    List<AccountKey> getAccountKeys();

    List<String> getSignatures();
}
