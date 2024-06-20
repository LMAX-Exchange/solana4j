package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.IntegrationTestBase;
import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

public class SystemProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "100");
    }

    @ParameterizedMessageEncodingTest
    void shouldCreateNonce(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("account");
        solana.createKeyPair("authority");

        solana.airdrop("authority", "10");
        solana.createNonceAccount("account", "authority", "payer");
    }

    @ParameterizedMessageEncodingTest
    void shouldAdvanceNonce(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("account");
        solana.createKeyPair("authority");

        solana.airdrop("address: authority", "amountSol: 10");
        solana.createNonceAccount("account", "authority", "payer");

        solana.advanceNonce("account", "authority", "payer");
    }

    @ParameterizedMessageEncodingTest
    void shouldTransferSol(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("from");
        solana.createKeyPair("to");

        solana.airdrop("from", "10");

        solana.transfer("from", "to", "1", "payer");

        solana.balance("from", "9");
        solana.balance("to", "1");
    }
}
