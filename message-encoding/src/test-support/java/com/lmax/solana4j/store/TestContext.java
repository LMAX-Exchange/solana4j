package com.lmax.solana4j.store;

import java.util.HashMap;
import java.util.Map;

public class TestContext
{
    private final Map<TestDataType<?>, TestDataStore<?>> data = new HashMap<>();

    @SuppressWarnings("unchecked")
    public final <T> TestDataStore<T> data(final TestDataType<T> key)
    {
        return (TestDataStore<T>) data.computeIfAbsent(key, unused -> new TestDataStore<>());
    }

    @SuppressWarnings("unchecked")
    public <T> String lookupOrLiteral(final String alias, final TestDataType<T> testDataType)
    {
        if (alias.startsWith("<") && alias.endsWith(">"))
        {
            return alias.substring(1, alias.length() - 1);
        }
        else
        {
            final TestDataStore<?> testDataStore = data.get(testDataType);

            final T lookup = (T) testDataStore.lookup(alias);

            return testDataType.getTransform().apply(lookup);
        }
    }
}
