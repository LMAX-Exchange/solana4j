package com.lmax.solana4j.util;

import java.math.BigInteger;

import static com.lmax.solana4j.util.ByteBufferPrimitiveArray.reverse;

/**
 * Utility class for performing Ed25519 elliptic curve operations.
 * <p>
 * This class provides a method to check whether a given public key, represented as a 32-byte array,
 * lies on the Ed25519 elliptic curve. It handles key validation by leveraging the curve equation
 * and modular arithmetic.
 * </p>
 * The Ed25519 curve equation is:
 * dx^2y^2 + x^2 = y^2 - 1 (mod P), where d is a curve constant.
 */
public class Ed25519
{
    private static final BigInteger P = new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffed", 16);
    private static final BigInteger D = new BigInteger("52036cee2b6ffe738cc740797779e89800700a4d4141d8ab75eb4dca135978a3", 16);
    private static final BigInteger PM5D8 = P.subtract(BigInteger.valueOf(5)).divide(BigInteger.valueOf(8));

    /**
     * Checks if the public key represented by a 32-byte array is on the Ed25519 curve.
     *
     * @param publicKeyBytes The 32-byte array representing the public key.
     * @return true if the point is on the curve, false otherwise.
     * @throws IllegalArgumentException if the public key byte array is not 32 bytes long.
     */
    public static boolean isOnCurve(final byte[] publicKeyBytes)
    {
        if (publicKeyBytes.length != 32)
        {
            throw new IllegalArgumentException("Public key must be 32 bytes long");
        }

        // Elliptic curve equation: dx^2y^2 + x^2 = y^2 - 1 (mod P)
        // Let x^2 = u/v where u = y^2 - 1 and v = dy^2 + 1 (mod P)
        byte[] yBytes = publicKeyBytes.clone();
        yBytes[31] &= 0x7F; // Mask the most significant bit to ensure y is positive

        // publicKey is in little endian encoding, so we must reverse the byte array before we "math"
        final BigInteger y = new BigInteger(1, reverse(yBytes));
        final BigInteger y2 = y.multiply(y).mod(P);

        final BigInteger u = y2.subtract(BigInteger.ONE).mod(P);
        final BigInteger v = D.multiply(y2).add(BigInteger.ONE).mod(P);

        final BigInteger x1 = calculateCandidateRoot(u, v);

        return isCandidateRootValid(x1, v, u);
    }

    private static BigInteger calculateCandidateRoot(final BigInteger u, final BigInteger v)
    {
        final BigInteger v3 = v.modPow(BigInteger.TEN, P).multiply(v).mod(P);
        final BigInteger v7 = v3.multiply(v3).multiply(v).mod(P);

        final BigInteger uv3 = u.multiply(v3).mod(P);

        final BigInteger uv7 = u.multiply(v7).mod(P);
        final BigInteger uv7Pm5d8 = uv7.modPow(PM5D8, P);

        // after some transformations, given the properties of the ed25519 curve we can say
        // x^2 = (u/v)
        // x1 = (u/v)^(P+3/8) where x1 is the candidate root
        // x1 = uv^3(uv^7)^(P-5/8)
        return uv3.multiply(uv7Pm5d8).mod(P);
    }

    private static boolean isCandidateRootValid(final BigInteger x1, final BigInteger v, final BigInteger u)
    {
        // given x1 = uv^3(uv^7)^(P-5/8) we can check to see if the candidate root satisfies
        // vx^2 = u (mod P) for x = x1 is a candidate root
        // vx^2 = -u (mod P) for x = x1 is a candidate root
        // if no roots then the point does not lie on the curve
        final BigInteger vx12 = v.multiply(x1.modPow(BigInteger.TWO, P)).mod(P);

        if (vx12.equals(u))
        {
            return true;
        }

        return vx12.equals(u.negate().mod(P));
    }
}
