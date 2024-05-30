package com.lmax.solana4j.testclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.testclient.api.Instruction;

import java.util.List;

import static java.util.Collections.unmodifiableList;

final class InstructionDTO implements Instruction
{
    private final List<Integer> accounts;
    private final String data;
    private final int programIdIndex;

    @JsonCreator
    InstructionDTO(
            final @JsonProperty("accounts") List<Integer> accounts,
            final @JsonProperty("data") String data,
            final @JsonProperty("programIdIndex") int programIdIndex)
    {
        this.accounts = unmodifiableList(accounts);
        this.data = data;
        this.programIdIndex = programIdIndex;
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
    public int getProgramIdIndex()
    {
        return programIdIndex;
    }

    @Override
    public String toString()
    {
        return "Instruction{" +
               "accounts=" + accounts +
               ", data='" + data + '\'' +
               ", programIdIndex=" + programIdIndex +
               '}';
    }
}
