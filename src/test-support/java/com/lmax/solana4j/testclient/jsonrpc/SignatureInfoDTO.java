package com.lmax.solana4j.testclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.testclient.api.Commitment;
import com.lmax.solana4j.testclient.api.SignatureInfo;

final class SignatureInfoDTO implements SignatureInfo
{
    private final String signature;
    private final long slot;
    private final Object err;
    private final String memo;
    private final Long blockTime;
    private final Commitment confirmationStatus;

    @JsonCreator
    public SignatureInfoDTO(
            final @JsonProperty("signature") String signature,
            final @JsonProperty("slot") long slot,
            final @JsonProperty("err") Object err,
            final @JsonProperty("memo") String memo,
            final @JsonProperty("blockTime") Long blockTime,
            final @JsonProperty("confirmationStatus") Commitment confirmationStatus)
    {
        this.signature = signature;
        this.slot = slot;
        this.err = err;
        this.memo = memo;
        this.blockTime = blockTime;
        this.confirmationStatus = confirmationStatus;
    }

    @Override
    public String getSignature()
    {
        return signature;
    }

    @Override
    public long getSlot()
    {
        return slot;
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
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public Commitment getConfirmationStatus()
    {
        return confirmationStatus;
    }

    @Override
    public String toString()
    {
        return "SignatureInfoDTO{" +
               "signature='" + signature + '\'' +
               ", slot=" + slot +
               ", err=" + err +
               ", memo='" + memo + '\'' +
               ", blockTime=" + blockTime +
               ", confirmationStatus=" + confirmationStatus +
               '}';
    }
}
