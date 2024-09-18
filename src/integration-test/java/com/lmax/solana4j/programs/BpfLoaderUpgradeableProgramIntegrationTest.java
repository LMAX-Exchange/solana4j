package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterization.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
public class BpfLoaderUpgradeableProgramIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdrop("payer", "0.01");
    }

    @ParameterizedMessageEncodingTest
    void shouldSetUpgradeAuthority(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("newAuthority");

        // see solana-run.sh for where these values come from
        solana.upgradeAuthority("<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>", "<13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d>");

        solana.setUpgradeAuthority(
                "<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>",
                "<13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d>",
                "<Ed4gbZARwspMoyVRPU3GvCCARFtRSPSw2TqtHvH6vvj8>",
                "newAuthority",
                "payer");

        solana.upgradeAuthority("<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>", "newAuthority");
    }

    @AfterEach
    void afterEachTest()
    {
        solana.setUpgradeAuthority(
                "<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>",
                "newAuthority",
                "newAuthority",
                "<13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d>",
                "payer");
    }
}
