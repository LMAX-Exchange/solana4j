package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SolanaAccountsView implements MessageVisitor.AccountsView
{
    private final List<PublicKey> staticAccounts;
    private final List<MessageVisitor.AccountLookupTableView> lookupAccounts;

    SolanaAccountsView(final List<PublicKey> staticAccounts)
    {
        this.staticAccounts = staticAccounts;
        this.lookupAccounts = List.of();
    }

    SolanaAccountsView(final List<PublicKey> staticAccounts, final List<MessageVisitor.AccountLookupTableView> lookupAccounts)
    {
        this.staticAccounts = staticAccounts;
        this.lookupAccounts = lookupAccounts;
    }

    @Override
    public List<PublicKey> staticAccounts()
    {
        return staticAccounts;
    }

    @Override
    public List<PublicKey> allAccounts()
    {
        return Stream.concat(staticAccounts.stream(), lookupAccounts.stream().map(MessageVisitor.AccountLookupTableView::lookupAccount)).collect(Collectors.toList());
    }
}
