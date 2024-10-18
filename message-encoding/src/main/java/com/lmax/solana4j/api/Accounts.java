package com.lmax.solana4j.api;

import java.util.List;

/**
 * Interface representing a collection of accounts within a Solana Message.
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
     * Returns a list of the static public keys.
     *
     * @return a list of {@link PublicKey} objects representing static accounts
     */
    List<PublicKey> getStaticAccounts();

    /**
     * Returns a list of address lookup entrys.
     *
     * @return a list of {@link AccountLookupEntry} objects representing lookup accounts
     */
    List<AccountLookupEntry> getAccountLookups();

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

