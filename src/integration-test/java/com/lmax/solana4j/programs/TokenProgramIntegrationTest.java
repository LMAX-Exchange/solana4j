package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedTokenTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.fail;

class TokenProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "0.1");
    }

    @ParameterizedTokenTest
    void shouldCreateTokenMint(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMintAddress");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");

        solana.maybeCreateAndExtendAddressLookupTables(messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable", "addresses: tokenMintAddress, mintAuthority, freezeAuthority"
        );

        solana.createMintAccount("tokenMintAddress", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable");
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

        solana.maybeCreateAndExtendAddressLookupTables(messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable1", "addresses: tokenMint, mintAuthority",
                "lookupTableAddress: addressLookupTable2", "addresses: freezeAuthority, tokenAccount, tokenAccountOwner"
        );

        solana.createMintAccount("tokenMint", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");
        solana.createTokenAccount("tokenAccount", "tokenAccountOwner", "tokenMint", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");

        solana.mintTo("tokenMint", "tokenAccount", "mintAuthority", "payer", "100", tokenProgram, "addressLookupTable1", "addressLookupTable2");
        solana.tokenBalance("tokenAccount", "0.0000000000000001");
    }

    @ParameterizedTokenTest
    void shouldTransferToken(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMint");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");
        solana.createKeyPair("tokenAccountSender");
        solana.createKeyPair("tokenAccountReceiver");
        solana.createKeyPair("tokenAccountOwner");

        solana.maybeCreateAndExtendAddressLookupTables(
                messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable1", "addresses: tokenMint, mintAuthority, freezeAuthority",
                "lookupTableAddress: addressLookupTable2", "addresses: tokenAccountSender, tokenAccountReceiver, tokenAccountOwner");

        solana.createMintAccount("tokenMint", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");
        solana.createTokenAccount("tokenAccountSender", "tokenAccountOwner", "tokenMint", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");

        solana.mintTo("tokenMint", "tokenAccountSender", "mintAuthority", "payer", "100", tokenProgram, "addressLookupTable1", "addressLookupTable2");
        solana.tokenBalance("tokenAccountSender", "0.0000000000000001");

        solana.createTokenAccount("tokenAccountReceiver", "tokenAccountOwner", "tokenMint", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");

        solana.tokenTransfer("tokenAccountSender", "tokenAccountReceiver", "tokenAccountOwner", "10", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");
        solana.tokenBalance("tokenAccountSender", "0.00000000000000009");
        solana.tokenBalance("tokenAccountReceiver", "0.00000000000000001");
    }

    @Disabled
    @ParameterizedTokenTest
    void shouldCreateMultisig(final String messageEncoding, final String tokenProgram)
    {
        fail();
    }

    @Disabled
    @ParameterizedTokenTest
    void shouldSetAuthority(final String messageEncoding, final String tokenProgram)
    {
        fail();
    }

}
