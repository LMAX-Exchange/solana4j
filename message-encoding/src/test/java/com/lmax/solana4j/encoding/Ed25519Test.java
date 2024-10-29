package com.lmax.solana4j.encoding;

import com.lmax.solana4j.domain.TestKeyPairGenerator;
import net.i2p.crypto.eddsa.math.GroupElement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static com.lmax.solana4j.encoding.Ed25519.reverse;
import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void shouldReverseByteArray()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var reversedSample = new byte[]{90, 80, 70, 60, 50, 40, 30, 20, 10};

        final var result = reverse(sample);

        assertThat(result).containsExactly(reversedSample);
    }

    @Test
    @Disabled("Just to check the implementations do provide the same results.")
    void solana4jImplementationIsPointOnCurveShouldMatchEddsaLibrary()
    {
        for (int i = 0; i < 100000; i++)
        {
            final var randomByteArray = createRandomByteArray(32);

            final boolean onCurveEddsaLibrary = isOnCurveEddsaLibrary(randomByteArray);
            final boolean onCurveSolana4jImplementation = Ed25519.isOnCurve(randomByteArray);

            assertEquals(onCurveEddsaLibrary, onCurveSolana4jImplementation);
        }
    }

    private byte[] createRandomByteArray(final int length)
    {
        final Random random = new Random();
        final byte[] array = new byte[length];
        for (int i = 0; i < length; i++)
        {
            array[i] = (byte) random.nextInt();
        }
        return array;
    }

    private boolean isOnCurveEddsaLibrary(final byte[] publickKey)
    {
        try
        {
            final GroupElement point = ED_25519_CURVE_SPEC.getCurve().createPoint(publickKey, false);
            return point.isOnCurve();
        }
        catch (final IllegalArgumentException e)
        {
            return false;
        }
    }
}