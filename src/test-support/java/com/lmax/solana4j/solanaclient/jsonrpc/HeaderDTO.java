package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.Header;

final class HeaderDTO implements Header
{
    private final int numReadonlySignedAccounts;
    private final int numReadonlyUnsignedAccounts;
    private final int numRequiredSignatures;

    @JsonCreator
    HeaderDTO(
            final @JsonProperty("numReadonlySignedAccounts") int numReadonlySignedAccounts,
            final @JsonProperty("numReadonlyUnsignedAccounts") int numReadonlyUnsignedAccounts,
            final @JsonProperty("numRequiredSignatures") int numRequiredSignatures)
    {
        this.numReadonlySignedAccounts = numReadonlySignedAccounts;
        this.numReadonlyUnsignedAccounts = numReadonlyUnsignedAccounts;
        this.numRequiredSignatures = numRequiredSignatures;
    }

    @Override
    public int getNumReadonlySignedAccounts()
    {
        return numReadonlySignedAccounts;
    }

    @Override
    public int getNumReadonlyUnsignedAccounts()
    {
        return numReadonlyUnsignedAccounts;
    }

    @Override
    public int getNumRequiredSignatures()
    {
        return numRequiredSignatures;
    }

    @Override
    public String toString()
    {
        return "Header{" +
               "numReadonlySignedAccounts=" + numReadonlySignedAccounts +
               ", numReadonlyUnsignedAccounts=" + numReadonlyUnsignedAccounts +
               ", numRequiredSignatures=" + numRequiredSignatures +
               '}';
    }
}
