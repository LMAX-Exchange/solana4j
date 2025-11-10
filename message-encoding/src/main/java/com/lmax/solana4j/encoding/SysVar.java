package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;

/**
 * Utility class for Solana system variable (sysvar) accounts.
 */
public final class SysVar
{

    /**
     * The public key for the recent blockhashes sysvar account.
     */
    public static final PublicKey RECENT_BLOCKHASHES = Solana.account(Base58.decode("SysvarRecentB1ockHashes11111111111111111111"));

    /**
     * The public key for the rent sysvar account.
     */
    public static final PublicKey RENT = Solana.account(Base58.decode("SysvarRent111111111111111111111111111111111"));

    /**
     * The public key for the clock sysvar account.
     */
    public static final PublicKey CLOCK = Solana.account(Base58.decode("SysvarC1ock11111111111111111111111111111111"));

    /**
     * The public key for the stake history sysvar account.
     */
    public static final PublicKey STAKE_HISTORY = Solana.account(Base58.decode("SysvarStakeHistory1111111111111111111111111"));
}
