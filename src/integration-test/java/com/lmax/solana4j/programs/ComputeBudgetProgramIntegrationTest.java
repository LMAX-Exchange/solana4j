package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertThrows;

public class ComputeBudgetProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "0.01");
    }

    @ParameterizedMessageEncodingTest
    void shouldSetComputeUnitLimitAndPrice(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.setComputeUnits("100000", "10", "payer", "tx");
    }

    @ParameterizedMessageEncodingTest
    void shouldFailIfComputeUnitLimitSetBelowComputeUnitsOfTransaction(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        assertThrows(RuntimeException.class, () -> solana.setComputeUnits("1", "10", "payer", "tx"));
    }

    @ParameterizedMessageEncodingTest
    void shouldFailIfComputeUnitPriceSetTooHighForAvailableFundsToPayer(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        assertThrows(RuntimeException.class, () -> solana.setComputeUnits("100000", "1000000000", "payer", "tx"));
    }
}
