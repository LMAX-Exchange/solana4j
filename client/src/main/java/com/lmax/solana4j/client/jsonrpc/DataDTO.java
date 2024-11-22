package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Data;

import java.util.List;

public class DataDTO implements Data
{
    private final String programId;
    private final List<String> data;

    public DataDTO(final @JsonProperty("programId") String programId, final @JsonProperty("data") List<String> data)
    {
        this.programId = programId;
        this.data = data;
    }

    @Override
    public String getProgramId()
    {
        return programId;
    }

    @Override
    public List<String> getData()
    {
        return data;
    }
}
