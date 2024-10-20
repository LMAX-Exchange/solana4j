package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

public class SystemProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdropSol("payer", "0.01");
    }

    @ParameterizedMessageEncodingTest
    void shouldCreateNonce(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("nonceAccount");
        solana.createKeyPair("nonceAuthority");

        solana.airdropSol("nonceAuthority", "0.01");

        solana.createNonceAccount("nonceAccount", "nonceAuthority", "payer", "nonceAccountValue");

        solana.verifyNonceAccount("nonceAccount", "nonceAuthority", "nonceAccountValue");
    }

    @ParameterizedMessageEncodingTest
    void shouldAdvanceNonce(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("nonceAccount");
        solana.createKeyPair("nonceAuthority");

        solana.airdropSol("address: nonceAuthority", "amountSol: 0.01");

        solana.createNonceAccount("nonceAccount", "nonceAuthority", "payer", "nonceAccountValue");

        solana.verifyNonceAccount("nonceAccount", "nonceAuthority", "nonceAccountValue");

        solana.advanceNonce("nonceAccount", "nonceAuthority", "payer", "newNonceAccountValue");

        solana.verifyNonceAccount("nonceAccount", "nonceAuthority", "newNonceAccountValue");
    }

    @ParameterizedMessageEncodingTest
    void shouldTransferSol(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("from");
        solana.createKeyPair("to");

        solana.airdropSol("from", "0.1");

        solana.transferSol("from", "to", "0.01", "payer");

        solana.solBalance("from", "0.09");
        solana.solBalance("to", "0.01");
    }
}
