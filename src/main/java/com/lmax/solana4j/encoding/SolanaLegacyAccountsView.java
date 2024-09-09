package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;

final class SolanaLegacyAccountsView implements MessageVisitor.LegacyAccountsView
{
    private final List<PublicKey> staticAccounts;

    SolanaLegacyAccountsView(final List<PublicKey> staticAccounts)
    {
        this.staticAccounts = staticAccounts;
    }

    @Override
    public List<PublicKey> staticAccounts()
    {
        return staticAccounts;
    }

}
