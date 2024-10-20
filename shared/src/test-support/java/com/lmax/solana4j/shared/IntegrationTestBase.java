package com.lmax.solana4j.shared;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
            final Path solanaRunPath = copyResourceToTempFile(parentDirectory, "solana-run.sh");
            final Path fetchSplPath = copyResourceToTempFile(parentDirectory, "fetch-spl.sh");
            final Path lmaxMultisigPath = copyResourceToTempFile(parentDirectory, "lmax_multisig.so");
            final Path upgradeAuthorityPath = copyResourceToTempFile(parentDirectory, "upgrade_authority.json");
            final Path bpfProgramPath = copyResourceToTempFile(parentDirectory, "bpf_program.json");

            SOLANA_VALIDATOR = new GenericContainer<>(new ImageFromDockerfile().withDockerfile(dockerfilePath))
                    .withCopyFileToContainer(MountableFile.forHostPath(solanaRunPath), "/solana-run.sh")
                    .withCopyFileToContainer(MountableFile.forHostPath(fetchSplPath), "/fetch-spl.sh")
                    .withCopyFileToContainer(MountableFile.forHostPath(lmaxMultisigPath), "/lmax_multisig.so")
                    .withCopyFileToContainer(MountableFile.forHostPath(upgradeAuthorityPath), "/upgrade_authority.json")
                    .withCopyFileToContainer(MountableFile.forHostPath(bpfProgramPath), "/bpf_program.json")
                    .withExposedPorts(SOLANA_HTTP_PORT, SOLANA_WS_PORT)
                    .withEnv("SOLANA_RUN_SH_VALIDATOR_ARGS", "--ticks-per-slot=8")
                    .withNetwork(NETWORK);

            if (arch.equals("aarch64"))
            {
                try
                {
                    final Path solanaReleasePath = copyResourceToTempFile(parentDirectory, "solana-release-aarch64-unknown-linux-gnu-1.18.25.tar.bz2");
                    SOLANA_VALIDATOR.withCopyFileToContainer(MountableFile.forHostPath(solanaReleasePath), "/solana-release-aarch64-unknown-linux-gnu-1.18.25.tar.bz2");
                }
                catch (final Exception e)
                {
                    throw new RuntimeException("Cannot find solana-release-aarch64-unknown-linux-gnu-1.18.25.tar.bz2, are you sure you've run BuildMeAnAarch64CompliantSolanaDockerImagePleaseDockerfile?");
                }
            }

            SOLANA_VALIDATOR.start();
        }
        catch (final RuntimeException | IOException e)
        {
            throw new RuntimeException("Something went wrong in the test set-up.", e);
        }
    }

    private static Path copyResourceToTempFile(final Path parent, final String name) throws IOException
    {
        final Path tempFile = Files.createTempFile(parent, name, "");
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
