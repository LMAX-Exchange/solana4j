package com.lmax.solana4j.client.api;

/**
 * Represents an object containing both the current solana feature set and version of solana core.
 */
public interface SolanaVersion
{
    /**
     * Returns the current feature set as a number.
     * Documentation for individual features is found <a href="https://docs.rs/solana-feature-set/latest/solana_feature_set/">here</a>
     *
     * @return the current feature set as a number.
     */
    long getFeatureSet();

    /**
     * Returns the version of solana core as a string.
     * This value is formatted as a semantic version number.
     *
     * @return the version of solana core as a string.
     */
    String getVersion();
}
