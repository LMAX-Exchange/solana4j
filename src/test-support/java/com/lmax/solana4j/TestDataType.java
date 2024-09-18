package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;

public final class TestDataType<T>
{
    public static final TestDataType<TestPublicKey> TEST_PUBLIC_KEY = new TestDataType<>();
    public static final TestDataType<TestKeyPair> TEST_KEY_PAIR = new TestDataType<>();
    public static final TestDataType<AddressLookupTable> ADDRESS_LOOKUP_TABLE = new TestDataType<>();
}
