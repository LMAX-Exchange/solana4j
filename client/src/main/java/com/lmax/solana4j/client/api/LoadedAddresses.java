package com.lmax.solana4j.client.api;

import java.util.List;

public interface LoadedAddresses
{
    List<String> getReadonly();

    List<String> getWritable();
}
