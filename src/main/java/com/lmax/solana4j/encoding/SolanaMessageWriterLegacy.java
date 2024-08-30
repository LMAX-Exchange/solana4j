package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.List;

final class SolanaMessageWriterLegacy
{
    private final Accounts accounts;
    private final SolanaBlockhash recentBlockHash;
    private final List<TransactionInstruction> instructions;

    SolanaMessageWriterLegacy(
            final Accounts accounts,
            final SolanaBlockhash recentBlockHash,
            final List<TransactionInstruction> instructions
    )
    {
        this.accounts = accounts;
        this.recentBlockHash = recentBlockHash;
        this.instructions = instructions;
    }

    void write(final ByteBuffer buffer)
    {
        final var formatter = new SolanaMessageFormattingCommon(buffer);

        // reserve signatures section
        formatter.reserveSignatures(accounts.getCountSigned());

        // write legacy header section
        formatter.writeByte((byte) accounts.getCountSigned());
        formatter.writeByte((byte) accounts.getCountSignedReadOnly());
        formatter.writeByte((byte) accounts.getCountUnsignedReadOnly());

        // write accounts table section
        formatter.writeStaticAccounts(accounts.getStaticAccounts());

        // write recent-block-header or NONCE
        formatter.writeBlockHash(recentBlockHash);

        // write transaction instructions
        formatter.writeInstructions(instructions, accounts.getFlattenedAccountList()::indexOf);
    }

}
