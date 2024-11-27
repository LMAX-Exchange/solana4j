#!/bin/bash
set -xeou pipefail

trap "docker rm --force solana-aarch64-build-container" EXIT

solana_version=${1:-}

if [ -z "$solana_version" ]; then
  echo "Error: solana_version is not provided as an argument. Please provide a version."
  exit 1
fi

docker build -t solana-aarch64-build-image -f BuildMeAnAarch64CompliantSolanaDockerImagePleaseDockerfile --build-arg SOLANA_VERSION=$solana_version .

docker run -d --name solana-aarch64-build-container solana-aarch64-build-image

docker cp solana-aarch64-build-container:/home/solana/solana-release-aarch64-unknown-linux-gnu-$solana_version.tar.bz2 .

docker rm --force solana-aarch64-build-container