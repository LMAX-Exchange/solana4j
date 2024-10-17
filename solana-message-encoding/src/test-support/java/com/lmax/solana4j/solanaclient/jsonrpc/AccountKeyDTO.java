package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.AccountKey;

final class AccountKeyDTO implements AccountKey
{
    private final String key;
    private final KeySource source;
    private final boolean signer;
    private final boolean writable;


    @JsonCreator
    AccountKeyDTO(
            final @JsonProperty("pubkey") String key,
            final @JsonProperty("source") KeySource source,
            final @JsonProperty("signer") boolean signer,
            final @JsonProperty("writable") boolean writable)
    {
        this.key = key;
        this.source = source;
        this.signer = signer;
        this.writable = writable;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    public KeySource getSource()
    {
        return source;
    }

    @Override
    public boolean isSigner()
    {
        return signer;
    }

    @Override
    public boolean isWritable()
    {
        return writable;
    }

    @Override
    public String toString()
    {
        return "AccountKeyDTO{" +
               "key='" + key + '\'' +
               ", source=" + source +
               ", signer=" + signer +
               ", writable=" + writable +
               '}';
    }
}
