package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressesFromLookup;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SolanaAccountsView implements MessageVisitor.AccountsView
{
    private final List<PublicKey> staticAccounts;
    private final int countReadWriteLookups;
    private final int countReadOnlyLookups;

    public SolanaAccountsView(final List<PublicKey> staticAccounts, final int countReadWriteLookups, final int countReadOnlyLookups)
    {
        this.staticAccounts = staticAccounts;
        this.countReadWriteLookups = countReadWriteLookups;
        this.countReadOnlyLookups = countReadOnlyLookups;
    }

    @Override
    public List<PublicKey> staticAccounts()
    {
        return staticAccounts;
    }

    @Override
    public List<PublicKey> allAccounts(final AddressesFromLookup addressesFromLookup)
    {
        if(countReadOnlyLookups != addressesFromLookup.countReadOnly() || countReadWriteLookups != addressesFromLookup.countReadWrite())
        {
            throw new IllegalArgumentException("Cannot evaluate accounts, missing account lookup values");
        }

        final List<PublicKey> accounts = new ArrayList<>();
        accounts.addAll(staticAccounts);
        addressesFromLookup.write(accounts);

        return accounts;
    }

    @Override
    public List<PublicKey> allAccounts()
    {
        return allAccounts(new SolanaAddressesFromLookup(Collections.emptyList(), Collections.emptyList()));
    }
}
