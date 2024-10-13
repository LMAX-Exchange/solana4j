#### Why does this README.md exist?

Well, Solana Labs release multiple different versions of the Solana validator, but they _don't_ release
 `solana-release-aarch64-unknown-linux-gnu.tar.bz2`, which is rather irritating. In order
to run a `solana-test-validator` on an `aarch64` based architecture, 
in particular on an `Apple M[1,2,3,4]` chip, _within_ a `Docker` container, you need it.  

The `Github` runner at `https://github.com/LMAX-Exchange/solana4j` runs on an `x86_64` architecture, so that will remain the default in this project. However, 
in the interest that all computer architectures should be treated equally, I have provided a way that you can build your own
image, on a `Apple M[1,2,3,4]` based laptop, and run the integration tests.  

I will not check the artifact into source, but allow the user to generate it themselves and then reference the `Aarch64Dockerfile` in 
`IntegrationTestBase.java`; be careful to not actually commit these changes, as the pipeline in `Github` will fail. The code change in `IntegrationTestBase.java`
is as follows:

```java
    SOLANA_VALIDATOR = new GenericContainer<>(new ImageFromDockerfile().withDockerfile(Path.of(MountableFile.forClasspathResource("Aarch64Dockerfile").getFilesystemPath())))
```

In order to build `solana-release-x86_64-unknown-linux-gnu.tar-1.18.25.bz2`, you should run the script `build_aarch64_solana_image.sh`. It will put the artifact exactly where you need
it, if you run the script from `src/integration-test/resources`, as you should. You may need to beef up the resources on 
`Docker Desktop`, particularly the `Virtual disk limit`, in order to do this. This may take up to 10-15 minutes, but thankfully only has to be done once.
Running that script and making the above code change, should be enough to run the integration tests locally on an `Apple M[1,2,3,4]` computer.