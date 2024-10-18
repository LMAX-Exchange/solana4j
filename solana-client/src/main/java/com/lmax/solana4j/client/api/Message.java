package com.lmax.solana4j.client.api;

import java.util.List;

/**
 * Represents a message in a Solana transaction.
 * A message contains all the necessary data for the transaction, including the account keys,
 * instructions to be executed, and metadata such as the recent blockhash and address table lookups.
 */
public interface Message
{
    /**
     * Returns a list of account keys used in the message.
     * The account keys are base58-encoded strings representing the accounts that are
     * involved in the transaction defined by the message.
     *
     * @return a list of base58-encoded account keys
     */
    List<String> getAccountKeys();

    /**
     * Returns the header of the message.
     * The header contains metadata about the transaction, such as the number of required
     * signatures and readonly accounts.
     *
     * @return the {@link Header} object representing the message header
     */
    Header getHeader();

    /**
     * Returns the list of instructions that are part of the message.
     * Each instruction defines an action to be executed as part of the transaction.
     *
     * @return a list of {@link Instruction} objects representing the message instructions
     */
    List<Instruction> getInstructions();

    /**
     * Returns the recent blockhash for the message.
     * The blockhash ensures the transaction is processed within a specific timeframe
     * and prevents it from being replayed.
     *
     * @return the base58-encoded string representing the recent blockhash
     */
    String getRecentBlockhash();

    /**
     * Returns the list of address table lookups used in the message.
     * Address table lookups allow the transaction to reference additional accounts
     * through lookup tables, reducing the size of the message.
     *
     * @return a list of {@link AddressTableLookup} objects representing the address table lookups
     */
    List<AddressTableLookup> getAddressTableLookups();
}