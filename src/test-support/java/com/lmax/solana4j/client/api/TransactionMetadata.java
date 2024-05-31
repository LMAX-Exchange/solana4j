package com.lmax.solana4j.client.api;

import java.util.List;

public interface TransactionMetadata
{
    Object getErr();

    long getFee();

    List<InnerInstruction> getInnerInstructions();

    List<String> getLogMessages();

    List<Long> getPostBalances();

    List<TokenBalance> getPostTokenBalances();

    List<Long> getPreBalances();

    List<TokenBalance> getPreTokenBalances();

    List<Reward> getRewards();
}
