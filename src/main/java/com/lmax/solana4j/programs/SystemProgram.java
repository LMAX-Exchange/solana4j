package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.encoding.SysVar;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public final class SystemProgram
{
    private static final byte[] SYSTEM_PROGRAM_ID = Base58.decode("11111111111111111111111111111111");
    public static final PublicKey SYSTEM_PROGRAM_ACCOUNT = Solana.account(SYSTEM_PROGRAM_ID);

    public static final int NONCE_ACCOUNT_LENGTH = 80;
    public static final int MINT_ACCOUNT_LENGTH = 82;

    private static final int CREATE_ACCOUNT_INSTRUCTION = 0;
    private static final int TRANSFER_INSTRUCTION = 2;
    private static final int ADVANCE_NONCE_INSTRUCTION = 4;
    private static final int NONCE_INIT_INSTRUCTION = 6;


    private final TransactionBuilder tb;

    public static SystemProgram factory(final TransactionBuilder tb)
    {
        return new SystemProgram(tb);
    }

    SystemProgram(final TransactionBuilder tb)
    {
        this.tb = tb;
    }

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
