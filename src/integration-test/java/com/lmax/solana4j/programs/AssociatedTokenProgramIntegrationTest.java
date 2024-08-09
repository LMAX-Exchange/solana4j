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

        solana.createMintAccount("mintAddress", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram);

        // When
        // I create the associated Token Address
        solana.createAssociatedTokenAddress("associatedTokenAddress: tokenAddress", "owner: owner", "tokenMint: mintAddress", "payer: payer", "tokenProgram: " + tokenProgram);

        // Then
        // The associated Token Address is successfully created
        solana.tokenBalance("address: tokenAddress", "amount: 0");
    }
}
