#!/usr/bin/env bash
#
# Run a minimal Solana cluster.  Ctrl-C to exit.
#
# Before running this script ensure standard Solana programs are available
# in the PATH, or that cargo build ran successfully
#
set -e

# Prefer possible cargo build binaries over PATH binaries
script_dir="$(readlink -f "$(dirname "$0")")"
if [[ "$script_dir" =~ /scripts$ ]]; then
  cd "$script_dir/.."
else
  cd "$script_dir"
fi


profile=debug
if [[ -n $NDEBUG ]]; then
  profile=release
fi
PATH=$PWD/target/$profile:$PATH

ok=true
for program in solana-{faucet,genesis,keygen,validator}; do
  $program -V || ok=false
done
$ok || {
  echo
  echo "Unable to locate required programs.  Try building them first with:"
  echo
  echo "  $ cargo build --all"
  echo
  exit 1
}

export RUST_LOG=${RUST_LOG:-solana=info,solana_runtime::message_processor=debug} # if RUST_LOG is unset, default to info
export RUST_BACKTRACE=1
dataDir=$PWD/config/"$(basename "$0" .sh)"
ledgerDir=$PWD/config/ledger

SOLANA_RUN_SH_CLUSTER_TYPE=${SOLANA_RUN_SH_CLUSTER_TYPE:-development}

set -x
if ! solana address; then
  echo Generating default keypair
  solana-keygen new --no-passphrase
fi
validator_identity="$dataDir/validator-identity.json"
if [[ -e $validator_identity ]]; then
  echo "Use existing validator keypair"
else
  solana-keygen new --no-passphrase -so "$validator_identity"
fi
validator_vote_account="$dataDir/validator-vote-account.json"
if [[ -e $validator_vote_account ]]; then
  echo "Use existing validator vote account keypair"
else
  solana-keygen new --no-passphrase -so "$validator_vote_account"
fi
validator_stake_account="$dataDir/validator-stake-account.json"
if [[ -e $validator_stake_account ]]; then
  echo "Use existing validator stake account keypair"
else
  solana-keygen new --no-passphrase -so "$validator_stake_account"
fi

if [[ -e "$ledgerDir"/genesis.bin || -e "$ledgerDir"/genesis.tar.bz2 ]]; then
  echo "Use existing genesis"
else
  ./fetch-spl.sh
  if [[ -r spl-genesis-args.sh ]]; then
    SPL_GENESIS_ARGS=$(cat spl-genesis-args.sh)
  fi

  # shellcheck disable=SC2086
  solana-genesis \
    --hashes-per-tick sleep \
    --faucet-lamports 500000000000000000 \
    --bootstrap-validator \
      "$validator_identity" \
      "$validator_vote_account" \
      "$validator_stake_account" \
    --ledger "$ledgerDir" \
    --cluster-type "$SOLANA_RUN_SH_CLUSTER_TYPE" \
    $SPL_GENESIS_ARGS \
    $SOLANA_RUN_SH_GENESIS_ARGS
fi

args=(
  --ledger "$ledgerDir"
  --gossip-port 8001
  --rpc-port 8899
)

# shellcheck disable=SC2086
solana-test-validator "${args[@]}" $SOLANA_RUN_SH_VALIDATOR_ARGS &
validator=$!


solana config set --url "http://localhost:8899"

set +e
while true; do

  solana ping --count 1 > /dev/null 2>&1

  if [ $? -eq 0 ]; then
    echo "Solana RPC is responsive!"
    break
  else
    echo "Waiting for Solana RPC to become responsive..."
  fi
  sleep 0.5
done
set -e

solana program deploy /lmax_multisig.so --program-id /bpf_program.json --upgrade-authority /upgrade_authority.json
#solana program show CxmAHzszTVSWmtBnCXda7eUTemd8DGyax88yYk54A2PT

wait "$validator"

