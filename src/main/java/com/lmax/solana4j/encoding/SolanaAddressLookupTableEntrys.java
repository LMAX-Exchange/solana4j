package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTableEntrys;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SolanaAddressLookupTableEntrys implements AddressLookupTableEntrys
{
    private final PublicKey addressLookupTable;
    private final List<LookupTableEntry> readWriteEntrys;
    private final List<LookupTableEntry> readOnlyEntrys;

    SolanaAddressLookupTableEntrys(final PublicKey addressLookupTable)
    {
        this.addressLookupTable = addressLookupTable;
        this.readWriteEntrys = new ArrayList<>();
        this.readOnlyEntrys = new ArrayList<>();
    }

    @Override
    public void addReadOnlyEntry(final PublicKey address, final int readOnlyIndex)
    {
        readOnlyEntrys.add(new SolanaAddressLookupTableEntry(address, readOnlyIndex));
    }

    @Override
    public void addReadWriteEntry(final PublicKey address, final int readWriteIndex)
    {
        readWriteEntrys.add(new SolanaAddressLookupTableEntry(address, readWriteIndex));
    }

    @Override
    public List<PublicKey> getAddresses()
    {
        return Stream.concat(readWriteEntrys.stream().map(LookupTableEntry::getAddress), readOnlyEntrys.stream().map(LookupTableEntry::getAddress)).collect(Collectors.toList());
    }

    @Override
    public PublicKey getLookupTableAddress()
    {
        return addressLookupTable;
    }

    @Override
    public List<LookupTableEntry> getReadWriteAddressEntrys()
    {
        return readWriteEntrys;
    }

    @Override
    public List<LookupTableEntry> getReadOnlyAddressEntrys()
    {
        return readOnlyEntrys;
    }
}
