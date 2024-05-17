package com.lmax.solana4j.api;

public interface AssociatedTokenAddress extends ProgramDerivedAddress
{
    PublicKey mint();
}
