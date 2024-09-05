package com.lmax.solana4j.assertion;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.concurrent.locks.LockSupport;

public final class Waiter
{
    private static final Duration DEFAULT_INITIAL_DELAY = Duration.ZERO;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_POLLING_INTERVAL = Duration.ofSeconds(3);

    private final Duration initialDelay;
    private final Duration timeout;
    private final Duration pollingInterval;

    private Waiter()
    {
        this(DEFAULT_INITIAL_DELAY, DEFAULT_TIMEOUT, DEFAULT_POLLING_INTERVAL);
    }

    private Waiter(final Duration initialDelay, final Duration timeout, final Duration pollingInterval)
    {
        this.initialDelay = initialDelay;
        this.timeout = timeout;
        this.pollingInterval = pollingInterval;
    }

    public Waiter withInitialDelay(final Duration initialDelay)
    {
        return new Waiter(initialDelay, timeout, pollingInterval);
    }

    public Waiter withTimeout(final Duration timeout)
    {
        return new Waiter(initialDelay, timeout, pollingInterval);
    }

    public Waiter withPollingInterval(final Duration pollingInterval)
    {
        return new Waiter(initialDelay, timeout, pollingInterval);
    }

    public <T> T waitForAssertion(final Assertion<T> assertion)
    {
        final long endTimeMillis = System.currentTimeMillis() + timeout.toMillis();

        LockSupport.parkNanos(initialDelay.toNanos());
        while (System.currentTimeMillis() < endTimeMillis)
        {
            try
            {
                assertion.doAssert();
                return assertion.getActual();
            }
            catch (final Throwable throwable)
            {
                LockSupport.parkNanos(pollingInterval.toNanos());
            }
        }

        throw new AssertionError("Waiting for condition that never happened. " + ZonedDateTime.now());
    }

    public static <T> T waitFor(final Assertion<T> assertion)
    {
        return new Waiter().waitForAssertion(assertion);
    }
}
