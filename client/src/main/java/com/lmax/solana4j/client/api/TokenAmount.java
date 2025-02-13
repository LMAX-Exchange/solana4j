package com.lmax.solana4j.client.api;

/**
 * Represents the amount of an spl token in a token account on the blockchain.
 */
public interface TokenAmount
{
    /**
     * Returns the raw token amount as a string.
     * The raw amount represents the token balance without applying any decimal places.
     *
     * @return the raw token amount as a string
     */
    String getAmount();

    /**
     * Returns the number of decimal places for the token.
     * This value is used to scale the raw amount into a user-friendly format.
     *
     * @return the number of decimal places for the token
     */
    long getDecimals();

    /**
     * Returns the token amount as a float, adjusted for the token's decimals.
     * This is the user-friendly representation of the token amount.
     *
     * @return the token amount as a float, adjusted for decimals
     */
    float getUiAmount();

    /**
     * Returns the token amount as a string, adjusted for the token's decimals.
     * This provides a user-friendly, string-based representation of the token amount.
     *
     * @return the token amount as a string, adjusted for decimals
     */
    String getUiAmountString();
}