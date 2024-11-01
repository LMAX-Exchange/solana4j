package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetBalanceContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetBalance() throws SolanaJsonRpcClientException
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : {
//            "context" : {
//                "apiVersion" : "2.0.14",
//                        "slot" : 309
//            },
//            "value" : 600000
//        },
//            "id" : 4
//        }
        assertThat(api.getBalance(payerAccount).getResponse()).isEqualTo(600000L);
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForTokenAccountBalance()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForUnknownAccountBalance()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedAccountBalance()
    {

    }
}
