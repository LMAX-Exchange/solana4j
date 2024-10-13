package com.lmax.solana4j.programs;

import com.lmax.solana4j.SolanaNodeDsl;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.time.Duration;

public abstract class IntegrationTestBase
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
            SOLANA_VALIDATOR = new GenericContainer<>(new ImageFromDockerfile().withDockerfile(Path.of(MountableFile.forClasspathResource("Aarch64Dockerfile").getFilesystemPath())))
                    .withCopyFileToContainer(MountableFile.forClasspathResource("solana-run.sh"), "/solana-run.sh")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("fetch-spl.sh"), "/fetch-spl.sh")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("lmax_multisig.so"), "/lmax_multisig.so")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("upgrade_authority.json"), "/upgrade_authority.json")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("bpf_program.json"), "/bpf_program.json")
                    .withExposedPorts(SOLANA_HTTP_PORT, SOLANA_WS_PORT)
                    .withEnv("SOLANA_RUN_SH_VALIDATOR_ARGS", "--ticks-per-slot=8")
                    .withNetwork(NETWORK)
                    .withStartupTimeout(Duration.ofMinutes(2));

            SOLANA_VALIDATOR.start();
        }
        catch (final RuntimeException e)
        {
            throw new RuntimeException("Something went wrong in the test set-up.", e);
        }
    }

    @BeforeEach
    public void setup()
    {
        try
        {
            final Integer mappedPort = SOLANA_VALIDATOR.getMappedPort(SOLANA_HTTP_PORT);
            final String solanaHost = SOLANA_VALIDATOR.getHost();
            final String rpcUrl = "http://" + solanaHost + ':' + mappedPort;

            solana = new SolanaNodeDsl(rpcUrl);
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
