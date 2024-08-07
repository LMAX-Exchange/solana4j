package com.lmax.solana4j.api;

/**
 * Interface for building signed messages in the Solana blockchain.
 * <p>
 * This interface provides methods for specifying the signer and building the signed message.
 * </p>
 */
public interface SignedMessageBuilder
{

    /**
     * Specifies the signer for the message.
     *
     * @param account the {@link PublicKey} of the account signing the message
     * @param signer  the {@link ByteBufferSigner} that performs the signing
     * @return this {@code SignedMessageBuilder} instance for method chaining
     */
    SignedMessageBuilder by(PublicKey account, ByteBufferSigner signer);

    /**
     * Builds and returns the signed message.
     *
     * @return the built {@link Message}
     */
    Message build();
}
