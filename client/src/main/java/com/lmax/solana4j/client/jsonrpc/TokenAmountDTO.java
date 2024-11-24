package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.SolanaRpcResponse;
import com.lmax.solana4j.client.api.TokenAmount;

final class TokenAmountDTO implements SolanaRpcResponse<TokenAmount>
{
    private final ContextDTO context;
    private final TokenAmountValueDTO value;

    @JsonCreator
    TokenAmountDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") TokenAmountValueDTO value)
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
    public TokenAmount getValue()
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

    static final class TokenAmountValueDTO implements TokenAmount
    {
        private final String amount;
        private final long decimals;
        private final float uiAmount;
        private final String uiAmountString;

        @JsonCreator
        TokenAmountValueDTO(
                final @JsonProperty("amount") String amount,
                final @JsonProperty("decimals") long decimals,
                final @JsonProperty("uiAmount") float uiAmount,
                final @JsonProperty("uiAmountString") String uiAmountString)
        {
            this.amount = amount;
            this.decimals = decimals;
            this.uiAmount = uiAmount;
            this.uiAmountString = uiAmountString;
        }

        @Override
        public String getAmount()
        {
            return amount;
        }

        @Override
        public long getDecimals()
        {
            return decimals;
        }

        @Override
        public float getUiAmount()
        {
            return uiAmount;
        }

        @Override
        public String getUiAmountString()
        {
            return uiAmountString;
        }

        @Override
        public String toString()
        {
            return "TokenAmountValueDTO{" +
                   "amount='" + amount + '\'' +
                   ", decimals=" + decimals +
                   ", uiAmount=" + uiAmount +
                   ", uiAmountString='" + uiAmountString + '\'' +
                   '}';
        }
    }
}
