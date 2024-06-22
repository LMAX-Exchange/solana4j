package com.lmax.solana4j.api;

import java.util.function.Consumer;

public interface MessageBuilderLegacy
{
    MessageBuilderLegacy instructions(Consumer<TransactionBuilder> builder);

    MessageBuilderLegacy payer(PublicKey account);

    MessageBuilderLegacy recent(Blockhash blockHash);

    SealedMessageBuilder seal();
}
