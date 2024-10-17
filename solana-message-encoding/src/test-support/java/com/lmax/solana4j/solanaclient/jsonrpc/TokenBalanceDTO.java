package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.TokenAmount;
import com.lmax.solana4j.solanaclient.api.TokenBalance;

final class TokenBalanceDTO implements TokenBalance
{
    private final int accountIndex;
    private final String mint;
    private final String owner;
    private final String programId;
    private final TokenAmountDTO.TokenAmountValueDTO uiTokenAmount;

    @JsonCreator
    TokenBalanceDTO(
            final @JsonProperty("accountIndex") int accountIndex,
            final @JsonProperty("mint") String mint,
            final @JsonProperty("owner") String owner,
            final @JsonProperty("programId") String programId,
            final @JsonProperty("uiTokenAmount") TokenAmountDTO.TokenAmountValueDTO uiTokenAmount)
    {
        this.accountIndex = accountIndex;
        this.mint = mint;
        this.owner = owner;
        this.programId = programId;
        this.uiTokenAmount = uiTokenAmount;
    }

    @Override
    public int getAccountIndex()
    {
        return accountIndex;
    }

    @Override
    public String getMint()
    {
        return mint;
    }

    @Override
    public String getOwner()
    {
        return owner;
    }

    @Override
    public String getProgramId()
    {
        return programId;
    }

    @Override
    public TokenAmount getUiTokenAmount()
    {
        return uiTokenAmount;
    }

    @Override
    public String toString()
    {
        return "TokenBalance{" +
                "accountIndex=" + accountIndex +
                ", mint='" + mint + '\'' +
                ", owner='" + owner + '\'' +
                ", programId='" + programId + '\'' +
                ", uiTokenAmount=" + uiTokenAmount +
                '}';
    }
}
