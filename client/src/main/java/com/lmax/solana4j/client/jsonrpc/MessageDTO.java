package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AccountKeys;
import com.lmax.solana4j.client.api.Header;
import com.lmax.solana4j.client.api.Instruction;
import com.lmax.solana4j.client.api.Message;

import java.util.List;

final class MessageDTO implements Message
{
    private final AccountKeysDTO accountKeys;
    private final HeaderDTO headerImpl;
    private final List<InstructionDTO> instructions;
    private final String recentBlockhash;

    @JsonCreator
    MessageDTO(
            final @JsonProperty("accountKeys") AccountKeysDTO accountKeys,
            final @JsonProperty("header") HeaderDTO headerImpl,
            final @JsonProperty("instructions") List<InstructionDTO> instructions,
            final @JsonProperty("recentBlockhash") String recentBlockhash)
    {
        this.accountKeys = accountKeys;
        this.headerImpl = headerImpl;
        this.instructions = instructions;
        this.recentBlockhash = recentBlockhash;
    }

    @Override
    public AccountKeys getAccountKeys()
    {
        return accountKeys;
    }

    @Override
    public Header getHeader()
    {
        return headerImpl;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Instruction> getInstructions()
    {
        return (List) instructions;
    }

    @Override
    public String getRecentBlockhash()
    {
        return recentBlockhash;
    }

    @Override
    public String toString()
    {
        return "MessageDTO{" +
                "accountKeys=" + accountKeys +
                ", headerImpl=" + headerImpl +
                ", instructions=" + instructions +
                ", recentBlockhash='" + recentBlockhash + '\'' +
                '}';
    }
}
