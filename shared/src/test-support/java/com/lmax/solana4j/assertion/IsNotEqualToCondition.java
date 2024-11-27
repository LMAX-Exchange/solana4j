package com.lmax.solana4j.assertion;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

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
            LOGGER.info("Actual value {}.", actual);
            if (notExpected == null)
            {
                Assertions.assertThat(actual).isNotNull();
            }
            else
            {
                Assertions.assertThat(actual).isNotEqualTo(notExpected);
            }
        }
        catch (final Exception e)
        {
            LOGGER.error("Something went wrong trying to get the actual value.", e.getCause());
            throw e;
        }
    }

    @Override
    public T getActual()
    {
        return actualSupplier.get();
    }
}
