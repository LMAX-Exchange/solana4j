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
}
