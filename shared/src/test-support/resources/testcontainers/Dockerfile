ARG SOLANA_VERSION
FROM anzaxyz/agave:v$SOLANA_VERSION

USER root

COPY solana-run.sh /solana-run.sh
COPY lmax_multisig.so /lmax_multisig.so
COPY upgrade_authority.json /upgrade_authority.json
COPY bpf_program.json /bpf_program.json
COPY accounts /accounts/

COPY solana-run.sh /usr/bin/solana-run.sh
RUN chmod +x /usr/bin/solana-run.sh

ENTRYPOINT ["/usr/bin/solana-run.sh"]