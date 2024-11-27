package com.lmax.solana4j.encoding;

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
public class Base58EncodingBenchmark
{
    private static final String BASE58_STRING = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final byte[] BYTES = BASE58_STRING.getBytes(StandardCharsets.UTF_8);

    @Benchmark
    public void base58DecodeSolana4jImplementation(final Blackhole bh)
    {
        bh.consume(Base58.decode(BASE58_STRING));
    }

    @Benchmark
    public void base58EncodeSolana4jImplementation(final Blackhole bh)
    {
        bh.consume(Base58.encode(BYTES));
    }

    @Benchmark
    public void base58DecodeBitcoinjImplementation(final Blackhole bh)
    {
        bh.consume(org.bitcoinj.core.Base58.decode(BASE58_STRING));
    }

    @Benchmark
    public void base58EncodeBitcoinjImplementation(final Blackhole bh)
    {
        bh.consume(org.bitcoinj.core.Base58.encode(BYTES));
    }
}