package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;

final class SolanaV0MessageView extends SolanaLegacyMessageView implements MessageVisitor.Version0MessageView
{
    private final int version = 0;
    private final List<MessageVisitor.AccountLookupTableView> lookupTables;

    SolanaV0MessageView(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final MessageVisitor.AccountsView accounts,
            final ByteBuffer transaction,
            final List<ByteBuffer> signatures,
            final PublicKey feePayer,
            final Blockhash recentBlockHash,
            final List<MessageVisitor.InstructionView> instructions,
            final List<MessageVisitor.AccountLookupTableView> lookupTables)
    {
        super(countAccountsSigned, countAccountsSignedReadOnly, countAccountsUnsignedReadOnly, accounts, transaction, signatures, feePayer, recentBlockHash, instructions);
        this.lookupTables = lookupTables;
    }

    @Override
    public int version()
    {
        return version;
    }

    @Override
    public List<MessageVisitor.AccountLookupTableView> lookupTables()
    {
        return lookupTables;
    }
}
