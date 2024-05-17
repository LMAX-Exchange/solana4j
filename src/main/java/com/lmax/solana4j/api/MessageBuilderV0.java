package com.lmax.solana4j.api;

import java.util.List;
import java.util.function.Consumer;

public interface MessageBuilderV0
{
    MessageBuilderV0 instructions(Consumer<VersionedTransactionBuilder> builder);

    MessageBuilderV0 payer(PublicKey account);

    MessageBuilderV0 recent(Blockhash blockHash);

    MessageBuilderV0 lookups(List<AddressLookupTable> accountLookups);

    SealedMessageBuilder seal();
}
