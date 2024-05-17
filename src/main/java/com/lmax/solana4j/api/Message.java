package com.lmax.solana4j.api;

public interface Message
{
    <T> T accept(MessageVisitor<T> visitor);
}
