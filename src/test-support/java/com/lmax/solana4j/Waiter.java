package com.lmax.solana4j;

import com.lmax.solana4j.assertions.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.locks.LockSupport;

public class Waiter
{
    // TODO: sort out test logging
    private static final Logger LOGGER = LoggerFactory.getLogger(Waiter.class);

    private final int retrys;
    private final Duration backOff;
    private final Duration initialDelay;

    public Waiter(final int retrys, final Duration backOff, final Duration initialDelay)
    {
        this.retrys = retrys;
        this.backOff = backOff;
        this.initialDelay = initialDelay;
    }

    public <T> T waitFor(final Assertion<T> assertion)
    {
        initialBackOff();

        for (int i=0; i < retrys; i++)
        {
            try
            {
                assertion.doAssert();
                return assertion.getActual();
            }
            catch (final Throwable throwable)
            {
                // TODO: make this exponential
                LockSupport.parkNanos(backOff.toMillis() * 1000 * 1000);
            }
        }
        throw new AssertionError("Waiting for condition that never happened.");
    }

    private void initialBackOff()
    {
        final long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < initialDelay.toMillis())
        {
            LockSupport.parkNanos(1000 * 500);
        }
    }
}
