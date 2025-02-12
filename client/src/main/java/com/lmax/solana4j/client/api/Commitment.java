package com.lmax.solana4j.client.api;

/**
 * Represents the commitment level of a transaction or query on the blockchain.
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
     * and incorporated onto the blockchain. This is the highest commitment level, representing
     * full certainty that the transaction will not be rolled back.
     */
    FINALIZED(2);

    private final int value;

    Commitment(final int value)
    {
        this.value = value;
    }

    /**
     * Returns the numerical representation of the commitment level.
     *
     * @return the numerical value of the commitment level
     */
    public int commitmentNumerical()
    {
        return value;
    }
}