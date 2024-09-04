package com.lmax.solana4j.encoding;

import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SolanaSlotTest
{
    @Test
    void throwsOnNullKey()
    {
        assertThatThrownBy(() ->
                new SolanaSlot(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnInvalidBlockhashLength()
    {
        assertThatThrownBy(() ->
                new SolanaSlot(new byte[0]))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                new SolanaSlot(new byte[1]))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                new SolanaSlot(new byte[7]))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                new SolanaSlot(new byte[9]))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void supportsEquality()
    {
        final var a = new SolanaSlot(new byte[]{0, 0, 0, 0, 0, 0, 0, 1});
        final var b = new SolanaSlot(new byte[]{0, 0, 0, 0, 0, 0, 2, 0});
        final var c = new SolanaSlot(new byte[]{0, 0, 0, 0, 0, 0, 2, 0});
        final var d = new SolanaSlot(new byte[]{0, 4, 0, 0, 0, 0, 0, 0});
        final var e = new SolanaSlot(new byte[]{5, 0, 0, 0, 0, 0, 0, 0});

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
        final var bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        final var a = new SolanaSlot(bytes);

        final var buffer = ByteBuffer.allocate(15);

        buffer.put((byte) 55).put((byte) 56).put((byte) 57);

        a.write(buffer);

        assertThat(buffer.position()).isEqualTo(11);
        assertThat(buffer.limit()).isEqualTo(15);

        assertThat((int) buffer.array()[0]).isEqualTo(55);
        assertThat((int) buffer.array()[1]).isEqualTo(56);
        assertThat((int) buffer.array()[2]).isEqualTo(57);
        assertThat(Arrays.compare(buffer.array(), 3, buffer.position(), bytes, 0, 8)).isZero();
    }

    @Test
    void throwsOnBufferOverflow()
    {
        final var bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        final var a = new SolanaSlot(bytes);

        final var buffer = ByteBuffer.allocate(7);

        assertThatThrownBy(() -> a.write(buffer)).isInstanceOf(BufferOverflowException.class);
    }
}