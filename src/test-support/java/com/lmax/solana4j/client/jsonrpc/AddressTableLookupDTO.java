package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AddressTableLookup;

import java.util.List;

final class AddressTableLookupDTO implements AddressTableLookup
{
    public final String accountKey;
    public final List<Long> writableIndexes;
    public final List<Long> readonlyIndexes;

    @JsonCreator
    AddressTableLookupDTO(
            final @JsonProperty("accountKey") String accountKey,
            final @JsonProperty("writableIndexes") List<Long> writableIndexes,
            final @JsonProperty("readonlyIndexes") List<Long> readonlyIndexes)
    {
        this.accountKey = accountKey;
        this.writableIndexes = writableIndexes;
        this.readonlyIndexes = readonlyIndexes;
    }

    public String getAccountKey()
    {
        return accountKey;
    }

    public List<Long> getWritableIndexes()
    {
        return writableIndexes;
    }

    public List<Long> getReadonlyIndexes()
    {
        return readonlyIndexes;
    }

    @Override
    public String toString()
    {
        return "AddressTableLookupDTO{" +
               "accountKey='" + accountKey + '\'' +
               ", writableIndexes=" + writableIndexes +
               ", readonlyIndexes=" + readonlyIndexes +
               '}';
    }
}
