package com.lmax.solana4j.programs.token;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import org.bitcoinj.core.Base58;

import java.util.List;
import java.util.Optional;


/**
 * Program for managing token operations on the Solana blockchain.
 *
 * @param <T> the type of the Token program
 */

@SuppressWarnings("unchecked") // return type cast is pretty safe after all as we're limited to our types only
public class TokenProgram<T extends TokenProgram<T>> extends TokenProgramBase<T>
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
    public static TokenProgramFactory<?> factory(final TransactionBuilder tb)
    {
        return new TokenProgramFactory<>(PROGRAM_ACCOUNT, tb);
    }

    public static class TokenProgramFactory<T extends TokenProgramFactory<T>> extends TokenProgramBaseFactory<T>
    {

        TokenProgramFactory(final PublicKey tokenProgramId, final TransactionBuilder tb)
        {
            super(tokenProgramId, tb);
        }
    }

    /**
     * Initializes a new token account.
     *
     * @param account the public key of the new account
     * @param mint    the public key of the mint
     * @param owner   the public key of the account owner
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction initializeAccount(final PublicKey account, final PublicKey mint, final PublicKey owner)
    {
        return initializeAccount(PROGRAM_ACCOUNT, account, mint, owner);
    }

    /**
     * Initializes a new mint.
     *
     * @param tokenMintAddress the public key of the token mint address
     * @param decimals         the number of decimals for the new mint
     * @param mintAuthority    the public key of the mint authority
     * @param freezeAuthority  the public key of the freeze authority (optional)
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction initializeMint(
            final PublicKey tokenMintAddress,
            final byte decimals,
            final PublicKey mintAuthority,
            final Optional<PublicKey> freezeAuthority)
    {
        return initializeMint(PROGRAM_ACCOUNT, tokenMintAddress, decimals, mintAuthority, freezeAuthority);
    }

    /**
     * Mints new tokens to a list of destinations.
     *
     * @param mint        the public key of the mint
     * @param authority   the public key of the authority
     * @param destination the list of destinations to mint to
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction mintTo(
            final PublicKey mint,
            final PublicKey authority,
            final Destination destination)
    {
        return mintTo(PROGRAM_ACCOUNT, mint, authority, destination);
    }

    /**
     * Transfers tokens between accounts.
     *
     * @param source      the public key of the source account
     * @param destination the public key of the destination account
     * @param owner       the public key of the owner account
     * @param amount      the amount of tokens to transfer
     * @param signers     the list of public keys of the signers
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction transfer(
            final PublicKey source,
            final PublicKey destination,
            final PublicKey owner,
            final long amount,
            final List<PublicKey> signers)
    {
        return transfer(PROGRAM_ACCOUNT, source, destination, owner, amount, signers);
    }

    /**
     * Initializes a new multisig account.
     *
     * @param multisigPublicKey  the public key of the multisig account
     * @param signers            the list of public keys of the signers
     * @param requiredSignatures the number of required signatures
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction initializeMultisig(
            final PublicKey multisigPublicKey,
            final List<PublicKey> signers,
            final int requiredSignatures)
    {
        return initializeMultisig(PROGRAM_ACCOUNT, multisigPublicKey, signers, requiredSignatures);
    }

    /**
     * Sets a new authority for a token account.
     *
     * @param tokenAccount  the public key of the token account
     * @param newAuthority  the public key of the new authority
     * @param oldAuthority  the public key of the old authority
     * @param signers       the list of public keys of the signers
     * @param authorityType the type of authority to set
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction setAuthority(
            final PublicKey tokenAccount,
            final PublicKey newAuthority,
            final PublicKey oldAuthority,
            final List<PublicKey> signers,
            final AuthorityType authorityType
    )
    {
        return setAuthority(PROGRAM_ACCOUNT, tokenAccount, newAuthority, oldAuthority, signers, authorityType);
    }
}
