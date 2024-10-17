package com.lmax.solana4j.solanaclient.api;

public interface SignatureStatus
{

    Long getConfirmations();

    long getSlot();

    Object getErr();

    Commitment getConfirmationStatus();
}
