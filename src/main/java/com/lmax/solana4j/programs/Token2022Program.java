package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import org.bitcoinj.core.Base58;

/**
 * Program for managing Token 2022 operations on the Solana blockchain.
 *
 */
public final class Token2022Program extends TokenProgramBase
{
    private static final byte[] TOKEN_2022_PROGRAM_ID = Base58.decode("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
    /**
     * The public key for the Token 2022 program account.
     * <p>
     * This constant defines the public key associated with the Solana account for the Token 2022 program.
     * It is set to the value returned by {@link Solana#account(byte[])} using the {@link #TOKEN_2022_PROGRAM_ID}.
     * </p>
     */
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(TOKEN_2022_PROGRAM_ID);

    @Override
    PublicKey getProgramId()
    {
        return PROGRAM_ACCOUNT;
    }

    /**
     * Factory method for creating a new instance of {@code Token2022ProgramFactory}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code Token2022ProgramFactory}
     */
    public static Token2022ProgramFactory factory(final TransactionBuilder tb)
    {
        return new Token2022ProgramFactory(PROGRAM_ACCOUNT, tb);
    }

    public static class Token2022ProgramFactory extends TokenProgramBaseFactory
    {
        /**
         * Constructs a new {@code Token2022ProgramFactory} instance.
         *
         * @param tb the transaction builder
         */
        Token2022ProgramFactory(final PublicKey programId, final TransactionBuilder tb)
        {
            super(programId, tb);
        }
    }
}
