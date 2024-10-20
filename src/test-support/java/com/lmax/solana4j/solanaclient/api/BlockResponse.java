package com.lmax.solana4j.solanaclient.api;

import java.util.List;

public interface BlockResponse
{
    Long getBlockHeight();

    Long getBlockTime();

    String getBlockhash();

    long getParentSlot();

    String getPreviousBlockhash();

    List<Transaction> getTransactions();

}
