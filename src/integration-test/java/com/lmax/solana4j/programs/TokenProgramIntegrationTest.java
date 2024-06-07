package com.lmax.solana4j.programs;

import com.lmax.solana4j.base.IntegrationTestBase;
import com.lmax.solana4j.base.ParameterizedTokenTest;
import org.junit.jupiter.api.BeforeEach;

class TokenProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "100");
    }

    @ParameterizedTokenTest
    void shouldInitializeTokenMint(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMintAddress");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");

        solana.createMintAccount("tokenMintAddress", "payer", tokenProgram);
        solana.initializeMint("tokenMintAddress", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram);
    }
}
