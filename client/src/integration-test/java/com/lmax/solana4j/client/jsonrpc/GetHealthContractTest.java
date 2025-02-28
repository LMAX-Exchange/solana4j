package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/gethealth
final class GetHealthContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetHealth() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getHealth().getResponse()).isEqualTo("ok");
    }
}
