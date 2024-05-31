package com.lmax.solana4j.client.api;

public interface SignatureStatus
{

    Long getConfirmations();

    long getSlot();

    Object getErr();

    Commitment getConfirmationStatus();
}
