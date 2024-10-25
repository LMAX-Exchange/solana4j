package com.lmax.solana4j.domain;

import java.math.BigDecimal;

public class Sol
{
    public static long lamports(final BigDecimal solAmount)
    {
        return solAmount.multiply(new BigDecimal(1_000_000_000L)).longValue();
    }
}
