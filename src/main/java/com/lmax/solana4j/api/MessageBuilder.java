package com.lmax.solana4j.api;

public interface MessageBuilder
{
    MessageBuilderLegacy legacy();

    MessageBuilderV0 v0();
}
