package com.lmax.solana4j.client.api;

import java.util.List;

public interface Instruction
{
    List<Integer> getAccounts();

    String getData();

    int getProgramIdIndex();
}
