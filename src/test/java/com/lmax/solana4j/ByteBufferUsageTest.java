package com.lmax.solana4j;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class ByteBufferUsageTest
{

    @Test
    void indexesAreRelativeToOffset()
    {
        final var buffer = ByteBuffer.allocate(1000);
        buffer.position(100);
        buffer.limit(500);

        final var subject = buffer.slice();

        assertThat(subject.arrayOffset()).isEqualTo(100);

        assertThat(subject.position()).isEqualTo(0);
        assertThat(subject.limit()).isEqualTo(400);
        assertThat(subject.capacity()).isEqualTo(400);
    }
}