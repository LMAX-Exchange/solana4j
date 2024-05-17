package com.lmax.solana4j.api;

import java.util.function.Consumer;

public interface TransactionBuilder
{
    void legacy(Consumer<LegacyTransactionBuilder> builder);

    void versioned(int version, Consumer<VersionedTransactionBuilder> builder);

}
