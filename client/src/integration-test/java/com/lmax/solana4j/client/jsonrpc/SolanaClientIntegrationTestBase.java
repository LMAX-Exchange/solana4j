package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import org.junit.jupiter.api.BeforeEach;

public class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    // found in shared/src/test-support/resources/testcontainers/accounts
    protected final String associatedTokenAccount = "4swkJXi9iwk1sRB4QAk4ZmX1WCWfpP9ButmqCfkEyaqC";
    protected final String multiSigAccount = "13SkCFWQiqAN4YTdH3SxNoUNL4RvcxCxMXXQxpJue1UW";
    protected final String nonceAccount = "6qS7fCwPYCSJ6msth7h1AB6g8aGe6rro1agHAamM4rAM";
    protected final String payerAccount = "EjmK3LTW8oBSJp14zvQ55PMvPJCuQwRrnjd17P4vrQYo";
    protected final String tokenAccount = "Ad1xH9GUwv8AgsMvGzPmxvgKyht3u2yEseztCqQGH6aJ";
    protected final String tokenMintAccount = "876uAwedRmXAxH2uuxcDwJ49gMyCemWZUz73m2L7NZVn";

    protected SolanaApi api;

    @BeforeEach
    void moreSetup()
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        Waiter.waitFor(Condition.isTrue(() -> api.getSlot() > 35));
    }
}
