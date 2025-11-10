package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents the response from the getVoteAccounts RPC method.
 */
public interface VoteAccounts
{
    /**
     * Returns the list of current vote accounts.
     *
     * @return the list of current vote accounts
     */
    List<VoteAccount> getCurrent();

    /**
     * Returns the list of delinquent vote accounts.
     *
     * @return the list of delinquent vote accounts
     */
    List<VoteAccount> getDelinquent();
}