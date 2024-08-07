package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing inner instructions in a Solana blockchain transaction.
 * <p>
 * Inner instructions are additional instructions that are part of a transaction and provide
 * more granular control over the transaction's execution.
 * </p>
 */
public interface InnerInstructions
{

    /**
     * Returns the list of transaction instructions.
     *
     * @return a list of {@link TransactionInstruction} objects representing the inner instructions
     */
    List<TransactionInstruction> getInstructions();

    /**
     * Returns the byte array representation of the inner transaction.
     *
     * @return a byte array representing the inner transaction
     */
    byte[] getInnerTransactionBytes();
}
