package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

import java.util.List;

import static java.util.Collections.unmodifiableList;

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
        private final List<String> data; // Base64 encoded
        private final boolean executable;
        private final long rentEpoch;
        private final int space;

        @JsonCreator
        AccountInfoValueDTO(
                final @JsonProperty("lamports") long lamports,
                final @JsonProperty("owner") String owner,
                final @JsonProperty("data") List<String> data,
                final @JsonProperty("executable") boolean executable,
                final @JsonProperty("rentEpoch") String rentEpoch,
                final @JsonProperty("space") int space)
        {
            this.lamports = lamports;
            this.owner = owner;
            this.data = unmodifiableList(data);
            this.executable = executable;
            // solana rpc uses unsigned longs which can overflow javas long object, this is a small workaround
            this.rentEpoch = Long.parseUnsignedLong(rentEpoch) < 0 ? 0 : Long.parseLong(rentEpoch);
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
        public List<String> getData()
        {
            return data;
        }

        @Override
        public boolean isExecutable()
        {
            return executable;
        }

        @Override
        public long getRentEpoch()
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
                    ", rentEpoch=" + rentEpoch +
                    ", space=" + space +
                    '}';
        }
    }
}
