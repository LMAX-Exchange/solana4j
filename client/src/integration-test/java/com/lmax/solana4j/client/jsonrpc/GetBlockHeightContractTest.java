package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getblockheight
class GetBlockHeightContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetBlockHeight()
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : 427,
//                "id" : 4
//        }

        assertThat(api.getBlockHeight()).isGreaterThan(0L);
    }
}
