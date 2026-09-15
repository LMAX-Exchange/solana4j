package com.lmax.solana4j.api;

import java.nio.BufferOverflowException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for building V1 messages for the blockchain.
 *
 * <p>
 * V1 messages raise the size limit to 4,096 bytes, move resource limits into the message
 * itself via a transaction config, and do not support address lookup tables.
 * The wire format is defined by <a href="https://github.com/solana-foundation/solana-improvement-documents/blob/main/proposals/0385-transaction-v1.md">SIMD-0385</a>.
 * </p>
 */
public interface MessageBuilderV1
{

    /**
     * Sets the instructions for the V1 message.
     *
     * @param builder a {@link Consumer} that accepts a {@link TransactionBuilder} to build the instructions
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 instructions(Consumer<TransactionBuilder> builder);

    /**
     * Sets the prebuilt instructions for the V1 message.
     *
     * @param instructions a list of {@link TransactionInstruction} of prebuilt instructions
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 prebuiltInstructions(List<TransactionInstruction> instructions);

    /**
     * Sets the payer for the V1 message.
     *
     * @param account the {@link PublicKey} of the payer
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 payer(PublicKey account);

    /**
     * Sets the recent blockhash for the V1 message.
     *
     * @param blockHash the {@link Blockhash} of the recent block
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 recent(Blockhash blockHash);

    /**
     * Sets the priority fee for the V1 message.
     *
     * <p>
     * Unlike legacy and V0 where the priority fee is set via {@code SetComputeUnitPrice}
     * as micro-lamports per compute unit, in V1 it is an absolute total in lamports.
     * </p>
     *
     * @param lamports the total priority fee in lamports
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 priorityFee(long lamports);

    /**
     * Sets the compute unit limit for the V1 message.
     *
     * <p>
     * Unlike legacy and V0 where the compute unit limit defaults to 200,000 per instruction
     * (capped at 1.4M), in V1 it must be set explicitly or the transaction will fail.
     * </p>
     *
     * <p>
     * Note that while the wire format makes this field optional (an unset bit means a requested
     * compute unit limit of zero, i.e. a transaction that cannot execute), this library enforces
     * its presence at {@link #seal()} time with an {@code IllegalStateException} to prevent
     * building transactions that request no compute units. Non-positive limits are likewise
     * rejected by {@code computeUnitLimit} itself.
     * </p>
     *
     * @param units the compute unit limit
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 computeUnitLimit(int units);

    /**
     * Sets the loaded accounts data size limit for the V1 message.
     *
     * <p>
     * Unlike legacy and V0 where the default is 64 MiB, in V1 it must be set explicitly
     * or the transaction will fail with {@code MaxLoadedAccountsDataSizeExceeded}.
     * </p>
     *
     * <p>
     * Note that while the wire format makes this field optional (an unset bit means a requested
     * limit of zero, which the network treats as reserving 32 KiB for the cost model), this
     * library enforces its presence at {@link #seal()} time with an {@code IllegalStateException}.
     * An explicit value of zero is permitted and expresses that minimal budget.
     * </p>
     *
     * @param bytes the loaded accounts data size limit in bytes
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 loadedAccountsDataSizeLimit(int bytes);

    /**
     * Sets the requested heap size for the V1 message.
     *
     * <p>
     * The heap size must be a multiple of 1 KiB in the inclusive range [32 KiB, 256 KiB],
     * as required by the V1 transaction format; values outside of these bounds are rejected
     * with an {@code IllegalStateException} by this library rather than by the network.
     * </p>
     *
     * @param bytes the requested heap size in bytes
     * @return this {@code MessageBuilderV1} instance for method chaining
     */
    MessageBuilderV1 requestedHeapSize(int bytes);

    /**
     * Seals the V1 message and returns a sealed message builder.
     *
     * <p>
     * This method finalizes the construction of a V1 message, ensuring that all required data is properly
     * written into the buffer. It then returns a {@link SealedMessageBuilder} instance representing the sealed message.
     * </p>
     *
     * @return a {@link SealedMessageBuilder} instance representing the sealed message
     * @throws BufferOverflowException if there is insufficient space in the buffer to write the complete
     *                                 message data. This can occur if the buffer size is smaller than required
     *                                 to hold all the data necessary for the message.
     */
    SealedMessageBuilder seal() throws BufferOverflowException;

}
