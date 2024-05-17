package com.lmax.solana4j.api;

import java.util.List;

public interface AddressesFromLookup
{
    void write(List<PublicKey> addresses);

    int countReadWrite();

    int countReadOnly();
}
