package com.lmax.solana4j.assertion;

public abstract class Assertion<T>
{
    public abstract void doAssert();

    public abstract T getActual();
}
