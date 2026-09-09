ARG SOLANA_VERSION
FROM python:3-slim AS extractor

COPY solana-release-x86_64-unknown-linux-gnu-*.tar.bz2 /tmp/
RUN python3 -c "import glob, tarfile; f=glob.glob('/tmp/solana-release-x86_64-unknown-linux-gnu-*.tar.bz2')[0]; tarfile.open(f, 'r:bz2').extractall('/opt/')" && rm /tmp/solana-release-x86_64-unknown-linux-gnu-*.tar.bz2

FROM ubuntu:22.04

COPY --from=extractor /opt/solana-release /opt/solana-release
ENV PATH="/opt/solana-release/bin:${PATH}"

COPY solana-run.sh /usr/bin/solana-run.sh
COPY lmax_multisig.so /lmax_multisig.so
COPY upgrade_authority.json /upgrade_authority.json
COPY bpf_program.json /bpf_program.json
COPY accounts /accounts/

RUN chmod +x /usr/bin/solana-run.sh

ENTRYPOINT ["/usr/bin/solana-run.sh"]
