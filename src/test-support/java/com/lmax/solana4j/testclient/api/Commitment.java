package com.lmax.solana4j.testclient.api;

public enum Commitment
{
    // Order is very important here
    PROCESSED(0),
    CONFIRMED(1),
    FINALIZED(2);

    private final int value;

    Commitment(final int value)
    {
        this.value = value;
    }

    public int commitmentNumerical()
    {
        return value;
    }
}
