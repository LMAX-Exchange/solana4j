package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTableIndexes;
import com.lmax.solana4j.api.PublicKey;

final class SolanaAddressLookupIndex implements AddressLookupTableIndexes.AddressLookupIndex
{
    private final PublicKey account;
    private final int index;

    SolanaAddressLookupIndex(final PublicKey account, final int index)
    {
        this.account = account;
        this.index = index;
    }

    @Override
    public PublicKey getAddress()
    {
        return account;
    }

    @Override
    public int getAddressIndex()
    {
        return index;
    }
}
