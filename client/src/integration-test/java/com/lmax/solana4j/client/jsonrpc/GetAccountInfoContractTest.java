package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.util.TestKeyPairGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    @Disabled
    void shouldGetAccountInfo()
    {
        final var testKeyPair = TestKeyPairGenerator.generateSolanaKeyPair();

        api.requestAirdrop(testKeyPair.getPublicKeyBase58(), 10L);

        final var accountInfo = Waiter.waitFor(Condition.isNotNull(() -> api.getAccountInfo(testKeyPair.getPublicKeyBase58())));
        assertThat(accountInfo).isNotNull();
    }
}
