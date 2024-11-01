package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.ParsedInstruction;
import com.lmax.solana4j.client.api.ParsedInstructionInfo;

public class ParsedInstructionDTO implements ParsedInstruction
{
    private final ParsedInstructionInfoDTO info;
    private final String type;

    @JsonCreator
    public ParsedInstructionDTO(
            final @JsonProperty("info") ParsedInstructionInfoDTO info,
            final @JsonProperty("type") String type)
    {
        this.info = info;
        this.type = type;
    }

    @Override
    public ParsedInstructionInfo getInfo()
    {
        return info;
    }

    @Override
    public String getType()
    {
        return type;
    }
}
