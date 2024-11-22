package com.lmax.solana4j.client.api;

import java.util.List;

public interface SimulateTransactionResponse
{
    Object getErr();

    List<String> getLogs();

    AccountInfoData getAccounts();

    List<Instruction> getInnerInstructions();

    Blockhash getReplacementBlockhash();

    Data getReturnData();

    int getUnitsConsumed();
}
