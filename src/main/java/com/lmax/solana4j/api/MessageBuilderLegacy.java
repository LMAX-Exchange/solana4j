package com.lmax.solana4j.api;

import java.util.List;
import java.util.function.Consumer;

public interface MessageBuilderLegacy
{
    MessageBuilderLegacy instructions(Consumer<TransactionBuilder> builder);

    List<? extends TransactionInstruction>  getInstructions();

    MessageBuilderLegacy payer(PublicKey account);

    MessageBuilderLegacy recent(Blockhash blockHash);

    SealedMessageBuilder seal();
}
