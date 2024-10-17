package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.util.Base58;;

/**
 * Utility class for Solana system variable (sysvar) accounts.
 */
public final class SysVar
{

    /**
     * The public key for the recent blockhashes sysvar account.
     * <p>
     * This constant defines the public key associated with the Solana account for recent blockhashes.
     * It is set to the value returned by {@link Solana#account(byte[])} using the Base58-encoded string for the sysvar account.
     * </p>
     */
    public static final PublicKey RECENT_BLOCKHASHES = Solana.account(Base58.decode("SysvarRecentB1ockHashes11111111111111111111"));

    /**
     * The public key for the rent sysvar account.
     * <p>
     * This constant defines the public key associated with the Solana account for rent.
     * It is set to the value returned by {@link Solana#account(byte[])} using the Base58-encoded string for the sysvar account.
     * </p>
     */
    public static final PublicKey RENT = Solana.account(Base58.decode("SysvarRent111111111111111111111111111111111"));
}
