package com.lmax.solana4j.testclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RpcWrapperDTO<T>
{
    private final T result;
    private final Error error;

    @JsonCreator
    public RpcWrapperDTO(final @JsonProperty("result") T result, final @JsonProperty("error") Error error)
    {
        this.result = result;
        this.error = error;
    }

    public T getResult()
    {
        return result;
    }

    public Error getError()
    {
        return error;
    }

    @Override
    public String toString()
    {
        return "RpcWrapperDTO{" +
                "result=" + result +
                ", error= " + error.toString() +
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
