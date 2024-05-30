package com.lmax.solana4j.testclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.testclient.api.BlockResponse;
import com.lmax.solana4j.testclient.api.Transaction;

import java.util.List;

import static java.util.Collections.unmodifiableList;

// visible for testing
public final class BlockResponseDTO implements BlockResponse
{
    private final Long blockHeight;
    private final Long blockTime;
    private final String blockhash;
    private final long parentSlot;
    private final String previousBlockhash;
    private final List<TransactionDTO> transactions;

    @JsonCreator
    BlockResponseDTO(
            final @JsonProperty("blockHeight") Long blockHeight,
            final @JsonProperty("blockTime") Long blockTime,
            final @JsonProperty("blockhash") String blockhash,
            final @JsonProperty("parentSlot") long parentSlot,
            final @JsonProperty("previousBlockhash") String previousBlockhash,
            final @JsonProperty("transactions") List<TransactionDTO> transactions)
    {
        this.blockHeight = blockHeight;
        this.blockTime = blockTime;
        this.blockhash = blockhash;
        this.parentSlot = parentSlot;
        this.previousBlockhash = previousBlockhash;
        this.transactions = unmodifiableList(transactions);
    }

    @Override
    public Long getBlockHeight()
    {
        return blockHeight;
    }

    @Override
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public String getBlockhash()
    {
        return blockhash;
    }

    @Override
    public long getParentSlot()
    {
        return parentSlot;
    }

    @Override
    public String getPreviousBlockhash()
    {
        return previousBlockhash;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Transaction> getTransactions()
    {
        return (List) transactions;
    }

    @Override
    public String toString()
    {
        return "BlockResponse{" +
               "blockHeight=" + blockHeight +
               ", blockTime=" + blockTime +
               ", blockhash='" + blockhash + '\'' +
               ", parentSlot=" + parentSlot +
               ", previousBlockhash='" + previousBlockhash + '\'' +
               ", transactions=" + transactions +
               '}';
    }

}
