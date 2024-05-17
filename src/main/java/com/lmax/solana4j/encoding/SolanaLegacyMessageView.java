package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.MessageVisitor.InstructionView;
import com.lmax.solana4j.api.MessageVisitor.LegacyMessageView;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;

class SolanaLegacyMessageView extends SolanaMessageView implements LegacyMessageView
{

    SolanaLegacyMessageView(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final MessageVisitor.AccountsView accounts,
            final ByteBuffer transaction,
            final List<ByteBuffer> signatures,
            final PublicKey feePayer,
            final Blockhash recentBlockHash,
            final List<InstructionView> instructions)
    {
        super(countAccountsSigned, countAccountsSignedReadOnly, countAccountsUnsignedReadOnly, accounts, transaction, signatures, feePayer, recentBlockHash, instructions);
    }
}
