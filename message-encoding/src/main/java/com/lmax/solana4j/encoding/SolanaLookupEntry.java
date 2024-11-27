package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AccountLookupEntry;
import com.lmax.solana4j.api.PublicKey;

final class SolanaLookupEntry implements AccountLookupEntry.LookupEntry
{
    private final PublicKey account;
    private final int index;

    SolanaLookupEntry(final PublicKey account, final int index)
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
