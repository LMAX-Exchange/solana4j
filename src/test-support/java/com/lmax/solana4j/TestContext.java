package com.lmax.solana4j;

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
}
