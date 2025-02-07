package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.SolanaVersion;

final class SolanaVersionDTO implements SolanaVersion
{
    private final long featureSet;
    private final String version;

    @JsonCreator
    SolanaVersionDTO(
            final @JsonProperty("feature-set") long featureSet,
            final @JsonProperty("solana-core") String version)
    {
        this.featureSet = featureSet;
        this.version = version;
    }

    @Override
    @JsonProperty("feature-set")
    public long getFeatureSet()
    {
        return featureSet;
    }

    @Override
    @JsonProperty("solana-core")
    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return "SolanaVersionValueDTO{" +
               "featureSet=" + featureSet +
               ", version='" + version + '\'' +
               '}';
    }
}