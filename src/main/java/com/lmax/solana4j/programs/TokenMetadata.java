package com.lmax.solana4j.programs;

public class TokenMetadata
{
    private final String name;

    public TokenMetadata(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "TokenMetadata{" +
               "name='" + name + '\'' +
               '}';
    }
}
