package com.lmax.solana4j.encoding;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class SolanaShortVecTest
{
    @Test
    void shouldDecodeAnInteger()
    {
        // 300 = 100101100
        // short-vec = 10101100 00000010
        final var n300 = new byte[]{ (byte) 0b10101100, (byte) 0b00000010};
        // 0 1010100
        final var actual = SolanaShortVec.readInt(ByteBuffer.wrap(n300));

        assertThat(actual).isEqualTo(300);
    }

    @Test
    void shouldDecodeALong()
    {
        // 300 = 100101100
        // short-vec = 10101100 00000010
        final var n300 = new byte[]{ (byte) 0b10101100, (byte) 0b00000010};
        // 0 1010100
        final var actual = SolanaShortVec.readLong(ByteBuffer.wrap(n300));

        assertThat(actual).isEqualTo(300);
    }

    @Test
    void shouldEncode()
    {
        // 300 = 100101100
        // short-vec = 10101100 00000010

        final var expected = new byte[]{ (byte) 0b10101100, (byte) 0b00000010};
        final var buffer = ByteBuffer.allocate(2);

        SolanaShortVec.write(300, buffer);

        assertThat(buffer.array()).isEqualTo(expected);
    }
}