package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.fail;

public class ComputeBudgetProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "0.01");
    }

    @Disabled
    @ParameterizedMessageEncodingTest
    void shouldSetComputeUnitLimit(final String messageEncoding)
    {
        fail();
    }


    @Disabled
    @ParameterizedMessageEncodingTest
    void shouldSetComputeUnitPrice(final String messageEncoding)
    {
        fail();
    }
}