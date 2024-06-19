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
    void shouldCreateTokenMint(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMintAddress");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");

        solana.createMintAccount("tokenMintAddress", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram);
    }

    @ParameterizedTokenTest
    void shouldMintToToken(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMint");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");
        solana.createKeyPair("tokenAccount");
        solana.createKeyPair("tokenAccountOwner");

        solana.createMintAccount("tokenMint", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram);
        solana.createTokenAccount("tokenAccount", "tokenAccountOwner", "tokenMint", "payer", tokenProgram);

        solana.mintTo("tokenMint", "tokenAccount", "mintAuthority", "payer", "100", tokenProgram);
        solana.tokenBalance("tokenAccount", "0.0000000000000001");
    }
}
