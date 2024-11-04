package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.AccountInfoData;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

final class AccountInfoDTO implements SolanaRpcResponse<AccountInfo>
{
    private final ContextDTO context;
    private final AccountInfoValueDTO value;

    @JsonCreator
    AccountInfoDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") AccountInfoValueDTO value)
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
    public AccountInfo getValue()
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

    static final class AccountInfoValueDTO implements AccountInfo
    {
        private final long lamports;
        private final String owner;
        private final AccountInfoData data;
        private final boolean executable;
        private final String rentEpoch;
        private final int space;

        @JsonCreator
        AccountInfoValueDTO(
                final @JsonProperty("lamports") long lamports,
                final @JsonProperty("owner") String owner,
                final @JsonProperty("data") AccountInfoDataDTO data,
                final @JsonProperty("executable") boolean executable,
                final @JsonProperty("rentEpoch") String rentEpoch,
                final @JsonProperty("space") int space)
        {
            this.lamports = lamports;
            this.owner = owner;
            this.data = data;
            this.executable = executable;
            this.rentEpoch = rentEpoch;
            this.space = space;
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
        public AccountInfoData getData()
        {
            return data;
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
        public long getSpace()
        {
            return space;
        }

        @Override
        public String toString()
        {
            return "AccountInfoValueDTO{" +
                    "lamports=" + lamports +
                    ", owner='" + owner + '\'' +
                    ", data=" + data +
                    ", executable=" + executable +
                    ", rentEpoch='" + rentEpoch + '\'' +
                    ", space=" + space +
                    '}';
        }
    }
}
