package com.lmax.solana4j.programs;

import com.lmax.solana4j.SolanaNodeDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runners.Parameterized;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.Arrays;
import java.util.List;

public class SolanaValidatorIntegrationTestBase
{
    public static final int SOLANA_HTTP_PORT = 8899;
    public static final int SOLANA_WS_PORT = 8900;
    public static final Network NETWORK = Network.newNetwork();
    protected static final GenericContainer<?> SOLANA_VALIDATOR;

    protected SolanaNodeDsl solana;

    static
    {
        try
        {
            SOLANA_VALIDATOR = new GenericContainer<>("solanalabs/solana:v1.17.34")
                    .withExposedPorts(SOLANA_HTTP_PORT, SOLANA_WS_PORT)
                    .withCommand("--ticks-per-slot=8")
                    .withNetwork(NETWORK);

            SOLANA_VALIDATOR.start();
        }
        catch (final RuntimeException e)
        {
            throw new RuntimeException("Something went wrong in the test set-up.", e);
        }
    }

    @Parameterized.Parameters(name = "Message Encoding: {0}")
    public static List<Object> data()
    {
        return Arrays.asList(
                new Object[]{"V0"},
                new Object[]{"Legacy"}
        );
    }

    @Parameterized.Parameter(0)
    public String messageEncoding;

    @BeforeEach
    public void setup()
    {
        try
        {
            final Integer mappedPort = SOLANA_VALIDATOR.getMappedPort(SOLANA_HTTP_PORT);
            final String solanaHost = SOLANA_VALIDATOR.getHost();
            final String rpcUrl = "http://" + solanaHost + ':' + mappedPort;

            solana = new SolanaNodeDsl(rpcUrl);

            // TODO: implement test parameterization - or maybe not, just have two subclasses to this which sets the encoding only for relevant tests
            messageEncoding = "V0";
            if (messageEncoding.equals("V0"))
            {
                solana.setMessageEncoding("V0");
            }
            else if (messageEncoding.equals("Legacy"))
            {
                solana.setMessageEncoding("Legacy");
            }
            else
            {
                throw new RuntimeException("Unknown message encoding.");
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Something went wrong in the test set-up", e);
        }

        // Solana test validator has no fees until the first transaction is finalised, therefore making tests intermittent
        // See: https://github.com/solana-labs/solana/blob/29f776c676ed4676f64bff927da87cdab0672878/test-validator/src/lib.rs#L1007
        solana.waitForSlot("35");
    }
}
