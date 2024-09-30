package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import org.bitcoinj.core.Base58;


/**
 * Program for managing token operations on the Solana blockchain.
 * <p>
 * This class provides functionality to manage Solana token accounts, including operations such as creating,
 * initializing, and interacting with token accounts under the Solana Token Program.
 * </p>
 */

public final class TokenProgram extends TokenProgramBase
{
    private static final byte[] TOKEN_PROGRAM_ID = Base58.decode("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

    /**
     * The public key for the token program account.
     * <p>
     * This constant defines the public key associated with the Solana account for the token program.
     * It is set to the value returned by {@link Solana#account(byte[])} using the {@link #TOKEN_PROGRAM_ID}.
     * </p>
     */
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(TOKEN_PROGRAM_ID);

    /**
     * The span (size) of the account layout in bytes.
     * <p>
     * This constant defines the fixed size of an account layout for a Solana token account.
     * For more details, see the <a href="https://spl.solana.com/token">Solana Token Program documentation</a>.
     * </p>
     */
    public static final int ACCOUNT_LAYOUT_SPAN = 165; // https://spl.solana.com/token

    /**
     * The span (size) of the multisig account layout in bytes.
     * <p>
     * This constant defines the fixed size of a multisig account layout for a Solana token account.
     * For more details, see the <a href="https://spl.solana.com/token">Solana Token Program documentation</a>.
     * </p>
     */
    public static final int MULTI_SIG_LAYOUT_SPAN = 355; // https://spl.solana.com/token

    /**
     * Returns the program ID for the Token Program.
     * <p>
     * This method provides the public key associated with the Solana Token Program, which is necessary for
     * transactions involving token accounts.
     * </p>
     *
     * @return the public key representing the Token Program ID
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
     * Factory class for creating transactions related to the Token Program.
     * <p>
     * This factory extends the {@link TokenProgramBase.TokenProgramBaseFactory} class to provide functionality
     * specific to the Solana Token Program.
     * </p>
     */
    public static final class TokenProgramFactory extends TokenProgramBase.TokenProgramBaseFactory
    {
        /**
         * Private constructor to initialize the factory with the given token program id and transaction builder.
         *
         * @param tokenProgramId the token program id
         * @param tb the transaction builder
         */
        private TokenProgramFactory(final PublicKey tokenProgramId, final TransactionBuilder tb)
        {
            super(tokenProgramId, tb);
        }
    }
}
