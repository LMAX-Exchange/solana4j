package com.lmax.solana4j.util;

import java.util.Arrays;

/**
 * Utility class for encoding and decoding Base58.
 * <p>
 * Base58 is commonly used in cryptocurrency systems like Bitcoin to encode large numbers into a shorter, human-readable string format.
 * It avoids characters that can cause confusion, such as '0' (zero), 'O' (capital o), 'l' (lowercase L), and 'I' (capital I).
 * </p>
 */
public final class Base58
{
    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final char ENCODED_ZERO = ALPHABET[0];
    private static final int[] INDEXES = new int[128];

    static
    {
        Arrays.fill(INDEXES, -1);
        for (int i = 0; i < ALPHABET.length; i++)
        {
            INDEXES[ALPHABET[i]] = i;
        }
    }

    private Base58()
    {
    }

    /**
     * Encodes the given byte array as a Base58 string.
     * <p>
     * The input byte array is treated as a large integer, and is encoded into a Base58 string using the Base58 alphabet.
     * Leading zeros in the byte array are preserved as '1' characters in the resulting Base58 string.
     * </p>
     *
     * @param input the bytes to encode
     * @return the Base58-encoded string
     */
    public static String encode(final byte[] input)
    {
        if (input.length == 0)
        {
            return "";
        }

        int zeros = 0;
        while (zeros < input.length && input[zeros] == 0)
        {
            ++zeros;
        }

        final byte[] inputCopy = Arrays.copyOf(input, input.length);

        char[] encoded = new char[inputCopy.length * 2];
        int outputStart = encoded.length;

        for (int inputStart = zeros; inputStart < inputCopy.length; )
        {
            encoded[--outputStart] = ALPHABET[divmod(inputCopy, inputStart, 256, 58)];
            if (inputCopy[inputStart] == 0)
            {
                ++inputStart;
            }
        }

        while (outputStart < encoded.length && encoded[outputStart] == ENCODED_ZERO)
        {
            ++outputStart;
        }

        while (--zeros >= 0)
        {
            encoded[--outputStart] = ENCODED_ZERO;
        }

        return new String(encoded, outputStart, encoded.length - outputStart);
    }

    /**
     * Decodes the given Base58 string into a byte array.
     * <p>
     * The input Base58 string is treated as a large integer, and decoded into a byte array.
     * Leading '1' characters in the Base58 string are treated as leading zeros in the byte array.
     * </p>
     * @param input the Base58 string to decode
     * @return the decoded byte array
     * @throws IllegalArgumentException if the input contains invalid Base58 characters
     */
    public static byte[] decode(final String input) throws IllegalArgumentException
    {
        if (input.isEmpty())
        {
            return new byte[0];
        }

        byte[] input58 = new byte[input.length()];
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            int digit = (c < 128) ? INDEXES[c] : -1;
            if (digit < 0)
            {
                throw new IllegalArgumentException("Invalid character " + c + " at index " + i);
            }
            input58[i] = (byte) digit;
        }

        int zeros = 0;
        while (zeros < input58.length && input58[zeros] == 0)
        {
            ++zeros;
        }

        byte[] decoded = new byte[input.length()];
        int outputStart = decoded.length;

        for (int inputStart = zeros; inputStart < input58.length; )
        {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256);
            if (input58[inputStart] == 0)
            {
                ++inputStart;
            }
        }

        while (outputStart < decoded.length && decoded[outputStart] == 0)
        {
            ++outputStart;
        }

        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
    }

    private static byte divmod(final byte[] number, final int firstDigit, final int base, final int divisor)
    {
        int remainder = 0;
        for (int i = firstDigit; i < number.length; i++)
        {
            int digit = (int) number[i] & 0xFF;
            int temp = remainder * base + digit;
            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }
        return (byte) remainder;
    }
}
