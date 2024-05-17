package com.lmax.solana4j.api;

import java.util.List;

public interface AddressLookupTable
{
    PublicKey getLookupTableAddress();

    List<PublicKey> getAddressLookups();
}
