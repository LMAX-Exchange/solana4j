#### Why does this README.md exist?

Well, Solana Labs release multiple different versions of the Solana validator, but they _don't_ release
 `solana-release-aarch64-unknown-linux-gnu-<version>.tar.bz2`, which is rather irritating. In order
to run a `solana-test-validator` on an `aarch64` based architecture, 
in particular on an `Apple M[1,2,3,4]` chip, _within_ a `Docker` container, you need it.  

The `Github` runner at `https://github.com/LMAX-Exchange/solana4j` runs on an `x86_64` architecture, so that will remain the default in this project. However, 
in the interest that all computer architectures should be treated equally, I have provided a way that you can build your own
image, on a `Apple M[1,2,3,4]` based laptop, and run the integration tests.

In order to build `solana-release-aarch64-unknown-linux-gnu-<version>.tar.bz2`, you should run the script `build_aarch64_solana_image.sh`, with the Solana
version number targeted in `gradle.properties`. It will put the artifact exactly where you need it, if you run the script from `shared/src/test-support/resources/testcontainers`, 
as you should. You may need to beef up the resources on `Docker Desktop`, particularly the `Virtual disk limit`, in order to do this. This may take up to 10-15 minutes, 
but thankfully only has to be done once. Running that script should be enough to run the integration tests locally on an `Apple M[1,2,3,4]` computer. Please be careful NOT to check 
this file into source. You will be warned by git itself because the file is so big when you try to commit your changes, so it should be hard to commit it by accident!