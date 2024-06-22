package com.lmax.solana4j.api;

import java.util.List;

public interface InnerInstructions
{
    List<TransactionInstruction> getInstructions();

    byte[] getInnerTransactionBytes();
}
