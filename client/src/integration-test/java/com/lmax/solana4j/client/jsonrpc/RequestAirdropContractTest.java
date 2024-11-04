package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/requestairdrop
class RequestAirdropContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldRequestAirdrop() throws SolanaJsonRpcClientException
    {
        assertThat(api.requestAirdrop(dummyAccount, Sol.lamports(BigDecimal.ONE)).getResponse()).isNotBlank();
    }

    @Test
    void shouldReturnErrorForMalformedAccount() throws SolanaJsonRpcClientException
    {
        final var response = api.requestAirdrop("iamnotarealaccount", Sol.lamports(BigDecimal.ONE));
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }
}
