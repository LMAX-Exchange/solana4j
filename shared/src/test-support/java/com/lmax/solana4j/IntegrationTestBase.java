package com.lmax.solana4j;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class IntegrationTestBase
{
    public static final int SOLANA_HTTP_PORT = 8899;
    public static final int SOLANA_WS_PORT = 8900;
    public static final Network NETWORK = Network.newNetwork();
    protected static final GenericContainer<?> SOLANA_VALIDATOR;

    protected String rpcUrl;

    static
    {
        try
        {
            final String arch = System.getProperty("os.arch");

            // unfortunately have to do this because of a bug in docker-java when using files from resources of another jar
            final Path parentDirectory = Files.createTempDirectory("integrationTests");
            final Path dockerfilePath = copyResourceToTempFile(parentDirectory, arch.equals("aarch64") ? "Aarch64Dockerfile" : "Dockerfile");

            copyResourceToTempFile(parentDirectory, "solana-run.sh");
            copyResourceToTempFile(parentDirectory, "fetch-spl.sh");
            copyResourceToTempFile(parentDirectory, "lmax_multisig.so");
            copyResourceToTempFile(parentDirectory, "upgrade_authority.json");
            copyResourceToTempFile(parentDirectory, "bpf_program.json");
            copyResourceToTempFile(parentDirectory, "accounts/payer.json");
            copyResourceToTempFile(parentDirectory, "accounts/associated_token_account.json");
            copyResourceToTempFile(parentDirectory, "accounts/multisig.json");
            copyResourceToTempFile(parentDirectory, "accounts/nonce_account.json");
            copyResourceToTempFile(parentDirectory, "accounts/token_account.json");
            copyResourceToTempFile(parentDirectory, "accounts/token_mint.json");

            if (arch.equals("aarch64"))
            {
                try
                {
                    copyResourceToTempFile(parentDirectory, "solana-release-aarch64-unknown-linux-gnu-1.18.25.tar.bz2");
                }
                catch (final Exception e)
                {
                    throw new RuntimeException(
                            "Cannot find solana-release-aarch64-unknown-linux-gnu-1.18.25.tar.bz2, " +
                            "are you sure you've run BuildMeAnAarch64CompliantSolanaDockerImagePleaseDockerfile?");
                }
            }

            SOLANA_VALIDATOR = new GenericContainer<>(new ImageFromDockerfile().withDockerfile(dockerfilePath))
                    .withExposedPorts(SOLANA_HTTP_PORT, SOLANA_WS_PORT)
                    .withEnv("SOLANA_RUN_SH_VALIDATOR_ARGS", "--ticks-per-slot=8")
                    .withNetwork(NETWORK)
                    .withStartupTimeout(Duration.of(10, ChronoUnit.MINUTES));

            SOLANA_VALIDATOR.start();
        }
        catch (final RuntimeException | IOException e)
        {

            throw new RuntimeException("Something went wrong in the test set-up.", e);
        }
    }

    private static Path copyResourceToTempFile(final Path parent, final String name) throws IOException
    {
        final List<String> subDirectoryFullRoute = Arrays.asList(name.split("/"));
        final String subDirectoryRouteMinusFileName = String.join("/", subDirectoryFullRoute.subList(0, subDirectoryFullRoute.size() - 1));

        Files.createDirectories(parent.resolve(subDirectoryRouteMinusFileName));

        final Path tempFile = Files.createFile(parent.resolve(name));
        Files.copy(Objects.requireNonNull(IntegrationTestBase.class.getResourceAsStream("/testcontainers/" + name)), tempFile, StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    @BeforeEach
    public void setup()
    {
        try
        {
            final Integer mappedPort = SOLANA_VALIDATOR.getMappedPort(SOLANA_HTTP_PORT);
            final String solanaHost = SOLANA_VALIDATOR.getHost();

            rpcUrl = "http://" + solanaHost + ':' + mappedPort;
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Something went wrong in the test set-up", e);
        }
    }
}
