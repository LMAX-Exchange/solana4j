package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AccountInfoParsedData;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

import java.util.Map;

public final class AccountInfoParsedDataDTO implements SolanaRpcResponse<AccountInfoParsedData>
{
    private final ContextDTO context;
    private final AccountInfoValueParsedDataDTO value;

    @JsonCreator
    AccountInfoParsedDataDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") AccountInfoValueParsedDataDTO value)
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
    public AccountInfoParsedData getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "AccountInfoDTO{" +
               "context=" + context +
               ", value=" + value +
               '}';
    }

    static final class AccountInfoValueParsedDataDTO implements AccountInfoParsedData
    {
        private final long lamports;
        private final String owner;
        private final Map<String, Object> data;
        private final boolean executable;
        private final String rentEpoch;

        @JsonCreator
        AccountInfoValueParsedDataDTO(
                final @JsonProperty("lamports") long lamports,
                final @JsonProperty("owner") String owner,
                final @JsonProperty("data") Map<String, Object> data,
                final @JsonProperty("executable") boolean executable,
                final @JsonProperty("rentEpoch") String rentEpoch)
        {
            this.lamports = lamports;
            this.owner = owner;
            this.data = data;
            this.executable = executable;
            this.rentEpoch = rentEpoch;
        }

        @Override
        public long getLamports()
        {
            return lamports;
        }

        @Override
        public String getOwner()
        {
            return owner;
        }

        @Override
        public Map<String, Object> getData()
        {
            return data;
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public String getType()
        {
            return (String) ((Map<String, Object>) data.get("parsed")).get("type");
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public Map<String, Object> getInfo()
        {
            return (Map<String, Object>) (((Map<String, Object>) data.get("parsed")).get("info"));
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public Integer getDecimals()
        {
            final Map<String, Object> parsedData = (Map<String, Object>) data.get("parsed");
            final Object decimalsString = ((Map<String, Object>) parsedData.get("info")).get("decimals");
            return (Integer) decimalsString;
        }

        @Override
        public boolean isExecutable()
        {
            return executable;
        }

        @Override
        public String getRentEpoch()
        {
            return rentEpoch;
        }

        @Override
        public String toString()
        {
            return "AccountInfoValueDTO{" +
                   "lamports=" + lamports +
                   ", owner='" + owner + '\'' +
                   ", data=" + data +
                   ", executable=" + executable +
                   ", rentEpoch=" + rentEpoch +
                   '}';
        }
    }
}
