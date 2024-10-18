package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.Context;
import com.lmax.solana4j.client.api.SolanaRpcResponse;
import com.lmax.solana4j.client.api.TokenAccount;

import java.util.List;

public class TokenAccountsByOwnerDTO implements SolanaRpcResponse<List<TokenAccount>>
{
    private final Context context;
    private final List<TokenAccount> value;

    @JsonCreator
    TokenAccountsByOwnerDTO(
            final @JsonProperty("context") Context context,
            final @JsonProperty("value") List<TokenAccount> value)
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
    public List<TokenAccount> getValue()
    {
        return value;
    }

    static final class TokenAccountDTO implements TokenAccount
    {
        private final String publicKey;
        private final AccountInfoDTO.AccountInfoValueDTO account;

        @JsonCreator
        TokenAccountDTO(
                final @JsonProperty("pubkey") String publicKey,
                final @JsonProperty("account") AccountInfoDTO.AccountInfoValueDTO account)
        {
            this.publicKey = publicKey;
            this.account = account;
        }

        @Override
        public String getPublicKey()
        {
            return publicKey;
        }

        @Override
        public AccountInfoDTO.AccountInfoValueDTO getAccountInfo()
        {
            return account;
        }
    }
}
