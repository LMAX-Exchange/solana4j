package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.nio.ByteOrder;
import java.util.List;

import static com.lmax.solana4j.encoding.SysVar.RENT;
import static com.lmax.solana4j.programs.SystemProgram.SYSTEM_PROGRAM_ACCOUNT;
import static java.util.Objects.requireNonNull;

/**
 * Program for managing associated token accounts on the blockchain.
 */
public final class AssociatedTokenProgram
{
    /**
     * The program id for the associated token program.
     */
    private static final byte[] ASSOCIATED_TOKEN_PROGRAM_ID = SolanaEncoding.decodeBase58("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");

    /**
     * The public key for the associated token program account.
     */
    public static final PublicKey ASSOCIATED_TOKEN_PROGRAM_ACCOUNT = Solana.account(ASSOCIATED_TOKEN_PROGRAM_ID);

    /**
     * The instruction code for creating an account.
     */
    public static final int CREATE_INSTRUCTION = 0;

    /**
     * The instruction code for idempotently creating an account.
     */
    public static final int IDEMPOTENT_CREATE_INSTRUCTION = 1;

    private AssociatedTokenProgram()
    {
    }

    /**
     * Factory method for creating a new instance of {@code AssociatedTokenProgramFactory}.
     *
     * @param tb the transaction builder to append instructions to
     * @return a new instance of {@code AssociatedTokenProgramFactory}
     */
    public static AssociatedTokenProgramFactory factory(final TransactionBuilder tb)
    {
        return new AssociatedTokenProgramFactory(tb);
    }

    /**
     * Inner factory class for building associated token program instructions using a {@link TransactionBuilder}.
     */
    public static final class AssociatedTokenProgramFactory
    {

        private final TransactionBuilder tb;

        private AssociatedTokenProgramFactory(final TransactionBuilder tb)
        {
            this.tb = tb;
        }

        /**
         * Creates a new associated token account.
         *
         * @param programDerivedAddress the program derived address
         * @param mint                  the public key of the mint
         * @param owner                 the public key of the owner
         * @param payer                 the public key of the payer
         * @param programId             the public key of the token program used to create the programDerivedAddress
         * @param idempotent            whether the creation should be idempotent
         * @return this {@code AssociatedTokenProgramFactory} instance
         */
        public AssociatedTokenProgramFactory createAssociatedTokenAccount(
                final ProgramDerivedAddress programDerivedAddress,
                final PublicKey mint,
                final PublicKey owner,
                final PublicKey payer,
                final PublicKey programId,
                final boolean idempotent)
        {
            tb.append(AssociatedTokenProgram.createAssociatedTokenAccount(programDerivedAddress, mint, owner, payer, programId, idempotent));
            return this;
        }
    }

    /**
     * Creates a new associated token account.
     *
     * @param programDerivedAddress the program derived address
     * @param mint                  the public key of the mint
     * @param owner                 the public key of the owner
     * @param payer                 the public key of the payer
     * @param programId             the public key of the token program used to create the programDerivedAddress
     * @param idempotent            whether the creation should be idempotent
     * @return A {@code TransactionInstruction} representing the created instruction
     */
    public static TransactionInstruction createAssociatedTokenAccount(
            final ProgramDerivedAddress programDerivedAddress,
            final PublicKey mint,
            final PublicKey owner,
            final PublicKey payer,
            final PublicKey programId,
            final boolean idempotent)
    {
        return Solana.instruction(ib -> ib
                .program(ASSOCIATED_TOKEN_PROGRAM_ACCOUNT)
                .account(payer, true, true)
                .account(programDerivedAddress.address(), false, true)
                .account(owner, false, false)
                .account(mint, false, false)
                .account(SYSTEM_PROGRAM_ACCOUNT, false, false)
                .account(programId, false, false)
                .account(RENT, false, false)
                .data(1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) (idempotent ? IDEMPOTENT_CREATE_INSTRUCTION : CREATE_INSTRUCTION)))
        );
    }

    /**
     * Derives a program address for a given owner, token program account, and mint.
     *
     * @param owner               the owner's public key; must not be null
     * @param tokenProgramAccount the token program account's public key; must not be null
     * @param mint                the mint's public key; must not be null
     * @return the derived program address based on the provided owner, token program account, and mint public keys
     * @throws NullPointerException if the owner, tokenProgramAccount, or mint is null
     */
    public static ProgramDerivedAddress deriveAddress(final PublicKey owner, final PublicKey tokenProgramAccount, final PublicKey mint)
    {
        requireNonNull(owner, "The owner public key must be specified, but was null");
        requireNonNull(tokenProgramAccount, "The token program public key must be specified, but was null");
        requireNonNull(mint, "The mint public key must be specified, but was null");

        return SolanaEncoding.deriveProgramAddress(
                List.of(owner.bytes(), tokenProgramAccount.bytes(), mint.bytes()),
                ASSOCIATED_TOKEN_PROGRAM_ACCOUNT
        );
    }
}
