package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterisation.ParameterizedMessageEncodingTest;

public class ComputeBudgetProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{

    @ParameterizedMessageEncodingTest
    void shouldSetComputeUnitLimitAndPrice(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("payer");

        solana.airdropSol("payer", "0.001");

        solana.setComputeUnits("300", "10000", "payer", "tx");

        solana.verifyTransactionMetadata("tx", "computeUnitsConsumed: 300");

        // -32002 = transaction simulation failed
        solana.setComputeUnits("299", "10000", "payer", "expectedErrorCode: -32002");

        // -32002 = transaction simulation failed
        solana.setComputeUnits("300", "1000000000", "payer", "expectedErrorCode: -32002");
    }
}
