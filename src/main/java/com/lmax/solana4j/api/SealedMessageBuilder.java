package com.lmax.solana4j.api;

public interface SealedMessageBuilder
{
    SignedMessageBuilder signed();

    UnsignedMessageBuilder unsigned();
}
