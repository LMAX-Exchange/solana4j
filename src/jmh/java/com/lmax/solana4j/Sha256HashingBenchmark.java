package com.lmax.solana4j;

import com.lmax.solana4j.util.Sha256;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@Threads(1)
@State(Scope.Thread)
public class Sha256HashingBenchmark
{
    private static final String RANDOM_STRING = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final byte[] BYTES = RANDOM_STRING.getBytes(StandardCharsets.UTF_8);

    @Benchmark
    public void sha256HashSolana4jImplementation(final Blackhole bh)
    {
        bh.consume(Sha256.hash(BYTES));
    }

    @Benchmark
    public void sha256HashBitcoinjImplementation(final Blackhole bh)
    {
        bh.consume(org.bitcoinj.core.Sha256Hash.hash(BYTES));
    }

}