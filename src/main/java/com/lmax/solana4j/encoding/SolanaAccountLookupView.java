package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;
import java.util.Optional;

final class SolanaAccountLookupView implements MessageVisitor.AccountLookupView
{
    private final PublicKey accountLookup;
    private final List<Integer> readWriteTableIndexes;
    private final List<Integer> readOnlyTableIndexes;

    SolanaAccountLookupView(final PublicKey accountLookup, final List<Integer> readWriteTableIndexes, final List<Integer> readOnlyTableIndexes)
    {
        this.accountLookup = accountLookup;
        this.readWriteTableIndexes = readWriteTableIndexes;
        this.readOnlyTableIndexes = readOnlyTableIndexes;
    }

    @Override
    public PublicKey accountLookup()
    {
        return accountLookup;
    }

    @Override
    public List<Integer> readWriteTableIndexes()
    {
        return readWriteTableIndexes;
    }

    @Override
    public List<Integer> readOnlyTableIndexes()
    {
        return readOnlyTableIndexes;
    }

    @Override
    public Optional<AddressLookupTable> findAddressLookupTable(final List<AddressLookupTable> addressLookupTables)
    {
        return addressLookupTables
                .stream()
                .filter(addressLookupTable -> addressLookupTable.getLookupTableAddress().equals(accountLookup))
                .findFirst();
    }
}
