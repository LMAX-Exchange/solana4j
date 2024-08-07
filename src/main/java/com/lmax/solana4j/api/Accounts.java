package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing a collection of accounts in the Solana blockchain.
 */
public interface Accounts
{

    /**
     * Returns a flattened list of all public keys in the account collection.
     *
     * @return a list of {@link PublicKey} objects representing all accounts
     */
    List<PublicKey> getFlattenedAccountList();

    /**
     * Returns a list of static public keys that do not change.
     *
     * @return a list of {@link PublicKey} objects representing static accounts
     */
    List<PublicKey> getStaticAccounts();

    /**
     * Returns a list of address lookup table indexes.
     *
     * @return a list of {@link AddressLookupTableIndexes} objects representing lookup accounts
     */
    List<AddressLookupTableIndexes> getLookupAccounts();

    /**
     * Returns the count of signed accounts.
     *
     * @return the number of signed accounts
     */
    int getCountSigned();

    /**
     * Returns the count of read-only signed accounts.
     *
     * @return the number of read-only signed accounts
     */
    int getCountSignedReadOnly();

    /**
     * Returns the count of read-only unsigned accounts.
     *
     * @return the number of read-only unsigned accounts
     */
    int getCountUnsignedReadOnly();
}

