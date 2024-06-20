package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.IntegrationTestBase;
import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.fail;

public class AssociatedTokenProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "100");
    }

    @Disabled
    @ParameterizedMessageEncodingTest
    void shouldCreateAssociatedTokenAddress(final String messageEncoding)
    {
        fail();
    }
}
