package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RequestAirdropContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldRequestAirdrop() throws SolanaJsonRpcClientException
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : "cPN2nfGaYtiFC9yR5FFLANmE5jxQFXxyYH3keCPKXuZ4LNAoZBvnqhFLomAMznTGorkw1iMrX3qNy8AVmX8UwEF",
//                "id" : 4
//        }
        assertThat(api.requestAirdrop(payerAccount, Sol.lamports(BigDecimal.ONE)).getResponse()).isNotBlank();
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedAccount()
    {

    }
}
