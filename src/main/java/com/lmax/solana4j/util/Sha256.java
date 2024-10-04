package com.lmax.solana4j.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256
{
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
