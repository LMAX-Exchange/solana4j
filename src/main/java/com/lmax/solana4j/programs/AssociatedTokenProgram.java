package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;

public final class AssociatedTokenProgram
{
    private static final byte[] ASSOCIATED_TOKEN_PROGRAM_ID = Base58.decode("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");
    public static final PublicKey ASSOCIATED_TOKEN_PROGRAM_ACCOUNT = Solana.account(ASSOCIATED_TOKEN_PROGRAM_ID);
}
