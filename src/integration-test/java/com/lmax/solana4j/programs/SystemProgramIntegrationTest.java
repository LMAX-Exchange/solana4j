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
        solana.airdrop("payer", "0.01");
    }

    @ParameterizedMessageEncodingTest
    void shouldCreateNonce(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("account");
        solana.createKeyPair("authority");

        solana.airdrop("authority", "0.01");
        solana.createNonceAccount("account", "authority", "payer");
    }

    @ParameterizedMessageEncodingTest
    void shouldAdvanceNonce(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("account");
        solana.createKeyPair("authority");

        solana.airdrop("address: authority", "amountSol: 0.01");
        solana.createNonceAccount("account", "authority", "payer");

        solana.advanceNonce("account", "authority", "payer");
    }

    @ParameterizedMessageEncodingTest
    void shouldTransferSol(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("from");
        solana.createKeyPair("to");

        solana.airdrop("from", "0.01");

        solana.transfer("from", "to", "0.01", "payer");

        solana.balance("from", "0.09");
        solana.balance("to", "0.01");
    }
}
