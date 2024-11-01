package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetBlockHeightContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetBlockHeight() throws SolanaJsonRpcClientException
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : 427,
//                "id" : 4
//        }

        assertThat(api.getBlockHeight().getResponse()).isGreaterThan(0L);
    }
}
