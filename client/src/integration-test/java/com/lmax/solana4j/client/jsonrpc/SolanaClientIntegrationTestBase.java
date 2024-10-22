package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import org.junit.jupiter.api.BeforeEach;

public class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    protected SolanaApi api;

    @BeforeEach
    void moreSetup()
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        Waiter.waitFor(Condition.isTrue(() -> api.getSlot() > 35));
    }
}
