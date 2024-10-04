package com.lmax.solana4j.util;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ed25519Test
{
    @Test
    void yEquals1IsAValidPointOnTheCurve()
    {
        final var yEqualsOne = new byte[]
                {
                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0
                };

        assertTrue(Ed25519.isOnCurve(yEqualsOne));
    }

    @Test
    void yEquals0IsAValidPointOnTheCurve()
    {
        final var yEquals0 = new byte[]
                {
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0
                };

        assertTrue(Ed25519.isOnCurve(yEquals0));
    }

    @Test
    void yEquals4DividedBy5IsAValidPointOnTheCurve()
    {
        // this is a base point - y = 4/5 (mod P)
        final var yEquals4DividedBy5 = new BigInteger("58ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);

        assertTrue(Ed25519.isOnCurve(yEquals4DividedBy5.toByteArray()));
    }

    @Test
    void pointNotOnCurve()
    {
        final var arbitraryPoint = new byte[]
                {
                        1, 5, 9, 3, 6, 9, 3, 6, 9, 3,
                        2, 6, 1, 4, 7, 1, 4, 7, 1, 4,
                        3, 7, 2, 5, 8, 2, 5, 8, 2, 5,
                        4, 8
                };

        assertFalse(Ed25519.isOnCurve(arbitraryPoint));
    }

    @Test
    void generatedPublicKeyValidPointOnTheCurve()
    {
        final var pointOnCurve = TestKeyPairGenerator.generateSolanaKeyPair().getPublicKeyBytes();

        assertTrue(Ed25519.isOnCurve(pointOnCurve));
    }

    @Test
    void shouldThrowExceptionForInvalidPublicKeyLength()
    {
        final var invalidPublicKey = new byte[] { 0 };

        assertThrows(RuntimeException.class, () -> Ed25519.isOnCurve(invalidPublicKey));
    }
}