package com.lmax.solana4j.util;

import com.lmax.solana4j.domain.TestKeyPair;
import org.bouncycastle.math.ec.rfc8032.Ed25519;

import java.security.SecureRandom;

public class TestKeyPairGenerator
{
    public static TestKeyPair generateSolanaKeyPair()
    {
        final var privateKey = new byte[32];
        final var publicKey = new byte[32];
        Ed25519.generatePrivateKey(new SecureRandom(), privateKey);
        Ed25519.generatePublicKey(privateKey, 0, publicKey, 0);

        return new TestKeyPair(publicKey, privateKey);
    }
}

