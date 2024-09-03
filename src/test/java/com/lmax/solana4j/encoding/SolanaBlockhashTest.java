package com.lmax.solana4j.encoding;

import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SolanaBlockhashTest
{
    @Test
    void throwsOnNullKey()
    {
        assertThatThrownBy(() ->
                new SolanaBlockhash(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnInvalidBlockhashLength()
    {
        assertThatThrownBy(() ->
                new SolanaBlockhash(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                new SolanaBlockhash(new byte[5]))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                new SolanaBlockhash(new byte[31]))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                new SolanaBlockhash(new byte[75]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void supportsEquality()
    {
        final var a = new SolanaBlockhash(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        final var b = new SolanaBlockhash(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        final var c = new SolanaBlockhash(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        final var d = new SolanaBlockhash(new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        final var e = new SolanaBlockhash(new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

        assertThat(a).isEqualTo(a);
        assertThat(a).isNotEqualTo(b);
        assertThat(a).isNotEqualTo(c);
        assertThat(b).isEqualTo(c);
        assertThat(d).isNotEqualTo(e);

        assertThat(a.hashCode()).isEqualTo(a.hashCode());
        assertThat(b.hashCode()).isEqualTo(c.hashCode());
    }

    @Test
    void writesToByteBufferCleanly()
    {
        final var bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
        final var a = new SolanaBlockhash(bytes);

        final var buffer = ByteBuffer.allocate(50);

        buffer.put((byte) 55).put((byte) 56).put((byte) 57);

        a.write(buffer);

        assertThat(buffer.position()).isEqualTo(35);
        assertThat(buffer.limit()).isEqualTo(50);

        assertThat((int) buffer.array()[0]).isEqualTo(55);
        assertThat((int) buffer.array()[1]).isEqualTo(56);
        assertThat((int) buffer.array()[2]).isEqualTo(57);
        assertThat(Arrays.compare(buffer.array(), 3, buffer.position(), bytes, 0, 32)).isZero();
    }

    @Test
    void throwsOnBufferOverflow()
    {
        final var bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
        final var a = new SolanaBlockhash(bytes);

        final var buffer = ByteBuffer.allocate(31);

        assertThatThrownBy(() -> a.write(buffer)).isInstanceOf(BufferOverflowException.class);
    }
}