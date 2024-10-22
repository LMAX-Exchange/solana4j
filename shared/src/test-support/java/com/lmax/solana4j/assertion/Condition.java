package com.lmax.solana4j.assertion;

import java.util.function.Supplier;

public abstract class Condition<T>
{
    public abstract void check();

    public abstract T getActual();

    public static Condition<Boolean> isTrue(final Supplier<Boolean> actualSupplier)
    {
        return new IsEqualToCondition<>(true, actualSupplier);
    }

    public static Condition<Boolean> isFalse(final Supplier<Boolean> actualSupplier)
    {
        return new IsEqualToCondition<>(false, actualSupplier);
    }

    public static <S> Condition<S> isNotNull(final Supplier<S> actualSupplier)
    {
        return new IsNotEqualToCondition<>(null, actualSupplier);
    }

    public static <S> Condition<S> isEqualTo(final S expected, final Supplier<S> actualSupplier)
    {
        return new IsEqualToCondition<>(expected, actualSupplier);
    }

    public static <S> Condition<S> isNotEqualTo(final S notExpected, final Supplier<S> actualSupplier)
    {
        return new IsNotEqualToCondition<>(notExpected, actualSupplier);
    }
}
