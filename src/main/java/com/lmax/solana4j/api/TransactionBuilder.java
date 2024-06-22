package com.lmax.solana4j.api;

import java.util.function.Consumer;

public interface TransactionBuilder
{
    TransactionBuilder append(Consumer<InstructionBuilder> builder);
}
