package com.lmax.solana4j.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Base58Test
{

    @Test
    void emptyBytesShouldReturnEmptyString()
    {
        final var emptyBytes = new byte[0];

        final var encode = Base58.encode(emptyBytes);

        assertThat(encode).isEqualTo("");
    }

    @Test
    void emptyStringShouldReturnEmptyBytes()
    {
        final var emptyString = "";

        final var decode = Base58.decode(emptyString);

        assertThat(decode).isEqualTo(new byte[0]);
    }

    @Test
    void validBase58StringShouldDecode()
    {
        final var validBase58String = "abcd";

        final var decode = Base58.decode(validBase58String);

        assertThat(decode).isEqualTo(new byte[]{100, 6, 2});
    }

    @Test
    void invalidBase58CharacterShouldThrowException()
    {
        assertThrows(IllegalArgumentException.class, () -> Base58.decode("10lL"));
    }

    @Test
    void shouldDecodeAndEncodeBackToOriginal()
    {
        final var validBase58String = "abcd";

        final var decode = Base58.decode(validBase58String);
        final var encode = Base58.encode(decode);

        assertThat(encode).isEqualTo(validBase58String);
    }

    @Test
    void shouldEncodeAndDecodeBackToOriginal()
    {
        final var bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        final var encode = Base58.encode(bytes);
        final var decode = Base58.decode(encode);

        assertThat(decode).isEqualTo(bytes);
    }

    @Test
    void leadingZerosShouldBeEncodedAsCharacter1()
    {
        final var leadingZeros = new byte[]{0, 0, 1};

        final var encode = Base58.encode(leadingZeros);

        assertThat(encode).isEqualTo("112");
    }

    @Test
    void singleZeroByteEncodedAsCharacter1()
    {
        final var singleZero = new byte[]{0};

        final var encode = Base58.encode(singleZero);

        assertThat(encode).isEqualTo("1");
    }

    @Test
    void handlesLargestSingleByteValue()
    {
        final var largestByteValue = new byte[]{(byte) 0xff};

        final var encode = Base58.encode(largestByteValue);

        assertThat(encode).isEqualTo("5Q");
    }

    @Test
    void handlesManyLargestByteValues()
    {
        final var largestByteValue = new byte[]{
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
        };

        final var encode = Base58.encode(largestByteValue);

        assertThat(encode).isEqualTo("jpXCZedGfVQ");
    }

    @Test
    void handlesLargeByteArray()
    {
        final var largeByteArray = createRandomByteArray(1000);

        final var encode = Base58.encode(largeByteArray);
        final var decode = Base58.decode(encode);

        assertThat(decode).isEqualTo(largeByteArray);
    }

    @Test
    void handlesArbitraryAsciiCharacters()
    {
        final var helloWorld = "helloWorld".getBytes(StandardCharsets.UTF_8);

        final var encode = Base58.encode(helloWorld);
        final var decode = Base58.decode(encode);

        assertThat(new String(decode, StandardCharsets.UTF_8)).isEqualTo("helloWorld");
    }

    @Test
    void handlesArbitraryNonAsciiCharacters()
    {
        final var blah = "测试".getBytes(StandardCharsets.UTF_8);

        final var encode = Base58.encode(blah);
        final var decode = Base58.decode(encode);

        assertThat(new String(decode, StandardCharsets.UTF_8)).isEqualTo("测试");
    }

    @Test
    @Disabled("Just to check the implementations do provide the same results.")
    void solana4jImplementationBase58EncodeShouldMatchBitcoinjLibrary()
    {
        for (int i = 0; i < 100000; i++)
        {
            final var randomByteArray = createRandomByteArray(32);

            final String base58EncodeSolana4jImplementation = Base58.encode(randomByteArray);
            final String base58EncodeBitcoinjImplementation = org.bitcoinj.core.Base58.encode(randomByteArray);

            assertEquals(base58EncodeSolana4jImplementation, base58EncodeBitcoinjImplementation);
        }
    }

    @Test
    @Disabled("Just to check the implementations do provide the same results.")
    void solana4jImplementationBase58DecodeShouldMatchBitcoinjLibrary()
    {
        for (int i = 0; i < 100000; i++)
        {
            final var randomString = createRandomBase58String();

            final byte[] base58DecodeSolana4jImplementation = Base58.decode(randomString);
            final byte[] base58DecodeBitcoinjImplementation = org.bitcoinj.core.Base58.decode(randomString);

            assertThat(base58DecodeSolana4jImplementation)
                    .usingDefaultElementComparator()
                    .isEqualTo(base58DecodeBitcoinjImplementation);
        }
    }

    private String createRandomBase58String()
    {
        final char[] characters = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
        final Random random = new Random();
        final char[] array = new char[10];
        for (int i = 0; i < 10; i++)
        {
            array[i] = characters[random.nextInt(characters.length)];
        }
        return new String(array);
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
}