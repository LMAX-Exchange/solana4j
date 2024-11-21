package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterisation.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
final class BpfLoaderUpgradeableProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdropSol("payer", "0.01");
    }

    @ParameterizedMessageEncodingTest
    void shouldSetUpgradeAuthority(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("newAuthority");

        // see solana-run.sh for where these values come from
        solana.verifyBpfUpgradeableAccount(
                "<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>",
                "<13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d>");

        solana.setBpfUpgradeableProgramUpgradeAuthority(
                "<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>",
                "<13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d>",
                "<Ed4gbZARwspMoyVRPU3GvCCARFtRSPSw2TqtHvH6vvj8>",
                "newAuthority",
                "payer");


        solana.verifyBpfUpgradeableAccount(
                "<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>",
                "newAuthority");
    }

    @AfterEach
    void afterEachTest()
    {
        solana.setBpfUpgradeableProgramUpgradeAuthority(
                "<CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT>",
                "newAuthority",
                "newAuthority",
                "<13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d>",
                "payer");
    }
}
