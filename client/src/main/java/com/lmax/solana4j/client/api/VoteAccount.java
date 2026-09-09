package com.lmax.solana4j.client.api;

/**
 * Represents a vote account in the Solana network.
 */
public interface VoteAccount
{
    /**
     * Returns the validator identity public key as a base58-encoded string.
     *
     * @return the validator identity public key
     */
    String getNodePubkey();

    /**
     * Returns the vote account public key as a base58-encoded string.
     *
     * @return the vote account public key
     */
    String getVotePubkey();

    /**
     * Returns the stake activated for this vote account in lamports.
     *
     * @return the activated stake in lamports
     */
    long getActivatedStake();

    /**
     * Returns whether the vote account is a commission-earning account.
     *
     * @return true if this is a commission-earning account
     */
    boolean isEpochVoteAccount();

    /**
     * Returns the current commission percentage for the vote account (0-100).
     *
     * @return the commission percentage
     */
    int getCommission();

    /**
     * Returns the most recent slot voted on by this validator.
     *
     * @return the last vote slot
     */
    long getLastVote();

    /**
     * Returns the current root slot for this validator.
     *
     * @return the root slot
     */
    long getRootSlot();

    /**
     * Returns the epoch credits history for this vote account.
     * Each entry is a three-element array containing [epoch, credits, previousCredits].
     *
     * @return the epoch credits history
     */
    java.util.List<java.util.List<Long>> getEpochCredits();

    /**
     * Returns the inflation rewards commission in basis points for this vote account.
     *
     * @return the inflation rewards commission in basis points, or {@code null} if not available
     */
    Integer getInflationRewardsCommissionBps();
}