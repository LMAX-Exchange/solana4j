package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.LoadedAddresses;

import java.util.List;

public class LoadedAddressesDTO implements LoadedAddresses
{
    private final List<String> readonly;
    private final List<String> writable;

    @JsonCreator
    public LoadedAddressesDTO(final @JsonProperty("readonly") List<String> readonly, final @JsonProperty("writable") List<String> writable)
    {
        this.readonly = readonly;
        this.writable = writable;
    }

    @Override
    public List<String> getReadonly()
    {
        return readonly;
    }

    @Override
    public List<String> getWritable()
    {
        return writable;
    }
}
