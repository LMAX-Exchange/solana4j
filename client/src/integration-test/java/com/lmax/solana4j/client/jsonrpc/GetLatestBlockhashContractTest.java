package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getlatestblockhash
class GetLatestBlockhashContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetLatestBlockhash()
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : {
//            "context" : {
//                "apiVersion" : "2.0.14",
//                        "slot" : 117
//            },
//            "value" : {
//                "blockhash" : "8hmtSxyZRLcXEwpVZyaeniQJevxQkLje3mLgFvyToWLs",
//                        "lastValidBlockHeight" : 267
//            }
//        },
//            "id" : 4
//        }
        final var latestBlockhash = api.getLatestBlockhash().getResponse();
        assertThat(latestBlockhash.getBlockhashBase58()).isNotBlank();
        assertThat(latestBlockhash.getLastValidBlockHeight()).isGreaterThan(0);
    }
}
