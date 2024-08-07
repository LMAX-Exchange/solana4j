package com.lmax.solana4j.api;

/**
 * Interface representing a message in the Solana blockchain.
 * <p>
 * A message in the Solana blockchain contains the transaction instructions, accounts, and other metadata.
 * </p>
 */
public interface Message
{

    /**
     * Accepts a visitor to process the message.
     *
     * @param visitor the {@link MessageVisitor} that processes the message
     * @param <T>     the type of the result produced by the visitor
     * @return the result produced by the visitor
     */
    <T> T accept(MessageVisitor<T> visitor);
}
