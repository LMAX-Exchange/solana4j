package com.lmax.solana4j.api;

public interface Destination
{
    PublicKey getDestination();

    long getAmount();
}
