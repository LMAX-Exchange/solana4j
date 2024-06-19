package com.lmax.solana4j.programs;

import com.lmax.solana4j.base.IntegrationTestBase;
import com.lmax.solana4j.base.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.fail;

public class BpfLoaderUpgradableProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "100");
    }

    @Disabled
    @ParameterizedMessageEncodingTest
    void shouldSetUpgradeAuthority(final String messageEncoding)
    {
        fail();
    }
}
