package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/requestairdrop
class RequestAirdropContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldRequestAirdrop()
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : "cPN2nfGaYtiFC9yR5FFLANmE5jxQFXxyYH3keCPKXuZ4LNAoZBvnqhFLomAMznTGorkw1iMrX3qNy8AVmX8UwEF",
//                "id" : 4
//        }
        assertThat(api.requestAirdrop(payerAccount, new Sol(BigDecimal.ONE).lamports())).isNotBlank();
    }
}
