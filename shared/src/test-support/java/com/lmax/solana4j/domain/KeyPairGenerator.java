package com.lmax.solana4j.domain;

import org.bouncycastle.math.ec.rfc8032.Ed25519;

import java.security.SecureRandom;

public class KeyPairGenerator
{
    public static KeyPair generateKeyPair()
    {
        final var privateKey = new byte[32];
        final var publicKey = new byte[32];
        Ed25519.generatePrivateKey(new SecureRandom(), privateKey);
        Ed25519.generatePublicKey(privateKey, 0, publicKey, 0);

        return new KeyPair(publicKey, privateKey);
    }

    public static final class KeyPair
    {
        private final byte[] publicKey;
        private final byte[] privateKey;

        public KeyPair(final byte[] publicKey, final byte[] privateKey)
        {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public byte[] getPublicKey()
        {
            return publicKey;
        }

        public byte[] getPrivateKey()
        {
            return privateKey;
        }
    }
}
