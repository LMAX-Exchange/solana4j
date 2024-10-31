package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getslot
class GetSlotContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetSlot() throws SolanaJsonRpcClientException
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : 369,
//                "id" : 4
//        }
        assertThat(api.getSlot().getResponse()).isGreaterThan(0L);
    }
}
