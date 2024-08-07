package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing indexes for an address lookup table in the Solana blockchain.
 */
public interface AddressLookupTableIndexes
{

    /**
     * Adds a read-only index for the given address.
     *
     * @param address       the {@link PublicKey} of the address
     * @param readOnlyIndex the index of the read-only address
     */
    void addReadOnlyIndex(PublicKey address, int readOnlyIndex);

    /**
     * Adds a read-write index for the given address.
     *
     * @param address        the {@link PublicKey} of the address
     * @param readWriteIndex the index of the read-write address
     */
    void addReadWriteIndex(PublicKey address, int readWriteIndex);

    /**
     * Returns a list of all public keys in the address lookup table.
     *
     * @return a list of {@link PublicKey} objects representing all addresses
     */
    List<PublicKey> getAddresses();

    /**
     * Returns the public key of the lookup table address.
     *
     * @return the {@link PublicKey} of the lookup table address
     */
    PublicKey getLookupTableAddress();

    /**
     * Returns a list of read-write address indexes.
     *
     * @return a list of {@link AddressLookupIndex} objects representing read-write address indexes
     */
    List<AddressLookupIndex> getReadWriteAddressIndexes();

    /**
     * Returns a list of read-only address indexes.
     *
     * @return a list of {@link AddressLookupIndex} objects representing read-only address indexes
     */
    List<AddressLookupIndex> getReadOnlyAddressIndexes();

    /**
     * Interface representing an address lookup index in the Solana blockchain.
     */
    interface AddressLookupIndex
    {

        /**
         * Returns the public key of the address.
         *
         * @return the {@link PublicKey} of the address
         */
        PublicKey getAddress();

        /**
         * Returns the index of the address in the lookup table.
         *
         * @return the index of the address
         */
        int getAddressIndex();
    }
}
