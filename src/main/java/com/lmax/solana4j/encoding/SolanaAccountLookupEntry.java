package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AccountLookupEntry;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SolanaAccountLookupEntry implements AccountLookupEntry
{
    private final PublicKey addressLookupTable;
    private final List<LookupEntry> readWriteEntrys;
    private final List<LookupEntry> readOnlyEntrys;

    SolanaAccountLookupEntry(final PublicKey addressLookupTable)
    {
        this.addressLookupTable = addressLookupTable;
        this.readWriteEntrys = new ArrayList<>();
        this.readOnlyEntrys = new ArrayList<>();
    }

    @Override
    public void addReadOnlyEntry(final PublicKey address, final int readOnlyIndex)
    {
        readOnlyEntrys.add(new SolanaLookupEntry(address, readOnlyIndex));
    }

    @Override
    public void addReadWriteEntry(final PublicKey address, final int readWriteIndex)
    {
        readWriteEntrys.add(new SolanaLookupEntry(address, readWriteIndex));
    }

    @Override
    public List<PublicKey> getAddresses()
    {
        return Stream.concat(
                        readWriteEntrys.stream().map(LookupEntry::getAddress),
                        readOnlyEntrys.stream().map(LookupEntry::getAddress)
                ).collect(Collectors.toList());
    }

    @Override
    public PublicKey getLookupTableAddress()
    {
        return addressLookupTable;
    }

    @Override
    public List<LookupEntry> getReadWriteLookupEntrys()
    {
        return readWriteEntrys;
    }

    @Override
    public List<LookupEntry> getReadOnlyLookupEntrys()
    {
        return readOnlyEntrys;
    }
}
