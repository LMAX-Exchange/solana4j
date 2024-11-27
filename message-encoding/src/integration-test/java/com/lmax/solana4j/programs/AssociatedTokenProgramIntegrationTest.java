package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterisation.ParameterizedTokenTest;
import org.junit.jupiter.api.BeforeEach;

final class AssociatedTokenProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdropSol("payer", "0.01");

        solana.createKeyPair("owner");
        solana.airdropSol("owner", "0.01");

        solana.createKeyPair("tokenMint");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");
    }

    @ParameterizedTokenTest
    void shouldCreateAssociatedTokenAccount(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.maybeCreateAndExtendAddressLookupTables(messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable", "addresses: tokenMint, mintAuthority, freezeAuthority"
        );

        solana.createMintAccount("tokenMint", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable");

        solana.createAssociatedTokenAccount(
                "associatedTokenAddress: associatedTokenAddress",
                "owner: owner",
                "tokenMint: tokenMint",
                "payer: payer",
                "tokenProgram: " + tokenProgram,
                "addressLookupTables: addressLookupTable");

        solana.verifyAssociatedTokenAccount("associatedTokenAddress", "tokenMint", "owner", "tokenProgram: " + tokenProgram);
    }
}
