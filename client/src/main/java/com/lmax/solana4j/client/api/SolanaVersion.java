package com.lmax.solana4j.client.api;

/**
 * Represents an object containing both the current Solana Feature Set and version of Solana Core.
 */
public interface SolanaVersion
{
    /**
     * Returns the current Feature Set as a number.
     * Documentation for individual features is found <a href="https://docs.rs/solana-feature-set/latest/solana_feature_set/">here</a>
     *
     * @return the current Feature Set as a number.
     */
    long getFeatureSet();

    /**
     * Returns the version of Solana Core as a string.
     * This value is formatted as a semantic version number.
     *
     * @return the version of Solana Core as a string.
     */
    String getVersion();
}
