package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.util.Base58;

import java.util.function.Function;

public final class TestDataType<T>
{
    public static final TestDataType<TestPublicKey> TEST_PUBLIC_KEY = new TestDataType<>(TestPublicKey::getPublicKeyBase58);
    public static final TestDataType<TestKeyPair> TEST_KEY_PAIR = new TestDataType<>(x -> Base58.encode(x.getPrivateKeyBytes()));
    public static final TestDataType<AddressLookupTable> ADDRESS_LOOKUP_TABLE = new TestDataType<>(x -> x.getLookupTableAddress().base58());
    public static final TestDataType<String> TRANSACTION_ID = new TestDataType<>();

    private Function<T, String> transform;

    public TestDataType()
    {
    }

    public TestDataType(final Function<T, String> transform)
    {
        this.transform = transform;
    }

    public Function<T, String> getTransform()
    {
        return transform;
    }
}
