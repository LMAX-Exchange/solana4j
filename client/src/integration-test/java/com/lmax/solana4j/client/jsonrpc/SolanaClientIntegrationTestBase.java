package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import org.junit.jupiter.api.BeforeEach;

public class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    // found in shared/src/test-support/resources/testcontainers/accounts
    protected final String payerAccount = "EjmK3LTW8oBSJp14zvQ55PMvPJCuQwRrnjd17P4vrQYo";
    protected final String tokenAccount = "BxoHF6TfQYXkpThMC7hVeex37U3duprVZuFAa7Akortf";
    protected final String tokenMintAccount = "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg";
    // currently unused and therefore untested
//    protected final String associatedTokenAccount = "4swkJXi9iwk1sRB4QAk4ZmX1WCWfpP9ButmqCfkEyaqC";
//    protected final String multiSigAccount = "13SkCFWQiqAN4YTdH3SxNoUNL4RvcxCxMXXQxpJue1UW";
//    protected final String nonceAccount = "6qS7fCwPYCSJ6msth7h1AB6g8aGe6rro1agHAamM4rAM"

    protected SolanaApi api;

    @BeforeEach
    void moreSetup()
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        Waiter.waitFor(Condition.isTrue(() -> api.getSlot().getResponse() > 35));
    }
}
