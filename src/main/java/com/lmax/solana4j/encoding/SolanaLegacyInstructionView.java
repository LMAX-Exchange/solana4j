package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

public class SolanaLegacyInstructionView extends SolanaInstructionView implements MessageVisitor.LegacyInstructionView
{
    private final MessageVisitor.LegacyAccountsView accountsView;

    public SolanaLegacyInstructionView(
            final int program,
            final List<Integer> accounts,
            final ByteBuffer data,
            final MessageVisitor.LegacyAccountsView accountsView)
    {
        super(program, accounts, data);
        this.accountsView = accountsView;
    }

    @Override
    public List<PublicKey> accounts()
    {
        return accountIndexes()
                .stream()
                .map(idx -> accountsView.staticAccounts().get(idx))
                .collect(Collectors.toList());
    }

    @Override
    public PublicKey program()
    {
        return accountsView.staticAccounts().get(programIndex());
    }
}
