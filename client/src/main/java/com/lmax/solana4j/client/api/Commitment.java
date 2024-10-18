package com.lmax.solana4j.client.api;

/**
 * Represents the commitment level of a transaction or query on the Solana blockchain.
 * The commitment level defines how finalized the data is at the point of querying or processing.
 * The levels are ordered by the certainty of the data, from most recent (less confirmed) to fully finalized.
 */
public enum Commitment
{
    /**
     * The "Processed" commitment level indicates that the transaction has been processed,
     * but it may not have been confirmed by a sufficient number of validators.
     * This is the lowest commitment level.
     */
    PROCESSED(0),

    /**
     * The "Confirmed" commitment level indicates that the transaction has been confirmed
     * by a sufficient number of validators, but it is not fully finalized.
     */
    CONFIRMED(1),

    /**
     * The "Finalized" commitment level indicates that the transaction has been fully finalized
     * and incorporated into the Solana blockchain.
     * This is the highest commitment level, representing full certainty that the transaction
     * will not be rolled back.
     */
    FINALIZED(2);

    private final int value;

    /**
     * Constructs a Commitment enum with a numerical value.
     *
     * @param value the numerical value representing the commitment level, where a higher value
     *              indicates a more finalized level.
     */
    Commitment(final int value)
    {
        this.value = value;
    }

    /**
     * Returns the numerical representation of the commitment level.
     * A higher number indicates a greater level of finalization or certainty.
     *
     * @return the numerical value of the commitment level
     */
    public int commitmentNumerical()
    {
        return value;
    }
}