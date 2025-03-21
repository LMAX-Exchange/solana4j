package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterisation.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

final class AddressLookupTableProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdropSol("payer", "0.01");
        solana.createKeyPair("addressLookupTableAuthority");
    }

    @ParameterizedMessageEncodingTest
    void shouldCreateAddressLookupTable(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createAddressLookupTable("lookupTableAddress", "addressLookupTableAuthority", "payer");
        solana.verifyAddressLookupTable("lookupTableAddress");
    }

    @ParameterizedMessageEncodingTest
    void shouldExtendAddressLookupTable(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createAddressLookupTable("lookupTableAddress", "addressLookupTableAuthority", "payer");

        solana.createKeyPair("addressForLookupTable1");
        solana.createKeyPair("addressForLookupTable2");
        solana.createKeyPair("addressForLookupTable3");

        solana.extendAddressLookupTable(
                "lookupTableAddress",
                "addressLookupTableAuthority",
                "payer",
                "addressForLookupTable1, addressForLookupTable2, addressForLookupTable3"
        );

        solana.verifyAddressLookupTable(
                "lookupTableAddress",
                "addressForLookupTable1, addressForLookupTable2, addressForLookupTable3"
        );
    }
}
