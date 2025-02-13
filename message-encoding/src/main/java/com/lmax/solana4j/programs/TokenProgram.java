package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.encoding.SolanaEncoding;

/**
 * Program for managing token operations on the blockchain.
 * <p>
 * This class provides functionality to manage solana token accounts, including operations such as creating,
 * initializing, and interacting with token accounts under the solana token program.
 * </p>
 */

public final class TokenProgram extends TokenProgramBase
{
    private static final byte[] TOKEN_PROGRAM_ID = SolanaEncoding.decodeBase58("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

    /**
     * The public key for the token program account.
     */
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(TOKEN_PROGRAM_ID);

    /**
     * The span (size) of the account layout in bytes.
     */
    public static final int ACCOUNT_LAYOUT_SPAN = 165; // https://spl.solana.com/token

    /**
     * The span (size) of the multisig account layout in bytes.
     */
    public static final int MULTI_SIG_LAYOUT_SPAN = 355; // https://spl.solana.com/token

    /**
     * Returns the program id for the token program.
     *
     * @return the public key representing the token program id
     */
    @Override
    PublicKey getProgramId()
    {
        return PROGRAM_ACCOUNT;
    }

    /**
     * Factory method for creating a new instance of {@code TokenProgramFactory}.
     * <p>
     * The factory method provides a mechanism for creating transactions and managing token accounts using
     * the provided {@link TransactionBuilder}.
     * </p>
     *
     * @param tb the transaction builder
     * @return a new instance of {@code TokenProgramFactory}
     */
    public static TokenProgramFactory factory(final TransactionBuilder tb)
    {
        return new TokenProgramFactory(PROGRAM_ACCOUNT, tb);
    }

    /**
     * Inner factory class for creating transactions for the Token Program using a token program id and {@link TransactionBuilder}.
     * <p>
     * This factory extends the {@link TokenProgramBase.TokenProgramBaseFactory} class to provide functionality
     * specific to the Solana Token Program.
     * </p>
     */
    public static final class TokenProgramFactory extends TokenProgramBase.TokenProgramBaseFactory
    {
        private TokenProgramFactory(final PublicKey tokenProgramId, final TransactionBuilder tb)
        {
            super(tokenProgramId, tb);
        }
    }
}
