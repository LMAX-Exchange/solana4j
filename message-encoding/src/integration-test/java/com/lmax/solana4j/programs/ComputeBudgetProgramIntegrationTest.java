package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;

import static org.junit.Assert.assertThrows;

public class ComputeBudgetProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{

    @ParameterizedMessageEncodingTest
    void shouldSetComputeUnitLimitAndPrice(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("payer");

        solana.airdropSol("payer", "1");

        solana.setComputeUnits("300", "10000", "payer", "tx");

        solana.verifyTransactionMetadata("tx", "computeUnitsConsumed: 300");

        // transaction is exactly 300 compute units so this should fail
        assertThrows(RuntimeException.class, () -> solana.setComputeUnits("299", "10000", "payer"));

        // let's set the compute unit price so high that we don't have enough sol on the payer account
        assertThrows(RuntimeException.class, () -> solana.setComputeUnits("300", "100000000000", "payer"));
    }
}
