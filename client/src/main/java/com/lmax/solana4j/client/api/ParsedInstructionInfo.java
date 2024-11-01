package com.lmax.solana4j.client.api;

public interface ParsedInstructionInfo
{
    String getDestination();

    long getLamports();

    String getSource();
}
