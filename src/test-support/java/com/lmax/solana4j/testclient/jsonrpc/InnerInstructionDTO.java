package com.lmax.solana4j.testclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.testclient.api.InnerInstruction;
import com.lmax.solana4j.testclient.api.Instruction;

import java.util.List;

import static java.util.Collections.unmodifiableList;

final class InnerInstructionDTO implements InnerInstruction
{
    private final long index;
    private final List<InstructionDTO> instructions;

    @JsonCreator
    InnerInstructionDTO(
            final @JsonProperty("index") long index,
            final @JsonProperty("instructions") List<InstructionDTO> instructions)
    {
        this.index = index;
        this.instructions = unmodifiableList(instructions);
    }

    @Override
    public long getIndex()
    {
        return index;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Instruction> getInstructions()
    {
        return (List) instructions;
    }

    @Override
    public String toString()
    {
        return "InnerInstruction{" +
               "index=" + index +
               ", instructions=" + instructions +
               '}';
    }
}
