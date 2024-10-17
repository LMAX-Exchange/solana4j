package com.lmax.solana4j.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Sha256Test
{
    @Test
    void shouldHashToExpectedValue()
    {
        final var sample = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        final var hashSample = new byte[]
                {
                -91, 31, -102, 11, 110, -93, 82, 58, -49, 70,
                -105, -84, 116, -4, -114, 54, 83, -82, -116, -66,
                 26, -65, 88, -83, -1, -80, 105, -31, -11, -24,
                -62, -27
                };

        final byte[] hash = Sha256.hash(sample);

        assertThat(hash).containsExactly(hashSample);
    }
}