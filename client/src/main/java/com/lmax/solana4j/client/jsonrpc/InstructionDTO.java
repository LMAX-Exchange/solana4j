package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Instruction;

import java.util.List;
import java.util.Map;

final class InstructionDTO implements Instruction
{
    private final List<Integer> accounts;
    private final Map<String, Object> instructionParsed;
    private final String data;
    private final String program;
    private final String programId;
    private final Integer programIdIndex;
    private final Integer stackHeight;

    @JsonCreator
    InstructionDTO(
            final @JsonProperty("accounts") List<Integer> accounts,
            final @JsonProperty("parsed") Map<String, Object> instructionParsed,
            final @JsonProperty("data") String data,
            final @JsonProperty("program") String program,
            final @JsonProperty("programId") String programId,
            final @JsonProperty("programIdIndex") Integer programIdIndex,
            final @JsonProperty("stackHeight") Integer stackHeight)
    {
        this.accounts = accounts;
        this.instructionParsed = instructionParsed;
        this.data = data;
        this.program = program;
        this.programId = programId;
        this.programIdIndex = programIdIndex;
        this.stackHeight = stackHeight;
    }

    @Override
    public List<Integer> getAccounts()
    {
        return accounts;
    }

    @Override
    public String getData()
    {
        return data;
    }

    @Override
    public Integer getProgramIdIndex()
    {
        return programIdIndex;
    }

    @Override
    public String getProgram()
    {
        return program;
    }

    @Override
    public String getProgramId()
    {
        return programId;
    }

    @Override
    public Map<String, Object> getInstructionParsed()
    {
        return instructionParsed;
    }

    @Override
    public Integer getStackHeight()
    {
        return stackHeight;
    }

    @Override
    public String toString()
    {
        return "InstructionDTO{" +
                "accounts=" + accounts +
                ", instructionParsed=" + instructionParsed +
                ", data='" + data + '\'' +
                ", program='" + program + '\'' +
                ", programId='" + programId + '\'' +
                ", programIdIndex=" + programIdIndex +
                ", stackHeight=" + stackHeight +
                '}';
    }
}
