package com.lmax.solana4j.api;

import java.nio.BufferOverflowException;
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
     * Sets the instructions for the legacy message.
     *
     * @param instructions a list of {@link TransactionInstruction} of prebuilt instructions
     * @return this {@code MessageBuilderV0} instance for method chaining
     */
    MessageBuilderV0 prebuiltInstructions(List<TransactionInstruction> instructions);

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
     * <p>
     * This method finalizes the construction of a version 0 message, ensuring that all required data is properly
     * written into the buffer. It then returns a {@link SealedMessageBuilder} instance representing the sealed message.
     * </p>
     *
     * @return a {@link SealedMessageBuilder} instance representing the sealed message
     * @throws BufferOverflowException if there is insufficient space in the buffer to write the complete message
     *                                 data. This can occur if the buffer size is smaller than required to hold
     *                                 all the data necessary for the message.
     */
    SealedMessageBuilder seal() throws BufferOverflowException;

}
