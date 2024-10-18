package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents an address table lookup used in Solana transactions.
 * This interface provides methods to access the account key associated with the lookup
 * and the indexes of writable and readonly accounts in the table.
 */
public interface AddressTableLookup
{
    /**
     * Returns the base58-encoded public key of the account associated with the address lookup table.
     * This account key is used to reference the address lookup table in Solana transactions.
     *
     * @return the base58-encoded string representing the account key for the address lookup table
     */
    String getAccountKey();

    /**
     * Returns a list of indexes that refer to the writable accounts in the address lookup table.
     * The indexes correspond to the writable entries within the address lookup table.
     *
     * @return a list of indexes representing writable accounts in the lookup table
     */
    List<Long> getWritableIndexes();

    /**
     * Returns a list of indexes that refer to the readonly accounts in the address lookup table.
     * The indexes correspond to the readonly entries within the address lookup table.
     *
     * @return a list of indexes representing readonly accounts in the lookup table
     */
    List<Long> getReadonlyIndexes();
}