package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AddressTableLookup;
import com.lmax.solana4j.client.api.Header;
import com.lmax.solana4j.client.api.Instruction;
import com.lmax.solana4j.client.api.Message;

import java.util.List;

final class MessageDTO implements Message
{
    private final List<String> accountKeys;
    private final HeaderDTO headerImpl;
    private final List<InstructionDTO> instructions;
    private final String latestBlockhash;
    private final List<AddressTableLookupDTO> addressTableLookups;

    @JsonCreator
    MessageDTO(
            final @JsonProperty("accountKeys") List<String> accountKeys,
            final @JsonProperty("header") HeaderDTO headerImpl,
            final @JsonProperty("instructions") List<InstructionDTO> instructions,
            final @JsonProperty("latestBlockhash") String latestBlockhash,
            final @JsonProperty("addressTableLookups") List<AddressTableLookupDTO> addressTableLookups)
    {
        this.accountKeys = accountKeys;
        this.headerImpl = headerImpl;
        this.instructions = instructions;
        this.latestBlockhash = latestBlockhash;
        this.addressTableLookups = addressTableLookups;
    }

    @Override
    public List<String> getAccountKeys()
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
    public String getLatestBlockhash()
    {
        return latestBlockhash;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<AddressTableLookup> getAddressTableLookups()
    {
        return (List) addressTableLookups;
    }

    @Override
    public String toString()
    {
        return "MessageDTO{" +
                "accountKeys=" + accountKeys +
                ", headerImpl=" + headerImpl +
                ", instructions=" + instructions +
                ", latestBlockhash='" + latestBlockhash + '\'' +
                ", addressTableLookups=" + addressTableLookups +
                '}';
    }
}
