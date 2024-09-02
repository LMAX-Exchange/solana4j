package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTableEntrys;
import com.lmax.solana4j.api.PublicKey;

final class SolanaAddressLookupTableEntry implements AddressLookupTableEntrys.LookupTableEntry
{
    private final PublicKey account;
    private final int index;

    SolanaAddressLookupTableEntry(final PublicKey account, final int index)
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
    public int getIndex()
    {
        return index;
    }
}
