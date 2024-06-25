package com.lmax.solana4j.api;

import java.util.function.Consumer;

public interface InnerTransactionBuilder
{
    InnerTransactionBuilder instructions(Consumer<TransactionBuilder> builder);

    InnerTransactionBuilder payer(PublicKey payer);

    InnerInstructions build();
}
