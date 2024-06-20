package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.AccountInfo;
import com.lmax.solana4j.solanaclient.api.ProgramAccountResponse;

final class ProgramAccountResponseDTO implements ProgramAccountResponse
{
    private final String pubKey;
    private final AccountInfoDTO.AccountInfoValueDTO account;

    @JsonCreator
    ProgramAccountResponseDTO(
            final @JsonProperty("pubKey") String pubKey,
            final @JsonProperty("account") AccountInfoDTO.AccountInfoValueDTO account)
    {
        this.pubKey = pubKey;
        this.account = account;
    }

    @Override
    public String getPubKey()
    {
        return pubKey;
    }

    @Override
    public AccountInfo getAccount()
    {
        return account;
    }

    @Override
    public String toString()
    {
        return "ProgramAccountsResponse{" +
                "pubKey='" + pubKey + '\'' +
                ", account=" + account +
                '}';
    }
}
