package com.lmax.solana4j.client.api;

import java.util.List;
import java.util.Map;

/**
 * Represents metadata associated with a transaction on the Solana blockchain.
 * This interface provides detailed information about the transaction, such as errors, fees, balances, log messages, rewards, and more.
 */
public interface TransactionMetadata
{
    /**
     * Returns any error information associated with the transaction.
     * If the transaction was successful, this method returns null; otherwise, it returns an object representing the error.
     *
     * @return an object representing the error, or null if the transaction was successful
     */
    Object getErr();

    /**
     * Returns the fee paid for processing the transaction.
     * The fee is deducted in lamports, which are the smallest unit of SOL.
     *
     * @return the transaction fee in lamports
     */
    long getFee();

    /**
     * Returns a list of inner instructions executed within the transaction.
     * Inner instructions are additional program invocations that occur as part of the transaction.
     *
     * @return a list of {@link InnerInstruction} objects representing the inner instructions
     */
    List<InnerInstruction> getInnerInstructions();

    /**
     * Returns the log messages generated during the transaction.
     * These messages can provide insights into the execution of the transaction, including program-specific logging.
     *
     * @return a list of log messages as strings
     */
    List<String> getLogMessages();

    /**
     * Returns a list of balances before the transaction was processed.
     * Each balance corresponds to an account involved in the transaction, represented in lamports.
     *
     * @return a list of pre-transaction balances in lamports
     */
    List<Long> getPreBalances();

    /**
     * Returns a list of balances after the transaction was processed.
     * Each balance corresponds to an account involved in the transaction, represented in lamports.
     *
     * @return a list of post-transaction balances in lamports
     */
    List<Long> getPostBalances();

    /**
     * Returns a list of token balances before the transaction was processed.
     * This provides the pre-transaction SPL token balances for accounts that hold tokens.
     *
     * @return a list of {@link TokenBalance} objects representing pre-transaction token balances
     */
    List<TokenBalance> getPreTokenBalances();

    /**
     * Returns a list of token balances after the transaction was processed.
     * This provides the post-transaction SPL token balances for accounts that hold tokens.
     *
     * @return a list of {@link TokenBalance} objects representing post-transaction token balances
     */
    List<TokenBalance> getPostTokenBalances();

    /**
     * Returns a list of rewards distributed as part of the transaction.
     * Rewards may be given to validators or other participants in the network.
     *
     * @return a list of {@link Reward} objects representing the rewards distributed
     */
    List<Reward> getRewards();

    /**
     * Returns the number of compute units consumed during the transaction's execution.
     * Compute units represent the computational resources used to process the transaction.
     *
     * @return the number of compute units consumed
     */
    long getComputeUnitsConsumed();

    /**
     * Returns the addresses that were loaded during the transaction.
     * Loaded addresses may include writable or read-only addresses that provide data or permissions necessary for the transaction's execution.
     *
     * @return the {@link LoadedAddresses} object representing loaded addresses
     */
    LoadedAddresses getLoadedAddresses();

    /**
     * Returns the transaction status, including the status type and message.
     * The status provides additional information on whether the transaction was successful and any related messages.
     *
     * @return a {@link Map.Entry} containing the status type as the key and the status message as the value
     */
    Map.Entry<String, String> getStatus();
}
