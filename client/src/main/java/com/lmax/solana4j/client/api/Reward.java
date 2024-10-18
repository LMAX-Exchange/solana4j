package com.lmax.solana4j.client.api;

/**
 * Represents a reward given to a validator or account on the Solana blockchain.
 * This interface provides access to details about the reward, including the recipient's public key,
 * the amount of lamports rewarded, the post-reward balance, the reward type, and any commission involved.
 */
public interface Reward
{
    /**
     * Returns the public key of the account or validator receiving the reward.
     * The public key is a base58-encoded string representing the recipient's account.
     *
     * @return the base58-encoded public key of the reward recipient
     */
    String getPubkey();

    /**
     * Returns the amount of lamports received as a reward.
     * Lamports are the smallest unit of SOL, the native token of Solana.
     *
     * @return the amount of lamports rewarded
     */
    long getLamports();

    /**
     * Returns the post-reward balance of the account.
     * This is the account's balance after the reward has been applied.
     *
     * @return the post-reward balance in lamports
     */
    long getPostBalance();

    /**
     * Returns the type of the reward.
     * The reward type may represent different categories, such as staking rewards or inflation rewards.
     *
     * @return the string representing the type of the reward
     */
    String getRewardType();

    /**
     * Returns the commission percentage taken from the reward.
     * This applies to validator rewards, where a percentage of the reward is taken as commission.
     * If the commission is not applicable, this can return 0.
     *
     * @return the commission percentage, or 0 if no commission is applied
     */
    int getCommission();
}