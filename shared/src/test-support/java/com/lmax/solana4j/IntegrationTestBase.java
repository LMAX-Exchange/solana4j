package com.lmax.solana4j;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class IntegrationTestBase
{
    private static final int SOLANA_HTTP_PORT = 8899;
    private static final int SOLANA_WS_PORT = 8900;
    private static final Network NETWORK = Network.newNetwork();
    private static final GenericContainer<?> SOLANA_VALIDATOR;
    private static final Path CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "solana4j");

    protected static String solanaRpcUrl;

    static
    {
        try
        {
            final String arch = System.getProperty("os.arch");

            // unfortunately have to do this because of a bug in docker-java when using files from resources of another jar
            final Path parentDirectory = Files.createTempDirectory("integrationTests");
            final Path dockerfilePath = copyResourceToTempFile(parentDirectory, arch.equals("aarch64") ? "Aarch64Dockerfile" : "Dockerfile");

            copyResourceToTempFile(parentDirectory, "solana-run.sh");
            copyResourceToTempFile(parentDirectory, "lmax_multisig.so");
            copyResourceToTempFile(parentDirectory, "upgrade_authority.json");
            copyResourceToTempFile(parentDirectory, "bpf_program.json");
            copyResourceToTempFile(parentDirectory, "accounts/sol_address.json");
            copyResourceToTempFile(parentDirectory, "accounts/token_mint.json");
            copyResourceToTempFile(parentDirectory, "accounts/token_account_1.json");
            copyResourceToTempFile(parentDirectory, "accounts/token_account_2.json");
            copyResourceToTempFile(parentDirectory, "accounts/nonce_account.json");

            final String solanaVersion = System.getProperty("solana.version");

            downloadSolanaRelease(parentDirectory, solanaVersion);

            SOLANA_VALIDATOR = new GenericContainer<>(new ImageFromDockerfile().withDockerfile(dockerfilePath).withBuildArg("SOLANA_VERSION", solanaVersion))
                    .withExposedPorts(SOLANA_HTTP_PORT, SOLANA_WS_PORT)
                    .withEnv("SOLANA_RUN_SH_VALIDATOR_ARGS", "--ticks-per-slot=8")
                    .withNetwork(NETWORK)
                    .withPrivilegedMode(true)
                    .withStartupTimeout(Duration.of(10, ChronoUnit.MINUTES));

            SOLANA_VALIDATOR.start();

            final Integer mappedPort = SOLANA_VALIDATOR.getMappedPort(SOLANA_HTTP_PORT);
            final String solanaHost = SOLANA_VALIDATOR.getHost();

            solanaRpcUrl = "http://" + solanaHost + ':' + mappedPort;
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Something went wrong in the test set-up.", e);
        }
    }

    private static Path copyResourceToTempFile(final Path parent, final String name) throws IOException
    {
        final Path fullSubDirectoryPath = parent.resolve(name);

        Files.createDirectories(fullSubDirectoryPath.getParent());
        final Path tempFile = Files.createFile(fullSubDirectoryPath);

        try (InputStream resourceStream = IntegrationTestBase.class.getResourceAsStream("/testcontainers/" + name))
        {
            if (resourceStream == null)
            {
                throw new IOException("Resource not found: /testcontainers/" + name);
            }
            Files.copy(resourceStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    private static void downloadSolanaRelease(final Path contextDir, final String solanaVersion) throws IOException
    {
        final String filename = "solana-release-x86_64-unknown-linux-gnu-" + solanaVersion + ".tar.bz2";
        final String url = "https://github.com/anza-xyz/agave/releases/download/v" + solanaVersion + "/solana-release-x86_64-unknown-linux-gnu.tar.bz2";
        downloadToContext(contextDir, filename, url);
    }

    private static void downloadToContext(final Path contextDir, final String filename, final String url) throws IOException
    {
        final Path target = contextDir.resolve(filename);
        if (Files.exists(target))
        {
            return;
        }

        final Path cached = CACHE_DIR.resolve(filename);
        if (!Files.exists(cached))
        {
            Files.createDirectories(CACHE_DIR);
            System.out.println("Downloading " + filename + " from " + url);
            try (InputStream in = new URL(url).openStream())
            {
                Files.copy(in, cached, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        Files.copy(cached, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
