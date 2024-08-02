package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

import static com.lmax.solana4j.encoding.SysVar.RENT;


/**
 * Program for managing token operations on the Solana blockchain.
 *
 * @param <T> the type of the Token program
 */

@SuppressWarnings("unchecked") // return type cast is pretty safe after all as we're limited to our types only
public class TokenProgram<T extends TokenProgram<? extends TokenProgram<T>>>
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
     * The instruction code for initializing a mint.
     * <p>
     * This constant defines the instruction code used to initialize a new mint in the Solana token program.
     * </p>
     */
    public static final int INITIALIZE_MINT_INSTRUCTION = 0;
    /**
     * The instruction code for initializing an account.
     * <p>
     * This constant defines the instruction code used to initialize a new account in the Solana token program.
     * </p>
     */
    public static final int INITIALIZE_ACCOUNT_INSTRUCTION = 1;
    /**
     * The instruction code for initializing a multisig account.
     * <p>
     * This constant defines the instruction code used to initialize a new multisig account in the Solana token program.
     * </p>
     */
    public static final int INITIALIZE_MULTISIG_INSTRUCTION = 2;
    /**
     * The instruction code for transferring tokens.
     * <p>
     * This constant defines the instruction code used to transfer tokens between accounts in the Solana token program.
     * </p>
     */
    public static final int TRANSFER_INSTRUCTION = 3;
    /**
     * The instruction code for setting an authority.
     * <p>
     * This constant defines the instruction code used to set a new authority for a token account in the Solana token program.
     * </p>
     */
    public static final int SET_AUTHORITY_INSTRUCTION = 6;
    /**
     * The instruction code for minting new tokens to an account.
     * <p>
     * This constant defines the instruction code used to mint new tokens to an existing account in the Solana token program.
     * </p>
     */
    public static final int MINT_TO_INSTRUCTION = 7;


    private final PublicKey programId;
    private final TransactionBuilder tb;

    /**
     * Factory method for creating a new instance of {@code TokenProgram}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code TokenProgram}
     */
    public static TokenProgram<?> factory(final TransactionBuilder tb)
    {
        return new TokenProgram<>(PROGRAM_ACCOUNT, tb);
    }

    TokenProgram(final PublicKey tokenProgramId, final TransactionBuilder tb)
    {
        this.programId = tokenProgramId;
        this.tb = tb;
    }

    /**
     * Initializes a new token account.
     *
     * @param account the public key of the new account
     * @param mint the public key of the mint
     * @param owner the public key of the account owner
     * @return this {@code TokenProgram} instance
     */
    public T initializeAccount(final PublicKey account, final PublicKey mint, final PublicKey owner)
    {
        tb.append(ib -> ib
                .program(programId)
                .account(account, false, true)
                .account(mint, false, false)
                .account(owner, false, false)
                .account(RENT, false, false)
                .data(1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN).put((byte) INITIALIZE_ACCOUNT_INSTRUCTION))
        );

        return (T) this;
    }

    /**
     * Initializes a new mint.
     *
     * @param tokenMintAddress the public key of the token mint address
     * @param decimals the number of decimals for the new mint
     * @param mintAuthority the public key of the mint authority
     * @param freezeAuthority the public key of the freeze authority (optional)
     * @return this {@code TokenProgram} instance
     */
    public T initializeMint(
            final PublicKey tokenMintAddress,
            final byte decimals,
            final PublicKey mintAuthority,
            final Optional<PublicKey> freezeAuthority)
    {
        tb.append(ib -> ib
                .program(programId)
                .account(tokenMintAddress, false, true)
                .account(RENT, false, false)
                .data(67, bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) INITIALIZE_MINT_INSTRUCTION)
                            .put(decimals);
                    mintAuthority.write(bb);
                    bb.put((byte) (
                            freezeAuthority.isPresent()
                            ? 1
                            : 0));
                    if (freezeAuthority.isPresent())
                    {
                        final PublicKey freezeAuthorityKey = freezeAuthority.get();
                        freezeAuthorityKey.write(bb);
                    }
                    else
                    {
                        bb.position(bb.position() + PublicKey.PUBLIC_KEY_LENGTH);
                    }
                })
        );

        return (T) this;
    }

    /**
     * Mints new tokens to a list of destinations.
     *
     * @param mint the public key of the mint
     * @param authority the public key of the authority
     * @param destinations the list of destinations to mint to
     * @return this {@code TokenProgram} instance
     */
    public T mintTo(
            final PublicKey mint,
            final PublicKey authority,
            final List<Destination> destinations)
    {
        for (final Destination destination : destinations)
        {
            tb.append(ib -> ib
                    .program(programId)
                    .account(mint, false, true)
                    .account(destination.getDestination(), false, true)
                    .account(authority, true, false)
                    .data(1 + 8, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) MINT_TO_INSTRUCTION)
                            .putLong(destination.getAmount()))
            );
        }

        return (T) this;
    }

    /**
     * Transfers tokens between accounts.
     *
     * @param source the public key of the source account
     * @param destination the public key of the destination account
     * @param owner the public key of the owner account
     * @param amount the amount of tokens to transfer
     * @param signers the list of public keys of the signers
     * @return this {@code TokenProgram} instance
     */
    public T transfer(
            final PublicKey source,
            final PublicKey destination,
            final PublicKey owner,
            final long amount,
            final List<PublicKey> signers)
    {
        tb.append(ib ->
        {
            ib
                    .program(programId)
                    .data(1 + 8, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) TRANSFER_INSTRUCTION)
                            .putLong(amount))
                    .account(source, false, true)
                    .account(destination, false, true)
                    .account(owner, signers.isEmpty(), false);
            signers.forEach(signer -> ib
                    .account(signer, true, false));
        });

        return (T) this;
    }

    /**
     * Initializes a new multisig account.
     *
     * @param multisigPublicKey the public key of the multisig account
     * @param signers the list of public keys of the signers
     * @param requiredSignatures the number of required signatures
     * @return this {@code TokenProgram} instance
     */
    public T initializeMultisig(
            final PublicKey multisigPublicKey,
            final List<PublicKey> signers,
            final int requiredSignatures)
    {
        tb.append(ib ->
        {
            ib
                    .program(programId)
                    .data(1 + 1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) INITIALIZE_MULTISIG_INSTRUCTION)
                            .put((byte) requiredSignatures))
                    .account(multisigPublicKey, false, true)
                    .account(RENT, false, false);
            signers.forEach(signer -> ib
                    .account(signer, false, false));
        });

        return (T) this;
    }

    /**
     * Sets a new authority for a token account.
     *
     * @param tokenAccount  the public key of the token account
     * @param newAuthority  the public key of the new authority
     * @param oldAuthority  the public key of the old authority
     * @param signers       the list of public keys of the signers
     * @param authorityType the type of authority to set
     * @return this {@code TokenProgram} instance
     */
    public T setAuthority(
            final PublicKey tokenAccount,
            final PublicKey newAuthority,
            final PublicKey oldAuthority,
            final List<PublicKey> signers,
            final AuthorityType authorityType
    )
    {
        final var newAuthorityBuffer = ByteBuffer.allocate(PublicKey.PUBLIC_KEY_LENGTH);
        newAuthority.write(newAuthorityBuffer);

        tb.append(ib ->
        {
            ib
                    .program(programId)
                    .data(1 + 1 + 1 + PublicKey.PUBLIC_KEY_LENGTH, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) SET_AUTHORITY_INSTRUCTION)
                            .put(authorityType.value)
                            .put((byte) 1)
                            .put(newAuthorityBuffer.flip())
                    )
                    .account(tokenAccount, false, true)
                    .account(oldAuthority, signers.isEmpty(), false);
            signers.forEach(signer -> ib
                    .account(signer, true, false));
        });

        return (T) this;
    }

    /**
     * Enumeration of authority types for the Solana token program.
     */
    public enum AuthorityType
    {
        /**
         * Authority type for minting new tokens.
         */
        MINT_TOKEN((byte) 0),

        /**
         * Authority type for freezing an account.
         */
        FREEZE_ACCOUNT((byte) 1),

        /**
         * Authority type for changing the owner of an account.
         */
        ACCOUNT_OWNER((byte) 2),

        /**
         * Authority type for closing an account.
         */
        CLOSE_ACCOUNT((byte) 3),

        /**
         * Authority type for configuring transfer fees.
         */
        TRANSFER_FEE_CONFIG((byte) 4),

        /**
         * Authority type for withdrawing withheld tokens.
         */
        WITHHELD_WITHDRAW((byte) 5),

        /**
         * Authority type for closing a mint.
         */
        CLOSE_MINT((byte) 6),

        /**
         * Authority type for setting the interest rate.
         */
        INTEREST_RATE((byte) 7),

        /**
         * Authority type for setting a permanent delegate.
         */
        PERMANENT_DELEGATE((byte) 8),

        /**
         * Authority type for configuring confidential transfer mint.
         */
        CONFIDENTIAL_TRANSFER_MINT((byte) 9),

        /**
         * Authority type for setting the transfer hook program ID.
         */
        TRANSFER_HOOK_PROGRAM_ID((byte) 10),

        /**
         * Authority type for configuring confidential transfer fees.
         */
        CONFIDENTIAL_TRANSFER_FEE_CONFIG((byte) 11),

        /**
         * Authority type for setting a metadata pointer.
         */
        METADATA_POINTER((byte) 12);

        final byte value;

        AuthorityType(final byte value)
        {
            this.value = value;
        }
    }
}
