package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AccountKey;
import com.lmax.solana4j.client.api.Message;
import com.lmax.solana4j.client.api.TransactionData;

import java.util.List;

final class TransactionDataDTO implements TransactionData
{
    private final MessageDTO message;
    private final List<AccountKeyDTO> accountKeys;
    private final List<String> signatures;

    @JsonCreator
    TransactionDataDTO(
            final @JsonProperty("message") MessageDTO message,
            final @JsonProperty("accountKeys") List<AccountKeyDTO> accountKeys,
            final @JsonProperty("signatures") List<String> signatures)
    {
        this.message = message;
        this.accountKeys = accountKeys;
        this.signatures = signatures;
    }

    @Override
    public Message getMessage()
    {
        return message;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<AccountKey> getAccountKeys()
    {
        return (List) accountKeys;
    }

    @Override
    public List<String> getSignatures()
    {
        return signatures;
    }

    @Override
    public String toString()
    {
        return "TransactionData{" +
               "message=" + message +
               ", signatures=" + signatures +
               '}';
    }
}
