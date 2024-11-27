package com.lmax.solana4j.domain;

public class TestKeyPairGenerator
{
    public static TestKeyPair generateTestKeyPair()
    {
        final KeyPairGenerator.KeyPair keyPair = KeyPairGenerator.generateKeyPair();

        return new TestKeyPair(keyPair.getPublicKey(), keyPair.getPrivateKey());
    }
}