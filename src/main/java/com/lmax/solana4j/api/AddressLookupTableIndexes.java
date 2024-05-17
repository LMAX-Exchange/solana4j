package com.lmax.solana4j.api;

import java.util.List;

public interface AddressLookupTableIndexes
{
    void addReadOnlyIndex(PublicKey address, int readOnlyIndex);

    void addReadWriteIndex(PublicKey address, int readWriteIndex);

    List<PublicKey> getAddresses();

    PublicKey getLookupTableAddress();

    List<AddressLookupIndex> getReadWriteAddressIndexes();

    List<AddressLookupIndex> getReadOnlyAddressIndexes();

    interface AddressLookupIndex
    {
        PublicKey getAddress();

        int getAddressIndex();
    }
}
