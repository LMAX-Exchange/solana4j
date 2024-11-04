package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents an instruction in a Solana transaction.
 * Instructions define actions to be executed on the Solana blockchain, specifying accounts involved,
 * the data for the instruction, and the program responsible for executing the instruction.
 *
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
    Integer getProgramIdIndex();

    /**
     * Returns the name or identifier of the program responsible for executing the instruction.
     * This program is associated with the program ID and specifies the logic that the instruction will follow.
     *
     * @return a string representing the program's name or identifier
     */
    String getProgram();

    /**
     * Returns the program ID associated with the instruction.
     * The program ID is a unique base58-encoded string that identifies the Solana program
     * responsible for processing the instruction's actions on the blockchain.
     *
     * @return the base58-encoded string representing the program ID
     */
    String getProgramId();


    InstructionParsed getInstructionParsed();

    /**
     * Returns the stack height at which this instruction operates, if applicable.
     * The stack height may indicate the depth or order in which the instruction is processed
     * within a series of instructions. A null value indicates that the stack height is unspecified.
     *
     * @return the stack height as an {@link Integer}, or null if not specified
     */
    Integer getStackHeight();
}
