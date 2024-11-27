package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getbalance
final class GetBalanceContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetBalance() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getBalance(SOL_ACCOUNT).getResponse()).isEqualTo(600000L);
    }

    @Test
    void shouldReturnZeroBalanceForUnknownAccount() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getBalance("9yznQg77FHgGqrcf5V9CMKXQ9tcCt4omVW6NbHesWyog").getResponse()).isEqualTo(0);
    }

    @Test
    void shouldReturnErrorForMalformedAccount() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.getBalance("iamnotarealaccount");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = SOLANA_API.getBalance(SOL_ACCOUNT, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
