package com.lmax.solana4j.assertion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Waiter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Waiter.class);
    private static final Random RANDOM = new Random();

    private int retrys = 10;
    private Duration maximumBackoff = Duration.ofSeconds(10);
    private Duration initialDelay = Duration.ofSeconds(1);

    public <T> T waitFor(final Assertion<T> assertion)
    {
        initialBackOff();

        for (int i = 0; i < retrys; i++)
        {
            try
            {
                assertion.doAssert();
                return assertion.getActual();
            }
            catch (final Throwable throwable)
            {
                final long exponentialBackoff = calculateExponentialBackoffDelay(i + 1);
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(exponentialBackoff));
            }
        }
        throw new AssertionError("Waiting for condition that never happened.");
    }

    public Waiter withRetrys(final int retrys)
    {
        this.retrys = retrys;
        return this;
    }

    public Waiter withMaximumBackoff(final Duration maximumBackOff)
    {
        this.maximumBackoff = maximumBackOff;
        return this;
    }

    public Waiter withInitialDelay(final Duration initialDelay)
    {
        this.initialDelay = initialDelay;
        return this;
    }

    private void initialBackOff()
    {
        final long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < initialDelay.toMillis())
        {
            LockSupport.parkNanos(1000 * 500);
        }
    }

    private long calculateExponentialBackoffDelay(final int attempt)
    {
        long delay = (1L << (attempt - 1));
        delay = Math.min(delay, maximumBackoff.toSeconds());
        if (delay > 1)
        {
            delay = delay / 2 + RANDOM.nextInt((int) delay / 2);
        }
        LOGGER.debug("Backing off, delay now is {} seconds.", delay);
        return delay;
    }
}
