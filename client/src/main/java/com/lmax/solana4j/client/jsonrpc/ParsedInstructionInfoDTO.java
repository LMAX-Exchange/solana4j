package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.ParsedInstructionInfo;

public class ParsedInstructionInfoDTO implements ParsedInstructionInfo
{
    private final String destination;
    private final long lamports;
    private final String source;

    @JsonCreator
    public ParsedInstructionInfoDTO(
            final @JsonProperty("destination") String destination,
            final @JsonProperty("lamports") long lamports,
            final @JsonProperty("source") String source)
    {
        this.destination = destination;
        this.lamports = lamports;
        this.source = source;
    }


    @Override
    public String getDestination()
    {
        return destination;
    }

    @Override
    public long getLamports()
    {
        return lamports;
    }

    @Override
    public String getSource()
    {
        return source;
    }
}
