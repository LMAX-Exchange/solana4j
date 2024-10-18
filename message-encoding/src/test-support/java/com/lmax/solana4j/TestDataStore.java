package com.lmax.solana4j;

import java.util.HashMap;
import java.util.Map;

public final class TestDataStore<T>
{
    private final Map<String, T> map = new HashMap<>();

    public void store(final String alias, final T value)
    {
        map.put(alias, value);
    }

    public T lookup(final String alias)
    {
        return map.get(alias);
    }
}
