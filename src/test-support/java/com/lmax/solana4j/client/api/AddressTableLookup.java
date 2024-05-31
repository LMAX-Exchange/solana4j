package com.lmax.solana4j.client.api;

import java.util.List;

public interface AddressTableLookup
{
    String getAccountKey();

    List<Long> getWritableIndexes();

    List<Long> getReadonlyIndexes();
}
