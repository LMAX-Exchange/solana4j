package com.lmax.solana4j.programs;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.SolanaNodeDsl;
import org.junit.jupiter.api.BeforeEach;

abstract class SolanaProgramsIntegrationTestBase extends IntegrationTestBase
{
    protected SolanaNodeDsl solana;

    @BeforeEach
    void moreSetup()
    {
        solana = new SolanaNodeDsl(solanaRpcUrl);
        // Solana test validator has no fees until the first transaction is finalised, therefore making tests intermittent
        // See: https://github.com/solana-labs/solana/blob/29f776c676ed4676f64bff927da87cdab0672878/test-validator/src/lib.rs#L1007
        solana.waitForSlot("35");
    }
}
