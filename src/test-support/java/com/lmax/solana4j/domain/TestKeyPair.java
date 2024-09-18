package com.lmax.solana4j.domain;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;

public class TestKeyPair
{
    private final byte[] publicKey;
    private final byte[] privateKey;

    public TestKeyPair(final byte[] publicKey, final byte[] privateKey)
    {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKeyBase58()
    {
        return Base58.encode(publicKey);
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
