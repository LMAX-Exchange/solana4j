package com.lmax.solana4j.api;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for building version 0 messages in the Solana blockchain.
 * <p>
 * This interface provides methods for setting instructions, specifying the payer, setting the recent blockhash,
 * adding account lookups, and sealing the message.
 * </p>
 */
public interface MessageBuilderV0
{

    /**
     * Sets the instructions for the version 0 message.
     *
     * @param builder a {@link Consumer} that accepts a {@link TransactionBuilder} to build the instructions
     * @return this {@code MessageBuilderV0} instance for method chaining
     */
    MessageBuilderV0 instructions(Consumer<TransactionBuilder> builder);

    /**
     * Sets the payer for the version 0 message.
     *
     * @param account the {@link PublicKey} of the payer
     * @return this {@code MessageBuilderV0} instance for method chaining
     */
    MessageBuilderV0 payer(PublicKey account);

    /**
     * Sets the recent blockhash for the version 0 message.
     *
     * @param blockHash the {@link Blockhash} of the recent block
     * @return this {@code MessageBuilderV0} instance for method chaining
     */
    MessageBuilderV0 recent(Blockhash blockHash);

    /**
     * Sets the account lookups for the version 0 message.
     *
     * @param accountLookups a list of {@link AddressLookupTable} objects representing the account lookups
     * @return this {@code MessageBuilderV0} instance for method chaining
     */
    MessageBuilderV0 lookups(List<AddressLookupTable> accountLookups);

    /**
     * Seals the version 0 message and returns a sealed message builder.
     *
     * @return a {@link SealedMessageBuilder} instance representing the sealed message
     */
    SealedMessageBuilder seal();
}
