package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.util.Base58;;

/**
 * Program for managing Token 2022 operations on the Solana blockchain.
 * <p>
 * This class extends the {@link TokenProgramBase} class to support Token 2022 specific operations.
 * It provides functionality to create transactions and manage token accounts under the Solana Token 2022 program.
 * </p>
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

    /**
     * Returns the program ID for the Token 2022 program.
     *
     * @return the public key representing the Token 2022 program ID
     */
    @Override
    PublicKey getProgramId()
    {
        return PROGRAM_ACCOUNT;
    }

    /**
     * Factory method for creating a new instance of {@code Token2022ProgramFactory}.
     * <p>
     * The factory method is used to build Token 2022 program transactions using a provided {@link TransactionBuilder}.
     * </p>
     *
     * @param tb the transaction builder
     * @return a new instance of {@code Token2022ProgramFactory}
     */
    public static Token2022ProgramFactory factory(final TransactionBuilder tb)
    {
        return new Token2022ProgramFactory(PROGRAM_ACCOUNT, tb);
    }

    /**
     * Inner factory class for creating transactions for the Token 2022 program using a token program id and {@link TransactionBuilder}.
     * <p>
     * This factory extends the {@link TokenProgramBaseFactory} class to provide functionality specific to the Token 2022 program.
     * </p>
     */
    public static final class Token2022ProgramFactory extends TokenProgramBaseFactory
    {
        /**
         * Private constructor to initialize the factory with the given token program id and transaction builder.
         *
         * @param tokenProgramId the token program id
         * @param tb the transaction builder
         */
        private Token2022ProgramFactory(final PublicKey tokenProgramId, final TransactionBuilder tb)
        {
            super(tokenProgramId, tb);
        }
    }
}
