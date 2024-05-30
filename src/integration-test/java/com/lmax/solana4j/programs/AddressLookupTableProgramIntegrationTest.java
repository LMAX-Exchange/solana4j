package com.lmax.solana4j.programs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressLookupTableProgramIntegrationTest extends SolanaValidatorIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "100");
        solana.createKeyPair("addressLookupTableAuthority");
    }

    @Test
    void shouldCreateAddressLookupTable()
    {
        solana.createAddressLookupTable("lookupTableAddress", "addressLookupTableAuthority", "payer");
        solana.retrieveAddressLookupTable("lookupTableAddress");
    }

    @Test
    void shouldExtendAddressLookupTable()
    {
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
