package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilderBase;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;

import static com.lmax.solana4j.encoding.SysVar.RENT;
import static com.lmax.solana4j.programs.SystemProgram.SYSTEM_PROGRAM_ACCOUNT;

public final class AssociatedTokenProgram
{
    private static final byte[] ASSOCIATED_TOKEN_PROGRAM_ID = Base58.decode("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");
    public static final PublicKey ASSOCIATED_TOKEN_PROGRAM_ACCOUNT = Solana.account(ASSOCIATED_TOKEN_PROGRAM_ID);

    public static final int IDEMPOTENT_CREATE_INSTRUCTION = 1;
    public static final int CREATE_INSTRUCTION = 0;

    private final TransactionBuilderBase tb;

    public static AssociatedTokenProgram factory(final TransactionBuilderBase tb)
    {
        return new AssociatedTokenProgram(tb);
    }

    private AssociatedTokenProgram(final TransactionBuilderBase tb)
    {
        this.tb = tb;
    }

    public AssociatedTokenProgram createAssociatedToken(
            final ProgramDerivedAddress programDerivedAddress,
            final PublicKey mint,
            final PublicKey owner,
            final PublicKey payer,
            final boolean idempotent)
    {
        tb.append(ib -> ib
                .program(ASSOCIATED_TOKEN_PROGRAM_ACCOUNT)
                .account(payer, true, true)
                .account(programDerivedAddress.address(), false, true)
                .account(owner, false, false)
                .account(mint, false, false)
                .account(SYSTEM_PROGRAM_ACCOUNT, false, false)
                .account(ASSOCIATED_TOKEN_PROGRAM_ACCOUNT, false, false)
                .account(RENT, false, false)
                .data(1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) (idempotent ? IDEMPOTENT_CREATE_INSTRUCTION : CREATE_INSTRUCTION)))
        );

        return this;
    }
}
