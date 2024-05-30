package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;

import java.util.HashMap;
import java.util.Map;

public class TestContext
{
    private final Map<String, TestPublicKey> publicKeys = new HashMap<>();
    private final Map<String, TestKeyPair> keyPairs = new HashMap<>();
    private final Map<String, AddressLookupTable> addressLookupTables = new HashMap<>();

    public TestPublicKey getPublicKey(final String alias)
    {
        return publicKeys.get(alias);
    }

    public void storePublicKey(final String alias, final TestPublicKey address)
    {
        publicKeys.put(alias, address);
    }

    public void storePublicKey(final String alias, final byte[] addressBytes)
    {
        publicKeys.put(alias, new TestPublicKey(addressBytes));
    }

    public TestKeyPair getKeyPair(final String alias)
    {
        return keyPairs.get(alias);
    }

    public void storeKeyPair(final String alias, final TestKeyPair testKeyPair)
    {
        keyPairs.put(alias, testKeyPair);
    }

    public void storeAddressLookupTable(final String alias, final AddressLookupTable addressLookupTable)
    {
        addressLookupTables.put(alias, addressLookupTable);
    }

    public AddressLookupTable getAddressLookupTable(final String alias)
    {
        return addressLookupTables.get(alias);
    }
}
