package com.lmax.solana4j.domain;

import com.lmax.solana4j.Solana;
import org.bitcoinj.core.Base58;

public class TestPublicKey
{
    private final byte[] publicKey;

    public TestPublicKey(final byte[] publicKey)
    {
        this.publicKey = publicKey;
    }

    public String getPublicKeyBase58()
    {
        return Base58.encode(publicKey);
    }

    public com.lmax.solana4j.api.PublicKey getSolana4jPublicKey()
    {
        return Solana.account(publicKey);
    }

    public byte[] getPublicKeyBytes()
    {
        return publicKey;
    }
}
