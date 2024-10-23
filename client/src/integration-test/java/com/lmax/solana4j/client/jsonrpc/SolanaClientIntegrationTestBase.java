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
    // associated token account public key : 4swkJXi9iwk1sRB4QAk4ZmX1WCWfpP9ButmqCfkEyaqC
    // multisig account public key : 13SkCFWQiqAN4YTdH3SxNoUNL4RvcxCxMXXQxpJue1UW
    // nonce account public key : 6qS7fCwPYCSJ6msth7h1AB6g8aGe6rro1agHAamM4rAM
    // payer account public key : EjmK3LTW8oBSJp14zvQ55PMvPJCuQwRrnjd17P4vrQYo
    // token account public key : Ad1xH9GUwv8AgsMvGzPmxvgKyht3u2yEseztCqQGH6aJ
    // token mint public key : 876uAwedRmXAxH2uuxcDwJ49gMyCemWZUz73m2L7NZVn

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
