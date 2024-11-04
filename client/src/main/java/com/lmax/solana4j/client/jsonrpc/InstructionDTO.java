package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Instruction;
import com.lmax.solana4j.client.api.InstructionParsed;

import java.util.List;

final class InstructionDTO implements Instruction
{
    private final List<Integer> accounts;
    private final InstructionParsedDTO instructionParsed;
    private final String data;
    private final String program;
    private final String programId;
    private final Integer programIdIndex;
    private final Integer stackHeight;

    @JsonCreator
    InstructionDTO(
            final @JsonProperty("accounts") List<Integer> accounts,
            final @JsonProperty("parsed") InstructionParsedDTO instructionParsed,
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
    public InstructionParsed getInstructionParsed()
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
                ", parsedInstruction=" + instructionParsed +
                ", data='" + data + '\'' +
                ", program='" + program + '\'' +
                ", programId='" + programId + '\'' +
                ", programIdIndex=" + programIdIndex +
                ", stackHeight=" + stackHeight +
                '}';
    }

    static final class InstructionParsedDTO implements InstructionParsed
    {
        private final InstructionInfoParsed info;
        private final String type;

        @JsonCreator
        InstructionParsedDTO(
                final @JsonProperty("info") InstructionInfoParsedDTO info,
                final @JsonProperty("type") String type)
        {
            this.info = info;
            this.type = type;
        }

        @Override
        public InstructionInfoParsed getInfo()
        {
            return info;
        }

        @Override
        public String getType()
        {
            return type;
        }

        static class InstructionInfoParsedDTO implements InstructionInfoParsed
        {
            private final String destination;
            private final long lamports;
            private final String source;

            @JsonCreator
            InstructionInfoParsedDTO(
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
    }
}
