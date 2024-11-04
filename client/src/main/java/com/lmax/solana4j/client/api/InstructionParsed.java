package com.lmax.solana4j.client.api;

public interface InstructionParsed
{
    InstructionInfoParsed getInfo();

    String getType();

    interface InstructionInfoParsed
    {
        String getDestination();

        long getLamports();

        String getSource();
    }

}
