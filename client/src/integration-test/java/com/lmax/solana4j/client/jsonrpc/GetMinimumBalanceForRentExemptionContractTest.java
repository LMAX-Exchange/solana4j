package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getminimumbalanceforrentexemption
class GetMinimumBalanceForRentExemptionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetMinimumBalanceForRentExemption()
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : 7850880,
//                "id" : 4
//        }

        assertThat(api.getMinimumBalanceForRentExemption(1000)).isGreaterThan(0L);
    }

    @Test
    @Disabled
    void whatHappensWithNegativeOrZeroSpace()
    {

    }
}
