package com.lmax.solana4j.assertion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class IsNotNullAssertion<T> extends Assertion<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IsNotNullAssertion.class);

    private final Supplier<T> actualSupplier;

    public IsNotNullAssertion(final Supplier<T> actualSupplier)
    {
        this.actualSupplier = actualSupplier;
    }

    @Override
    public void doAssert()
    {
        final T actual = actualSupplier.get();
        LOGGER.info("Actual value {}.", actual);
        assertThat(actual).isNotNull();
    }

    @Override
    public T getActual()
    {
        return actualSupplier.get();
    }
}
