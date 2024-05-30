package com.lmax.solana4j;

public class Sol
{
    private final long amount;

    public Sol(final long amount)
    {
        this.amount = amount;
    }

    public long lamports()
    {
        return amount * 1_000_000_000L;
    }
}
