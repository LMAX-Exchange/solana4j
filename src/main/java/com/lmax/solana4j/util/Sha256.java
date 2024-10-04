package com.lmax.solana4j.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating SHA-256 hashes.
 * <p>
 * This class provides a method to generate a SHA-256 hash from a given byte array.
 * SHA-256 is a cryptographic hash function that produces a 256-bit (32-byte) hash value.
 * It's commonly used for data integrity and security purposes.
 * </p>
 */
public class Sha256
{

    /**
     * Computes the SHA-256 hash of the input byte array.
     *
     * @param input the byte array to be hashed
     * @return a byte array containing the SHA-256 hash of the input
     * @throws RuntimeException if the SHA-256 algorithm is not available
     */
    public static byte[] hash(final byte[] input)
    {
        final MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
        return digest.digest(input);
    }
}
