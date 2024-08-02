package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import org.bitcoinj.core.Base58;

/**
 * Program for managing Token 2022 operations on the Solana blockchain.
 *
 * @param <T> the type of the Token 2022 program
 */
public final class Token2022Program<T extends Token2022Program<T>> extends TokenProgram<T>
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
     * Constructs a new {@code Token2022Program} instance.
     *
     * @param tokenProgramId the public key of the token program ID
     * @param tb             the transaction builder
     */
    Token2022Program(final PublicKey tokenProgramId, final TransactionBuilder tb)
    {
        super(tokenProgramId, tb);
    }

    /**
     * Factory method for creating a new instance of {@code Token2022Program}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code Token2022Program}
     */
    public static Token2022Program<?> factory(final TransactionBuilder tb)
    {
        return new Token2022Program<>(PROGRAM_ACCOUNT, tb);
    }
}
