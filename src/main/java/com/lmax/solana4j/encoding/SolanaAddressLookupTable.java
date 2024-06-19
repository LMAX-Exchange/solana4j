package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;

final class SolanaAddressLookupTable implements AddressLookupTable
{
    final PublicKey lookupTableAddress;
    final List<PublicKey> lookupAddresses;

    SolanaAddressLookupTable(final PublicKey lookupTableAddress, final List<PublicKey> lookupAddresses)
    {
        this.lookupTableAddress = lookupTableAddress;
        this.lookupAddresses = lookupAddresses;
    }

    @Override
    public PublicKey getLookupTableAddress()
    {
        return lookupTableAddress;
    }

    @Override
    public List<PublicKey> getAddressLookups()
    {
        return lookupAddresses;
    }
}
