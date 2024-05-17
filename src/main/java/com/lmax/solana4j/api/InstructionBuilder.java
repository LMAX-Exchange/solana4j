package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public interface InstructionBuilder
{
    InstructionBuilder account(PublicKey account, boolean signs, boolean writes);

    InstructionBuilder program(PublicKey account);

    InstructionBuilder data(int size, Consumer<ByteBuffer> writer);

    MessageBuilder build();
}
