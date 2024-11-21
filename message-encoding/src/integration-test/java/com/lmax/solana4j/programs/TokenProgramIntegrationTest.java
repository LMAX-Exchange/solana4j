package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterisation.ParameterizedTokenTest;
import org.junit.jupiter.api.BeforeEach;

final class TokenProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdropSol("payer", "0.1");
    }

    @ParameterizedTokenTest
    void shouldCreateTokenMint(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMint");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");

        solana.maybeCreateAndExtendAddressLookupTables(messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable", "addresses: tokenMint, mintAuthority, freezeAuthority"
        );

        solana.createMintAccount("tokenMint", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable");

        solana.verifyMintAccount("tokenMint", "mintAuthority", "freezeAuthority", "tokenProgram: " + tokenProgram);
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

        solana.tokenTransfer(
                "tokenAccountSender",
                "tokenAccountReceiver",
                "tokenAccountOwner",
                "10",
                "payer",
                "signers: payer, tokenAccountOwner",
                "tokenProgram: " + tokenProgram,
                "addressLookupTables: addressLookupTable1, addressLookupTable2");

        solana.tokenBalance("tokenAccountSender", "0.00000000000000009");
        solana.tokenBalance("tokenAccountReceiver", "0.00000000000000001");
    }

    @ParameterizedTokenTest
    void shouldCreateMultiSig(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("multiSig");
        solana.createKeyPair("signer1");
        solana.createKeyPair("signer2");
        solana.createKeyPair("signer3");
        solana.createKeyPair("signer4");
        solana.createKeyPair("signer5");

        solana.maybeCreateAndExtendAddressLookupTables(
                messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable", "addresses: signer1, signer2, signer3, signer4, signer5");

        solana.createMultiSigAccount(
                "multiSig: multiSig",
                "multiSigSigners: signer1, signer2, signer3, signer4, signer5",
                "requiredSigners: 3",
                "payer: payer",
                "tokenProgram: " + tokenProgram,
                "addressLookupTables: addressLookupTable"
        );

         solana.verifyMultiSigAccount(
                "multiSigAccount: multiSig",
                "multiSigSigners: signer1, signer2, signer3, signer4, signer5",
                "requiredSigners: 3",
                "tokenProgram: " + tokenProgram
        );
    }

    @ParameterizedTokenTest
    void shouldSetAuthority(final String messageEncoding, final String tokenProgram)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("tokenMint");
        solana.createKeyPair("mintAuthority");
        solana.createKeyPair("freezeAuthority");
        solana.createKeyPair("tokenAccount");
        solana.createKeyPair("tokenAccountAuthority");
        solana.createKeyPair("tokenAccountNewAuthority");

        solana.maybeCreateAndExtendAddressLookupTables(
                messageEncoding, "payer: payer",
                "lookupTableAddress: addressLookupTable1", "addresses: tokenMint, mintAuthority, freezeAuthority",
                "lookupTableAddress: addressLookupTable2", "addresses: tokenAccount, tokenAccountAuthority, tokenAccountNewAuthority");

        solana.createMintAccount("tokenMint", "18", "mintAuthority", "freezeAuthority", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");
        solana.createTokenAccount("tokenAccount", "tokenAccountAuthority", "tokenMint", "payer", tokenProgram, "addressLookupTable1", "addressLookupTable2");

        solana.verifyTokenAccount(
                "tokenAccount",
                "tokenMint",
                "tokenAccountAuthority",
                "tokenProgram: " + tokenProgram);

        solana.setTokenAccountAuthority(
                "tokenAccount",
                "tokenAccountAuthority",
                "tokenAccountNewAuthority",
                "ACCOUNT_OWNER",
                "payer",
                "tokenProgram: " + tokenProgram);

        solana.verifyTokenAccount(
                "tokenAccount",
                "tokenMint",
                "tokenAccountNewAuthority",
                "tokenProgram: " + tokenProgram);
    }
}
