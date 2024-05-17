package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressesFromLookup;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;
import java.util.Objects;

final class SolanaAddressesFromLookup implements AddressesFromLookup
{
    private final List<PublicKey> writable;
    private final List<PublicKey> readOnly;

    public SolanaAddressesFromLookup(final List<PublicKey> writable, final List<PublicKey> readOnly)
    {
        this.writable = writable;
        this.readOnly = readOnly;
    }

    @Override
    public void write(final List<PublicKey> addresses)
    {
        addresses.addAll(writable);
        addresses.addAll(readOnly);
    }

    @Override
    public int countReadWrite()
    {
        return writable.size();
    }

    @Override
    public int countReadOnly()
    {
        return readOnly.size();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final SolanaAddressesFromLookup that = (SolanaAddressesFromLookup) o;
        return Objects.equals(writable, that.writable) && Objects.equals(readOnly, that.readOnly);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(writable, readOnly);
    }
}
