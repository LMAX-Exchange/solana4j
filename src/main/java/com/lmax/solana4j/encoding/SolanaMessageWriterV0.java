package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.util.List;

final class SolanaMessageWriterV0
{
    private final SolanaBlockhash recentBlockHash;
    private final List<TransactionInstruction> instructions;
    private final Accounts accounts;

    SolanaMessageWriterV0(
            final SolanaBlockhash recentBlockHash,
            final List<TransactionInstruction> instructions,
            final Accounts accounts
    )
    {
        this.recentBlockHash = recentBlockHash;
        this.instructions = instructions;
        this.accounts = accounts;
    }

    void write(final ByteBuffer buffer)
    {
        final var formatter = new SolanaMessageFormattingCommon(buffer);

        // reserve signatures section
        formatter.reserveSignatures(accounts.getCountSigned());

        // write V0 header section
        formatter.writeByte((byte) 0x80);
        formatter.writeByte((byte) accounts.getCountSigned());
        formatter.writeByte((byte) accounts.getCountSignedReadOnly());
        formatter.writeByte((byte) accounts.getCountUnsignedReadOnly());

        // write accounts table section
        formatter.writeStaticAccounts(accounts.getStaticAccounts());

        // write recent-block-header or NONCE
        formatter.writeBlockHash(recentBlockHash);

        // write transaction instructions
        formatter.writeInstructions(instructions, accounts.getFlattenedAccountList()::indexOf);

        // write lookup accounts
        formatter.writeLookupAccounts(accounts.getLookupAccounts());
    }
}
