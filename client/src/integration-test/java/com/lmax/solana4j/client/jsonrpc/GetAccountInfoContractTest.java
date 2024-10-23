package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetAccountInfo()
    {
        final var accountInfo = Waiter.waitFor(Condition.isNotNull(() -> api.getAccountInfo(payerAccount)));

        assertThat(accountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
        assertThat(accountInfo.getData().get(0)).isEqualTo("");
        assertThat(accountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(600000L);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getSpace()).isEqualTo(0);
    }
}
