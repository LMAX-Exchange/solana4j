package com.lmax.solana4j.encoding;

import com.lmax.solana4j.domain.TestKeyPairGenerator;
import net.i2p.crypto.eddsa.math.GroupElement;
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

import java.util.concurrent.TimeUnit;

import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@Threads(1)
@State(Scope.Thread)
public class EddsaPointOnCurveBenchmark
{
    private static final byte[] POINT_NOT_ON_CURVE = new byte[]
            {
                    1, 5, 9, 3, 6, 9, 3, 6, 9, 3,
                    2, 6, 1, 4, 7, 1, 4, 7, 1, 4,
                    3, 7, 2, 5, 8, 2, 5, 8, 2, 5,
                    4, 8
            };

    private static final byte[] POINT_ON_CURVE = TestKeyPairGenerator.generateSolanaKeyPair().getPublicKeyBytes();

    @Benchmark
    public void pointOnCurveSolana4jImplementation(final Blackhole bh)
    {
        bh.consume(Ed25519.isOnCurve(POINT_ON_CURVE));
    }

    @Benchmark
    public void pointNotOnCurveSolana4jImplementation(final Blackhole bh)
    {
        bh.consume(Ed25519.isOnCurve(POINT_NOT_ON_CURVE));
    }

    @Benchmark
    public void pointOnCurveEddsaImplementation(final Blackhole bh)
    {
        try
        {
            final GroupElement point = ED_25519_CURVE_SPEC.getCurve().createPoint(POINT_ON_CURVE, false);
            bh.consume(point.isOnCurve());
        }
        catch (final Exception e)
        {
            bh.consume(true);
        }
    }

    @Benchmark
    public void pointNotOnCurveEddsaImplementation(final Blackhole bh)
    {
        try
        {
            final GroupElement point = ED_25519_CURVE_SPEC.getCurve().createPoint(POINT_NOT_ON_CURVE, false);
            bh.consume(point.isOnCurve());
        }
        catch (final Exception e)
        {
            bh.consume(false);
        }
    }
}