package com.lmax.solana4j.assertion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class Waiter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Waiter.class);
    private static final Random RANDOM = new Random();

    private int retries = 10;
    private Duration maximumBackoff = Duration.ofSeconds(3);
    private Duration initialDelay = Duration.ofSeconds(0);

    public <T> T waitFor(final Assertion<T> assertion)
    {
        LockSupport.parkNanos(this.initialDelay.toNanos());

        for (int i = 0; i < retries; i++)
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
        throw new AssertionError("Waiting for condition that never happened. " + ZonedDateTime.now());
    }

    public Waiter withRetries(final int retries)
    {
        this.retries = retries;
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
