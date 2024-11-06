package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SimulateTransactionResponse;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

import java.util.List;

final class SimulateTransactionResponseDTO implements SolanaRpcResponse<SimulateTransactionResponse>
{
    private final ContextDTO context;
    private final SimulateTransactionValueDTO value;

    SimulateTransactionResponseDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") SimulateTransactionValueDTO value)
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
    public SimulateTransactionResponse getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "SimulateTransactionResponseDTO{" +
               "context=" + context +
               ", value=" + value +
               '}';
    }

    public static final class SimulateTransactionValueDTO implements SimulateTransactionResponse
    {
        private final Object err;
        private final List<String> logs;
        private final int unitsConsumed;

        @JsonCreator
        SimulateTransactionValueDTO(
                final @JsonProperty("err") Object err,
                final @JsonProperty("logs") List<String> logs,
                final @JsonProperty("unitsConsumed") int unitsConsumed)
        {
            this.err = err;
            this.logs = logs;
            this.unitsConsumed = unitsConsumed;
        }

        @Override
        public Object getErr()
        {
            return err;
        }

        @Override
        public List<String> getLogs()
        {
            return logs;
        }

        @Override
        public int getUnitsConsumed()
        {
            return unitsConsumed;
        }

        @Override
        public String toString()
        {
            return "SimulateTransactionResponseDTO{" +
                   "err='" + err + '\'' +
                   ", logs=" + logs +
                   ", unitsConsumed=" + unitsConsumed +
                   '}';
        }
    }
}
