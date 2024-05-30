package com.lmax.solana4j.testclient.api;

import java.util.List;

public interface Instruction
{
    List<Integer> getAccounts();

    String getData();

    int getProgramIdIndex();
}
