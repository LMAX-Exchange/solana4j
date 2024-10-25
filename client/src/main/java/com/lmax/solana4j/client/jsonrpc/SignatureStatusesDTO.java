package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SignatureStatus;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

import java.util.List;

final class SignatureStatusesDTO implements SolanaRpcResponse<List<SignatureStatus>>
{
    private final ContextDTO context;
    private final List<SignatureStatus> value;

    @JsonCreator
    SignatureStatusesDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") List<SignatureStatus> value)
    {
        this.context = context;
        this.value = value;
    }

    @Override
    public Context getContext()
    {
        return context;
    }

    @Override
    public List<SignatureStatus> getValue()
    {
        return value;
    }

    static final class SignatureStatusesDataDTO implements SignatureStatus
    {
        private final Long confirmations;
        private final long slot;
        private final Object err;
        private final Commitment confirmationStatus;

        @JsonCreator
        SignatureStatusesDataDTO(
                final @JsonProperty("confirmations") Long confirmations,
                final @JsonProperty("slot") long slot,
                final @JsonProperty("err") Object err,
                final @JsonProperty("confirmationStatus") Commitment confirmationStatus)
        {
            this.confirmations = confirmations;
            this.slot = slot;
            this.err = err;
            this.confirmationStatus = confirmationStatus;
        }

        @Override
        public Long getConfirmations()
        {
            return confirmations;
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
        public Commitment getConfirmationStatus()
        {
            return confirmationStatus;
        }
    }
}