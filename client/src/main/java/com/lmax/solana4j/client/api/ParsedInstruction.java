package com.lmax.solana4j.client.api;

public interface ParsedInstruction
{
    ParsedInstructionInfo getInfo();

    String getType();
}
