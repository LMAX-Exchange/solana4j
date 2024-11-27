package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.SolanaRpcResponse;
import com.lmax.solana4j.client.api.TokenAccount;

import java.util.List;

final class TokenAccountsByOwnerDTO implements SolanaRpcResponse<List<TokenAccount>>
{
    private final Context context;
    private final List<TokenAccountDTO> value;

    @JsonCreator
    TokenAccountsByOwnerDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") List<TokenAccountDTO> value)
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
    public List<TokenAccount> getValue()
    {
        return (List) value;
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
