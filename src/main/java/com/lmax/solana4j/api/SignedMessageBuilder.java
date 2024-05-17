package com.lmax.solana4j.api;

public interface SignedMessageBuilder
{
    SignedMessageBuilder by(PublicKey account, ByteBufferSigner signer);

    Message build();
}
