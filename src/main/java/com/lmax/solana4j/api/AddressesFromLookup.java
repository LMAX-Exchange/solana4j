package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing a collection of addresses obtained from a lookup table in the Solana blockchain.
 */
public interface AddressesFromLookup
{

    /**
     * Writes the provided list of public key addresses.
     *
     * @param addresses a list of {@link PublicKey} objects representing the addresses to be written
     */
    void write(List<PublicKey> addresses);

    /**
     * Returns the count of read-write addresses.
     *
     * @return the number of read-write addresses
     */
    int countReadWrite();

    /**
     * Returns the count of read-only addresses.
     *
     * @return the number of read-only addresses
     */
    int countReadOnly();
}
