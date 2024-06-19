package com.lmax.solana4j.api;

import java.nio.ByteBuffer;

public interface Slot
{
    void write(ByteBuffer buffer);
    byte[] bytes();
}
