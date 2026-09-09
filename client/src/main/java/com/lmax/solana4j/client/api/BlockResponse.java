package com.lmax.solana4j.client.api;

import java.util.List;


/**
 * Represents a block on the Solana blockchain.
 */
public interface BlockResponse
{
    /**
     * The number of blocks beneath this block in the chain.  This will be less than or equal to the slot number.
     *
     * @return The height of this block in the blockchain
     */
    long getBlockHeight();

    /**
     * Returns the block time.
     *
     * @return the block time as a Unix timestamp, or null if not available
     */
    Long getBlockTime();

    /**
     * The SHA-256 hash which uniquely represents this block.
     *
     * @return The SHA-256 hash for this block
     */
    String getBlockhash();

    /**
     * The slot number in which this block was generated.
     *
     * @return The slot number for this block
     */
    long getParentSlot();

    /**
     * The SHA-256 hash which uniquely represents the parent block of this block.
     *
     * @return The SHA-256 hash for this block's parent
     */
    String getPreviousBlockhash();

    /**
     * Details of reward payments issued in this block.
     *
     * @return A list of reward objects
     */
    List<Rewards> getRewards();

    /**
     * The transactions contained in this block.
     *
     * @return A list of transaction objects
     */
    List<TransactionResponse> getTransactions();

    /**
     * Returns the number of reward partitions in the block.
     *
     * @return the number of reward partitions, or {@code null} if not available
     */
    Long getNumRewardPartitions();

    /**
     * Represents the details of a reward payment.
     */
    interface Rewards
    {
        /**
         * The commission taken out of a staking reward by the validator on which the funds were staked.
         *
         * @return The validator commission
         */
        Long getCommission();

        /**
         * The number of lamports paid in this reward.
         *
         * @return The reward value in lamports
         */
        long getLamports();

        /**
         * The balance of the rewarded address after payment of the reward.
         *
         * @return Address balance after reward
         */
        long getPostBalance();

        /**
         * The address to which the reward was paid.
         *
         * @return Address rewqrded
         */
        String getPubkeyBase58();

        /**
         * The type of reward that was paid.
         *
         * @return The type of reward
         */
        String getRewardType();
    }
}
