package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.List;

final class SolanaV0AccountsView implements MessageVisitor.V0AccountsView
{
    private final List<PublicKey> staticAccounts;
    private final List<MessageVisitor.AddressLookupView> addressLookups;

    SolanaV0AccountsView(final List<PublicKey> staticAccounts, final List<MessageVisitor.AddressLookupView> addressLookups)
    {
        this.staticAccounts = staticAccounts;
        this.addressLookups = addressLookups;
    }

    @Override
    public List<PublicKey> staticAccounts()
    {
        return staticAccounts;
    }

    @Override
    public List<PublicKey> accounts(final List<AddressLookupTable> addressLookupTables)
    {
        final List<PublicKey> allAccounts = new ArrayList<>(staticAccounts);
        for (final MessageVisitor.AddressLookupView addressLookup : addressLookups)
        {
            final AddressLookupTable addressLookupTable = findAddressLookupTable(addressLookup, addressLookupTables);

            for (final int readWriteIndex : addressLookup.readWriteTableIndexes())
            {
                allAccounts.add(addressLookupTable.getAddresses().get(readWriteIndex));
            }
            for (final int readOnlyIndex : addressLookup.readOnlyTableIndexes())
            {
                allAccounts.add(addressLookupTable.getAddresses().get(readOnlyIndex));
            }
        }
        return allAccounts;
    }

    private AddressLookupTable findAddressLookupTable(
            final MessageVisitor.AddressLookupView addressLookup,
            final List<AddressLookupTable> addressLookupTables)
    {
        return addressLookupTables
                .stream()
                .filter(addressLookupTable -> addressLookupTable.getLookupTableAddress().equals(addressLookup.lookupAccount()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The address lookup tables provided do not contain an address" +
                        "lookup table present in the message."));
    }
}
