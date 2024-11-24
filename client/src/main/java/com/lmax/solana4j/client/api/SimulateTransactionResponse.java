package com.lmax.solana4j.client.api;

import java.util.List;

public interface SimulateTransactionResponse
{
    Object getErr();

    List<String> getLogs();

    AccountInfo.AccountInfoData getAccounts();

    List<TransactionResponse.InnerInstruction> getInnerInstructions();

    Blockhash getReplacementBlockhash();

    Data getReturnData();

    int getUnitsConsumed();

    interface Data
    {
        String getProgramId();

        List<String> getData();
    }
}
