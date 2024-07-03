package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.IntegrationTestBase;
import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

final class AddressLookupTableProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "0.01");
        solana.createKeyPair("addressLookupTableAuthority");
    }

    @ParameterizedMessageEncodingTest
    void shouldCreateAddressLookupTable(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createAddressLookupTable("lookupTableAddress", "addressLookupTableAuthority", "payer");
        solana.retrieveAddressLookupTable("lookupTableAddress");
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

        solana.retrieveAddressLookupTable(
                "lookupTableAddress",
                "addressForLookupTable1, addressForLookupTable2, addressForLookupTable3"
        );
    }
}
