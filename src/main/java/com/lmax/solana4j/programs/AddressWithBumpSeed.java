package com.lmax.solana4j.programs;

public final class AddressWithBumpSeed
{
    private final byte[] lookupTableAddress;
    private final int bumpSeed;

    AddressWithBumpSeed(final byte[] lookupTableAddress, final int bumpSeed)
    {
        this.lookupTableAddress = lookupTableAddress;
        this.bumpSeed = bumpSeed;
    }

    public byte[] getLookupTableAddress()
    {
        return lookupTableAddress;
    }

    public int getBumpSeed()
    {
        return bumpSeed;
    }
}
