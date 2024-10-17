package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.Context;
import com.lmax.solana4j.solanaclient.api.SolanaRpcResponse;
import com.lmax.solana4j.solanaclient.api.TokenAccount;

import java.util.List;

public class TokenAccountsByOwnerDTO implements SolanaRpcResponse<List<TokenAccountsByOwnerDTO.TokenAccountDTO>>
{
    private final ContextDTO context;
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
    public List<TokenAccountDTO> getValue()
    {
        return value;
    }

    public static final class TokenAccountDTO implements TokenAccount
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
