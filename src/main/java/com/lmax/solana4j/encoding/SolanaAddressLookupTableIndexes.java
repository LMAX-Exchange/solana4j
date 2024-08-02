package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTableIndexes;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SolanaAddressLookupTableIndexes implements AddressLookupTableIndexes
{
    private final PublicKey account;
    private final List<AddressLookupIndex> readWriteAccountIndexes;
    private final List<AddressLookupIndex> readOnlyAccountIndexes;

    SolanaAddressLookupTableIndexes(final PublicKey account)
    {
        this.account = account;
        this.readWriteAccountIndexes = new ArrayList<>();
        this.readOnlyAccountIndexes = new ArrayList<>();
    }

    @Override
    public void addReadOnlyIndex(final PublicKey address, final int readOnlyIndex)
    {
        readOnlyAccountIndexes.add(new SolanaAddressLookupIndex(address, readOnlyIndex));
    }

    @Override
    public void addReadWriteIndex(final PublicKey address, final int readWriteIndex)
    {
        readWriteAccountIndexes.add(new SolanaAddressLookupIndex(address, readWriteIndex));
    }

    @Override
    public List<PublicKey> getAddresses()
    {
        return Stream.concat(readWriteAccountIndexes.stream().map(AddressLookupIndex::getAddress), readOnlyAccountIndexes.stream().map(AddressLookupIndex::getAddress)).collect(Collectors.toList());
    }

    @Override
    public PublicKey getLookupTableAddress()
    {
        return account;
    }

    @Override
    public List<AddressLookupIndex> getReadWriteAddressIndexes()
    {
        return readWriteAccountIndexes;
    }

    @Override
    public List<AddressLookupIndex> getReadOnlyAddressIndexes()
    {
        return readOnlyAccountIndexes;
    }
}
