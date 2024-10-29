package com.lmax.solana4j.domain;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.encoding.SolanaEncoding;

public class TestKeyPair
{
    private final byte[] publicKey;
    private final byte[] privateKey;

    public TestKeyPair(final byte[] publicKeyBytes, final byte[] privateKeyBytes)
    {
        this.publicKey = publicKeyBytes;
        this.privateKey = privateKeyBytes;
    }

    public TestKeyPair(final String publicKey, final String privateKey)
    {
        this.publicKey = SolanaEncoding.decodeBase58(publicKey);
        this.privateKey = SolanaEncoding.decodeBase58(privateKey);
    }

    public byte[] getPublicKeyBytes()
    {
        return publicKey;
    }

    public PublicKey getSolana4jPublicKey()
    {
        return Solana.account(publicKey);
    }

    public byte[] getPrivateKeyBytes()
    {
        return privateKey;
    }

    public TestPublicKey getPublicKey()
    {
        return new TestPublicKey(publicKey);
    }
}
