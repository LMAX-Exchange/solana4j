package com.lmax.solana4j.domain;

import java.math.BigDecimal;

public class Sol
{
    private final BigDecimal amount;

    public Sol(final BigDecimal amount)
    {
        this.amount = amount;
    }

    public long lamports()
    {
        return amount.multiply(new BigDecimal(1_000_000_000L)).longValue();
    }
}
