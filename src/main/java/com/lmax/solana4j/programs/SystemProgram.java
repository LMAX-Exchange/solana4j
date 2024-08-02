package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.encoding.SysVar;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Program for managing system-level operations on the Solana blockchain.
 */
public final class SystemProgram
{
    private static final byte[] SYSTEM_PROGRAM_ID = Base58.decode("11111111111111111111111111111111");
    /**
     * The public key for the system program account.
     * <p>
     * This constant defines the public key associated with the Solana account for the system program.
     * It is set to the value returned by {@link Solana#account(byte[])} using the {@link #SYSTEM_PROGRAM_ID}.
     * </p>
     */
    public static final PublicKey SYSTEM_PROGRAM_ACCOUNT = Solana.account(SYSTEM_PROGRAM_ID);
    /**
     * The length of a nonce account in bytes.
     * <p>
     * This constant defines the fixed length of a nonce account used in Solana.
     * </p>
     */
    public static final int NONCE_ACCOUNT_LENGTH = 80;
    /**
     * The length of a mint account in bytes.
     * <p>
     * This constant defines the fixed length of a mint account used in Solana.
     * </p>
     */
    public static final int MINT_ACCOUNT_LENGTH = 82;
    /**
     * The instruction code for creating an account.
     * <p>
     * This constant defines the instruction code used to create a new account in Solana.
     * </p>
     */
    public static final int CREATE_ACCOUNT_INSTRUCTION = 0;
    /**
     * The instruction code for transferring funds.
     * <p>
     * This constant defines the instruction code used to transfer funds between accounts in Solana.
     * </p>
     */
    public static final int TRANSFER_INSTRUCTION = 2;
    /**
     * The instruction code for advancing a nonce.
     * <p>
     * This constant defines the instruction code used to advance a nonce in a nonce account in Solana.
     * </p>
     */
    public static final int ADVANCE_NONCE_INSTRUCTION = 4;
    /**
     * The instruction code for initializing a nonce account.
     * <p>
     * This constant defines the instruction code used to initialize a nonce account in Solana.
     * </p>
     */
    public static final int NONCE_INIT_INSTRUCTION = 6;

    private final TransactionBuilder tb;

    /**
     * Factory method for creating a new instance of {@code SystemProgram}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code SystemProgram}
     */
    public static SystemProgram factory(final TransactionBuilder tb)
    {
        return new SystemProgram(tb);
    }

    private SystemProgram(final TransactionBuilder tb)
    {
        this.tb = tb;
    }

    /**
     * Creates a new account.
     *
     * @param payer the public key of the payer
     * @param newAccount the public key of the new account
     * @param lamports the number of lamports to transfer to the new account
     * @param space the amount of space to allocate for the new account
     * @param program the public key of the program to associate with the new account
     * @return this {@code SystemProgram} instance
     */
    public SystemProgram createAccount(final PublicKey payer, final PublicKey newAccount, final long lamports, final long space, final PublicKey program)
    {
        tb.append(ib -> ib
                .program(SYSTEM_PROGRAM_ACCOUNT)
                .account(payer, true, true)
                .account(newAccount, true, true)
                .data(52, bb ->
                {
                    bb
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(CREATE_ACCOUNT_INSTRUCTION)
                            .putLong(lamports)
                            .putLong(space);
                    program.write(bb);
                })
        );

        return this;
    }

    /**
     * Initializes a nonce account.
     *
     * @param nonce the public key of the nonce account
     * @param authorized the public key of the authorized account
     * @return this {@code SystemProgram} instance
     */
    public SystemProgram nonceInitialize(final PublicKey nonce, final PublicKey authorized)
    {
        tb.append(ib -> ib
                .program(SYSTEM_PROGRAM_ACCOUNT)
                .account(nonce, false, true)
                .account(SysVar.RECENT_BLOCKHASHES, false, false)
                .account(SysVar.RENT, false, false)
                .data(36, bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(NONCE_INIT_INSTRUCTION);
                    authorized.write(bb);
                })
        );

        return this;
    }

    /**
     * Advances a nonce.
     *
     * @param nonce the public key of the nonce account
     * @param authorized the public key of the authorized account
     * @return this {@code SystemProgram} instance
     */
    public SystemProgram nonceAdvance(final PublicKey nonce, final PublicKey authorized)
    {
        tb.append(ib -> ib
                .program(SYSTEM_PROGRAM_ACCOUNT)
                .account(nonce, false, true)
                .account(SysVar.RECENT_BLOCKHASHES, false, false)
                .account(authorized, true, false)
                .data(4, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(ADVANCE_NONCE_INSTRUCTION))
        );

        return this;
    }

    /**
     * Transfers funds between accounts.
     *
     * @param from the public key of the account to transfer funds from
     * @param to the public key of the account to transfer funds to
     * @param lamports the amount of lamports to transfer
     * @return this {@code SystemProgram} instance
     */
    public SystemProgram transfer(final PublicKey from, final PublicKey to, final long lamports)
    {
        tb.append(ib -> ib
                .program(SYSTEM_PROGRAM_ACCOUNT)
                .account(from, true, true)
                .account(to, false, true)
                .data(12, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(TRANSFER_INSTRUCTION)
                        .putLong(lamports))
        );

        return this;
    }

    /**
     * Gets the nonce value from a nonce account's data.
     *
     * @param nonceAccountDataBytes the byte array containing the nonce account's data
     * @return the public key representing the nonce value
     */
    public static PublicKey getNonceAccountValue(final byte[] nonceAccountDataBytes)
    {
        assert nonceAccountDataBytes.length >= 72;

        final ByteBuffer nonceAccountData = ByteBuffer.allocate(nonceAccountDataBytes.length);
        nonceAccountData.put(nonceAccountDataBytes);

        final byte[] nonceValue = new byte[32];
        nonceAccountData.position(40);
        nonceAccountData.get(nonceValue, 0, 32);

        return Solana.account(nonceValue);
    }
}
