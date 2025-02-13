package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing an address lookup table on the blockchain.
 */
public interface AddressLookupTable
{

    /**
     * Returns the public key of the lookup table address.
     *
     * @return the {@link PublicKey} of the lookup table address
     */
    PublicKey getLookupTableAddress();

    /**
     * Returns a list of public keys that are part of the address lookups.
     *
     * @return a list of {@link PublicKey} objects representing the address lookups
     */
    List<PublicKey> getAddresses();
}
