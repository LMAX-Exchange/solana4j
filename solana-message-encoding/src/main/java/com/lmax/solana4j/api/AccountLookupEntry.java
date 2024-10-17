package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing an account lookup entry which stores the read only and read write indexes of
 * addresses in an address lookup table.
 */
public interface AccountLookupEntry
{

    /**
     * Adds a read-only entry for the given address.
     *
     * @param address       the {@link PublicKey} of the address
     * @param readOnlyIndex the index of the read-only address
     */
    void addReadOnlyEntry(PublicKey address, int readOnlyIndex);

    /**
     * Adds a read-write entry for the given address.
     *
     * @param address        the {@link PublicKey} of the address
     * @param readWriteIndex the index of the read-write address
     */
    void addReadWriteEntry(PublicKey address, int readWriteIndex);

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
     * @return a list of {@link LookupEntry} objects representing read-write address indexes
     */
    List<LookupEntry> getReadWriteLookupEntrys();

    /**
     * Returns a list of read-only address indexes.
     *
     * @return a list of {@link LookupEntry} objects representing read-only address indexes
     */
    List<LookupEntry> getReadOnlyLookupEntrys();

    /**
     * Interface representing an address lookup entry, which is the address and it's index within
     * an address lookup table.
     */
    interface LookupEntry
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
        int getIndex();
    }
}
