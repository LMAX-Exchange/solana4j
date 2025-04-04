package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.encoding.SolanaEncoding;

/**
 * Program for managing token 2022 operations on the blockchain.
 * <p>
 * This class extends the {@link TokenProgramBase} class to support token 2022 specific operations.
 * It provides functionality to create transactions and manage token accounts under the solana token 2022 program.
 * </p>
 */
public final class Token2022Program extends TokenProgramBase
{
    private static final byte[] TOKEN_2022_PROGRAM_ID = SolanaEncoding.decodeBase58("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");

    /**
     * The public key for the Token 2022 program account.
     */
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(TOKEN_2022_PROGRAM_ID);

    /**
     * Returns the program id for the token 2022 program.
     *
     * @return the public key representing the token 2022 program id
     */
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

    /**
     * Inner factory class for creating transactions for the Token 2022 program using a token program id and {@link TransactionBuilder}.
     * <p>
     * This factory extends the {@link TokenProgramBaseFactory} class to provide functionality specific to the Token 2022 program.
     * </p>
     */
    public static final class Token2022ProgramFactory extends TokenProgramBaseFactory
    {
        private Token2022ProgramFactory(final PublicKey tokenProgramId, final TransactionBuilder tb)
        {
            super(tokenProgramId, tb);
        }
    }
}
