package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents an instruction in a Solana transaction.
 * Instructions define actions that are to be executed on the Solana blockchain, specifying accounts involved,
 * the data for the instruction, and the program to be executed.
 */
public interface Instruction
{
    /**
     * Returns a list of account indices involved in the instruction.
     * Each index refers to an account in the transaction's account list that is affected by this instruction.
     *
     * @return a list of integers representing account indices
     */
    List<Integer> getAccounts();

    /**
     * Returns the base58-encoded data associated with the instruction.
     * This data contains the arguments or parameters needed for the execution of the instruction.
     *
     * @return the base58-encoded string representing the instruction data
     */
    String getData();

    /**
     * Returns the index of the program ID that is responsible for executing the instruction.
     * The index refers to the program in the transaction's account list that defines the instruction's logic.
     *
     * @return the integer index of the program ID
     */
    int getProgramIdIndex();
}