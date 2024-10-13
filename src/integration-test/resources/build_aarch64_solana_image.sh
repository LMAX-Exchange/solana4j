#!/bin/bash
set -xeou pipefail

trap "docker rm --force solana-aarch64-build-container" EXIT

solana_version=${1:-1.18.25}

docker build -t solana-aarch64-build-image -f BuildMeAnAarch64CompliantSolanaDockerImagePleaseDockerfile --build-arg SOLANA_VERSION=$solana_version .

docker run -d --name solana-aarch64-build-container solana-aarch64-build-image

docker cp solana-aarch64-build-container:/home/solana/solana-release-aarch64-unknown-linux-gnu-$solana_version.tar.bz2 .

docker rm --force solana-aarch64-build-container