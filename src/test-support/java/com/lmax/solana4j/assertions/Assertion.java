package com.lmax.solana4j.assertions;

public abstract class Assertion<T>
{
    public abstract void doAssert();

    public abstract T getActual();
}
