package com.lmax.solana4j.assertion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class IsEqualToAssertion<T> extends Assertion<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IsEqualToAssertion.class);

    private final T expected;
    private final Supplier<T> actualSupplier;

    public IsEqualToAssertion(final T expected, final Supplier<T> actualSupplier)
    {
        this.expected = expected;
        this.actualSupplier = actualSupplier;
    }

    @Override
    public void doAssert()
    {
        try
        {
            final T actual = actualSupplier.get();
            LOGGER.info("Expected value {} and actual value {}.", expected, actual);
            assertThat(expected).isEqualTo(actual);
        }
        catch (final Exception e)
        {
            LOGGER.info("Something went wrong trying to get the actual value: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public T getActual()
    {
        return actualSupplier.get();
    }
}
