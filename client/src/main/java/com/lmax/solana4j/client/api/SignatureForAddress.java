package com.lmax.solana4j.client.api;

public interface SignatureForAddress
{
    Object getErr();

    String getMemo();

    String getSignature();

    Long getSlot();

    Long getBlockTime();

    Commitment getConfirmationStatus();
}
