package com.lmax.solana4j.programs.token;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import org.bitcoinj.core.Base58;


/**
 * Program for managing token operations on the Solana blockchain.
 *
 */

public final class TokenProgram
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
     * The span (size) of the multi sig account layout in bytes.
     * <p>
     * This constant defines the fixed size of a multi sig account layout for a Solana token account.
     * For more details, see the <a href="https://spl.solana.com/token">Solana Token Program documentation</a>.
     * </p>
     */
    public static final int MULTI_SIG_LAYOUT_SPAN = 355; // https://spl.solana.com/token

    /**
     * Factory method for creating a new instance of {@code TokenProgramFactory}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code TokenProgramFactory}
     */
    public static TokenProgramFactory factory(final TransactionBuilder tb)
    {
        return new TokenProgramFactory(PROGRAM_ACCOUNT, tb);
    }

    public static class TokenProgramFactory extends TokenProgramBase.TokenProgramBaseFactory
    {
        TokenProgramFactory(final PublicKey tokenProgramId, final TransactionBuilder tb)
        {
            super(tokenProgramId, tb);
        }
    }
}