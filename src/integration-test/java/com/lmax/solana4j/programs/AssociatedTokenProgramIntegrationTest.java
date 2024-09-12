package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedTokenTest;
import org.junit.jupiter.api.BeforeEach;

public class AssociatedTokenProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "0.01");

        solana.createKeyPair("owner");
        solana.airdrop("owner", "0.01");

        solana.createKeyPair("mintAddress");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");
    }

    @ParameterizedTokenTest
    void shouldCreateAssociatedTokenAddress(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.maybeCreateAndExtendAddressLookupTables(messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable", "addresses: mintAddress, mintAuthority, freezeAuthority"
        );

        solana.createMintAccount("mintAddress", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable");

        solana.createAssociatedTokenAddress(
                "associatedTokenAddress: tokenAddress",
                "owner: owner",
                "tokenMint: mintAddress",
                "payer: payer",
                "tokenProgram: " + tokenProgram,
                "addressLookupTables: addressLookupTable");

        solana.tokenBalance("address: tokenAddress", "amount: 0");
    }
}
