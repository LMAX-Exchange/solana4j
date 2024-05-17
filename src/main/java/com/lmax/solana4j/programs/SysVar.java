package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;

public final class SysVar
{
    public static final PublicKey CLOCK =
            Solana.account(Base58.decode("SysvarC1ock11111111111111111111111111111111"));

    public static final PublicKey EPOCH_SCHEDULE =
            Solana.account(Base58.decode("SysvarEpochSchedu1e111111111111111111111111"));

    public static final PublicKey INSTRUCTIONS =
            Solana.account(Base58.decode("Sysvar1nstructions1111111111111111111111111"));

    public static final PublicKey RECENT_BLOCKHASHES =
            Solana.account(Base58.decode("SysvarRecentB1ockHashes11111111111111111111"));

    public static final PublicKey RENT =
            Solana.account(Base58.decode("SysvarRent111111111111111111111111111111111"));

    public static final PublicKey REWARDS =
            Solana.account(Base58.decode("SysvarRewards111111111111111111111111111111"));

    public static final PublicKey SLOT_HASHES =
            Solana.account(Base58.decode("SysvarS1otHashes111111111111111111111111111"));

    public static final PublicKey SLOT_HISTORY =
            Solana.account(Base58.decode("SysvarS1otHistory11111111111111111111111111"));

    public static final PublicKey STAKE_HISTORY =
            Solana.account(Base58.decode("SysvarStakeHistory1111111111111111111111111"));
}