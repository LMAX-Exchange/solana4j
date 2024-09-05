package com.lmax.solana4j.assertion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class IsNotEqualToCondition<T> extends Condition<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IsNotEqualToCondition.class);

    private final T notExpected;
    private final Supplier<T> actualSupplier;

    IsNotEqualToCondition(final T notExpected, final Supplier<T> actualSupplier)
    {
        this.notExpected = notExpected;
        this.actualSupplier = actualSupplier;
    }

    @Override
    public void check()
    {
        try
        {
            final T actual = actualSupplier.get();
            LOGGER.debug("Actual value {}.", actual);
            if (notExpected == null)
            {
                assertThat(actual).isNotNull();
            }
            else
            {
                assertThat(actual).isNotEqualTo(notExpected);
            }
        }
        catch (final Exception e)
        {
            LOGGER.debug("Something went wrong trying to get the actual value: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public T getActual()
    {
        return actualSupplier.get();
    }
}
