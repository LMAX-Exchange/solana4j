package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilderBase;
import org.bitcoinj.core.Base58;


public final class Token2022Program<T extends Token2022Program<T>> extends TokenProgram<T>
{
    private static final byte[] TOKEN_2022_PROGRAM_ID = Base58.decode("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb"); // see https://spl.solana.com/token-2022
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(TOKEN_2022_PROGRAM_ID);

    Token2022Program(final PublicKey tokenProgramId, final TransactionBuilderBase tb)
    {
        super(tokenProgramId, tb);
    }

    public static Token2022Program<?> factory(final TransactionBuilderBase tb)
    {
        return new Token2022Program<>(PROGRAM_ACCOUNT, tb);
    }
}
