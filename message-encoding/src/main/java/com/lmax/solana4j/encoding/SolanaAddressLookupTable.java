package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;

final class SolanaAddressLookupTable implements AddressLookupTable
{
    private final PublicKey lookupTableAddress;
    private final List<PublicKey> addresses;

    SolanaAddressLookupTable(final PublicKey lookupTableAddress, final List<PublicKey> addresses)
    {
        this.lookupTableAddress = lookupTableAddress;
        this.addresses = addresses;
    }

    @Override
    public PublicKey getLookupTableAddress()
    {
        return lookupTableAddress;
    }

    @Override
    public List<PublicKey> getAddresses()
    {
        return addresses;
    }
}
