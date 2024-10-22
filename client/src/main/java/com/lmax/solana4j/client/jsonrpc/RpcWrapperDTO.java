package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class RpcWrapperDTO<T>
{
    private final String jsonrpc;
    private final T result;
    private final int id;
    private final Error error;

    @JsonCreator
    RpcWrapperDTO(
            final @JsonProperty("jsonrpc") String jsonrpc,
            final @JsonProperty("result") T result,
            final @JsonProperty("id") int id,
            final @JsonProperty("error") Error error)
    {
        this.jsonrpc = jsonrpc;
        this.result = result;
        this.id = id;
        this.error = error;
    }

    public T getResult()
    {
        return result;
    }

    public String getJsonrpc()
    {
        return jsonrpc;
    }

    public int getId()
    {
        return id;
    }

    public Error getError()
    {
        return error;
    }

    @Override
    public String toString()
    {
        return "RpcWrapperDTO{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", result=" + result +
                ", id=" + id +
                ", error=" + error +
                '}';
    }

    public static class Error
    {
        private long code;

        private String message;

        public long getCode()
        {
            return code;
        }

        public String getMessage()
        {
            return message;
        }



        @Override
        public String toString()
        {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
