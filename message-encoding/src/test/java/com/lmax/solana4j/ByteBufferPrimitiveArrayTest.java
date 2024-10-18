package com.lmax.solana4j;

import com.lmax.solana4j.util.ByteBufferPrimitiveArray;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;


class ByteBufferPrimitiveArrayTest
{
    @Test
    void shouldReturnAllBytesFromWrappedArray()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var buffer = ByteBuffer.wrap(sample);

        final var result = ByteBufferPrimitiveArray.copy(buffer);

        assertThat(result).containsExactly(sample);
    }

    @Test
    void shouldReturnAllBytesFromBuffer()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var buffer = ByteBuffer.wrap(sample);

        buffer.position(3);

        final var result = ByteBufferPrimitiveArray.copy(buffer);

        assertThat(result).containsExactly(sample);
    }

    @Test
    void shouldReturnBytesFromFrontSlice()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var buffer = ByteBuffer.wrap(sample);

        buffer.position(0);
        buffer.limit(3);
        final var subject = buffer.slice();

        final var result = ByteBufferPrimitiveArray.copy(subject);

        assertThat(result).containsExactly(new byte[]{10, 20, 30});
    }

    @Test
    void shouldReturnBytesFromEndSlice()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var buffer = ByteBuffer.wrap(sample);

        buffer.position(6);
        final var subject = buffer.slice();

        final var result = ByteBufferPrimitiveArray.copy(subject);

        assertThat(result).containsExactly(new byte[]{70, 80, 90});
    }

    @Test
    void shouldReturnBytesFromMidSlice()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var buffer = ByteBuffer.wrap(sample);

        buffer.position(3);
        buffer.limit(6);

        final var subject = buffer.slice();

        final var result = ByteBufferPrimitiveArray.copy(subject);

        assertThat(result).containsExactly(new byte[]{40, 50, 60});
    }

    @Test
    void shouldReturnAllBytesFromSlicedSlice()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var buffer = ByteBuffer.wrap(sample);

        buffer.position(1);
        buffer.limit(8);
        final var slice = buffer.slice();

        slice.position(1);
        slice.limit(6);
        final var slicedSlice = slice.slice();

        final var result = ByteBufferPrimitiveArray.copy(slicedSlice);

        assertThat(result).containsExactly(new byte[]{30, 40, 50, 60, 70});
    }

    @Test
    void shouldReverseByteArray()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var reversedSample = new byte[]{90, 80, 70, 60, 50, 40, 30, 20, 10};

        final var result = ByteBufferPrimitiveArray.reverse(sample);

        assertThat(result).containsExactly(reversedSample);

    }
}