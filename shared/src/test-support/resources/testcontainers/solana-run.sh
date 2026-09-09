#!/usr/bin/env bash
#
# Run a minimal Solana test-validator cluster.  Ctrl-C to exit.
#
set -e

script_dir="$(readlink -f "$(dirname "$0")")"
cd "$script_dir"

# Verify required programs are available
ok=true
for program in solana-{keygen,test-validator}; do
  $program -V || ok=false
done

$ok || {
  echo
  echo "Unable to locate required programs."
  echo
  exit 1
}

export RUST_LOG=${RUST_LOG:-solana=info,agave=info,solana_runtime::message_processor=debug}
export RUST_BACKTRACE=1
ledgerDir=$PWD/config/ledger
accountDir=/accounts

mkdir -p "$ledgerDir"

set -x

# solana-test-validator creates its own genesis; --upgradeable-program embeds
# the BPF program at genesis time.
args=(
  --ledger "$ledgerDir"
  --gossip-port 8001
  --rpc-port 8899
  --account-dir "$accountDir"
  --upgradeable-program /bpf_program.json /lmax_multisig.so /upgrade_authority.json
)

# shellcheck disable=SC2086
solana-test-validator "${args[@]}" $SOLANA_RUN_SH_VALIDATOR_ARGS &
validator=$!

solana config set --url localhost

set +e
while true; do

  solana slot > /dev/null 2>&1

  if [ $? -eq 0 ]; then
    echo "Solana RPC is responsive!"
    break
  else
    echo "Waiting for Solana RPC to become responsive..."
  fi
  sleep 0.5
done
set -e

# bpf_program: CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT (public key) 5A7mHgUWzqpb9sbyHMzn7dLvdfHGswk5PHxfUu2PTEKR (private key)
# upgrade_authority: 13jT1jL8jpTFzFcZATPq9W4gmRB5uZbEYjXxJJbugB1d (public key) Ed4gbZARwspMoyVRPU3GvCCARFtRSPSw2TqtHvH6vvj8 (private key)
# Program deployed via --upgradeable-program in solana-test-validator args above

wait "$validator"
