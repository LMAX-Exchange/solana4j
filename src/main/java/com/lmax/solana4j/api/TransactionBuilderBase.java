package com.lmax.solana4j.api;

import java.util.function.Consumer;

public interface TransactionBuilderBase
{
    LegacyTransactionBuilder append(Consumer<InstructionBuilder> builder);
}
