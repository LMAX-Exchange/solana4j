package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SignatureStatus;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

import java.util.List;
import java.util.Map;

final class SignatureStatusesDTO implements SolanaRpcResponse<List<SignatureStatus>>
{
    private final ContextDTO context;
    private final List<SignatureStatusesDataDTO> value;

    @JsonCreator
    SignatureStatusesDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") List<SignatureStatusesDataDTO> value)
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<SignatureStatus> getValue()
    {
        return (List) value;
    }

    static final class SignatureStatusesDataDTO implements SignatureStatus
    {
        private final Long confirmations;
        private final long slot;
        private final Object err;
        private final Commitment confirmationStatus;
        private final Map.Entry<String, Object> status;

        @JsonCreator
        SignatureStatusesDataDTO(
                final @JsonProperty("confirmations") Long confirmations,
                final @JsonProperty("slot") long slot,
                final @JsonProperty("err") Object err,
                final @JsonProperty("confirmationStatus") Commitment confirmationStatus,
                final @JsonProperty("status") Map.Entry<String, Object> status)
        {
            this.confirmations = confirmations;
            this.slot = slot;
            this.err = err;
            this.confirmationStatus = confirmationStatus;
            this.status = status;
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

        @Override
        public Map.Entry<String, Object> getStatus()
        {
            return status;
        }
    }
}
