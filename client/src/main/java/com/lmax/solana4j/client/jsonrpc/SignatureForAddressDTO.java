package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SignatureForAddress;

public class SignatureForAddressDTO implements SignatureForAddress
{
    private final Object err;
    private final String memo;
    private final String signature;
    private final Long slot;
    private final Long blockTime;
    private final Commitment confirmationStatus;

    @JsonCreator
    public SignatureForAddressDTO(
            final @JsonProperty("err") Object err,
            final @JsonProperty("memo") String memo,
            final @JsonProperty("signature") String signature,
            final @JsonProperty("slot") Long slot,
            final @JsonProperty("blockTime") Long blockTime,
            final @JsonProperty("confirmationStatus") Commitment confirmationStatus)
    {
        this.err = err;
        this.memo = memo;
        this.signature = signature;
        this.slot = slot;
        this.blockTime = blockTime;
        this.confirmationStatus = confirmationStatus;
    }

    @Override
    public Object getErr()
    {
        return err;
    }

    @Override
    public String getMemo()
    {
        return memo;
    }

    @Override
    public String getSignature()
    {
        return signature;
    }

    @Override
    public Long getSlot()
    {
        return slot;
    }

    @Override
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public Commitment getConfirmationStatus()
    {
        return confirmationStatus;
    }
}
