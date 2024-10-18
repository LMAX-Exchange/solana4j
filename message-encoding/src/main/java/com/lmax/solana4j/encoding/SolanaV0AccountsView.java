package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class SolanaV0AccountsView implements MessageVisitor.V0AccountsView
{
    private final List<PublicKey> staticAccounts;
    private final List<MessageVisitor.AccountLookupView> accountLookups;

    SolanaV0AccountsView(final List<PublicKey> staticAccounts, final List<MessageVisitor.AccountLookupView> accountLookups)
    {
        this.staticAccounts = staticAccounts;
        this.accountLookups = accountLookups;
    }

    @Override
    public List<PublicKey> staticAccounts()
    {
        return staticAccounts;
    }

    @Override
    public List<PublicKey> accounts(final List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException
    {
        final List<PublicKey> allAccounts = new ArrayList<>(staticAccounts);
        for (final MessageVisitor.AccountLookupView accountLookup : accountLookups)
        {
            final Optional<AddressLookupTable> addressLookupTable = accountLookup.findAddressLookupTable(addressLookupTables);

            if (addressLookupTable.isEmpty())
            {
                throw new IllegalArgumentException(
                        "The address lookup tables provided do not contain an address" +
                                "lookup table present in the message.");
            }
            for (final int readWriteIndex : accountLookup.readWriteTableIndexes())
            {
                allAccounts.add(addressLookupTable.get().getAddresses().get(readWriteIndex));
            }
            for (final int readOnlyIndex : accountLookup.readOnlyTableIndexes())
            {
                allAccounts.add(addressLookupTable.get().getAddresses().get(readOnlyIndex));
            }
        }
        return allAccounts;
    }
}
