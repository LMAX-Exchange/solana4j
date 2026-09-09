#### Solana Test Validator Docker Setup

This directory contains the Dockerfiles and supporting files used to build a
Docker image running a `solana-test-validator` for integration testing.

## Solana Version

The Solana version is controlled by `SOLANA_VERSION` in `gradle.properties`.
The x86_64 Docker image uses a pre-built release tarball downloaded from
[GitHub releases](https://github.com/anza-xyz/agave/releases).

## Files

### Dockerfile (x86_64)

The main Dockerfile. It extracts the pre-built Solana release tarball into an
`ubuntu:22.04` image. The tarball is downloaded by `IntegrationTestBase.java`
at test time and placed in the Docker build context.

### Aarch64Dockerfile

For aarch64 (e.g. Apple Silicon) there is no pre-built Linux release tarball,
so the `Aarch64Dockerfile` builds Solana from source by cloning the agave
repository and running `cargo-install-all.sh`. The first build takes 15-30
minutes; subsequent runs use the cached Docker image.

### solana-run.sh

The Docker entrypoint. It starts `solana-test-validator` with:
- `--upgradeable-program` to deploy the BPF program at genesis
- `--account-dir /accounts` to load pre-created token accounts
- `--ticks-per-slot=8` (via `SOLANA_RUN_SH_VALIDATOR_ARGS`)

It waits for the RPC to become responsive using `solana slot` (the `solana ping`
command was removed in Solana 4.x).

### BPF Program

`lmax_multisig.so` is a Solana program compiled with `cargo-build-sbf --arch v2`
using platform-tools v1.54 from the Solana 4.2.2 release. It is deployed at
genesis via `--upgradeable-program` with:
- Program ID: `CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT` (`bpf_program.json`)
- Upgrade authority: `13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d` (`upgrade_authority.json`)

In Solana 4.x, runtime `solana program deploy` fails because the validator
detects an SBPF version requirement that is not enabled. Deploying via
`--upgradeable-program` at genesis avoids this issue.

### Pre-created Accounts

The `accounts/` directory contains JSON keypair files for token accounts that
are loaded via `--account-dir /accounts`.

### Building for Aarch64

`BuildMeAnAarch64CompliantSolanaDockerImagePleaseDockerfile` and
`build_aarch64_solana_image.sh` can be used to build an aarch64 release tarball
from source. Run:

```bash
./build_aarch64_solana_image.sh <solana-version>
```

This produces `solana-release-aarch64-unknown-linux-gnu-<version>.tar.bz2`
which can then be used with a modified Dockerfile.

## How It Works

The Docker image is built automatically by testcontainers when the integration
tests run. `IntegrationTestBase.java` copies the Dockerfile and supporting files
to a temp directory, downloads the Solana release tarball, and builds the image.

The container runs with `--privileged` to provide the capabilities required by
the Solana 4.x validator (XDP, io_uring, etc.).
