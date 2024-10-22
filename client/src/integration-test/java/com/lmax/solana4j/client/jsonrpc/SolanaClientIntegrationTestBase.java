package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.domain.Sol;
import com.lmax.solana4j.util.TestKeyPairGenerator;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

public class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    protected SolanaApi api;
    protected String account;

    @BeforeEach
    void moreSetup()
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        Waiter.waitFor(Condition.isTrue(() -> api.getSlot() > 35));

        final var testKeyPair = TestKeyPairGenerator.generateSolanaKeyPair();
        account = testKeyPair.getPublicKeyBase58();

        api.requestAirdrop(testKeyPair.getPublicKeyBase58(), new Sol(BigDecimal.ONE).lamports());

        Waiter.waitFor(Condition.isEqualTo(1000000000L, () -> api.getBalance(account)));
    }
}
