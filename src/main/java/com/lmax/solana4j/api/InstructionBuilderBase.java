package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public interface InstructionBuilderBase
{
    InstructionBuilderBase account(PublicKey account, boolean signs, boolean writes);

    InstructionBuilderBase program(PublicKey account);

    InstructionBuilderBase data(int size, Consumer<ByteBuffer> writer);
}
