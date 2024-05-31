package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.PublicKey;

final class SolanaDestination implements Destination
{
    private final PublicKey destination;
    private final long amount;

    SolanaDestination(final PublicKey destination, final long amount)
    {
        this.destination = destination;
        this.amount = amount;
    }

    @Override
    public PublicKey getDestination()
    {
        return destination;
    }

    @Override
    public long getAmount()
    {
        return amount;
    }
}
