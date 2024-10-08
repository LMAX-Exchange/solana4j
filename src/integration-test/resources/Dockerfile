FROM ubuntu:20.04

USER root

RUN apt-get update && apt-get install -y wget curl

ENV SOLANA_VERSION v1.18.25
ENV SOLANA_TAR solana-release-x86_64-unknown-linux-gnu.tar.bz2
ENV SOLANA_URL https://github.com/solana-labs/solana/releases/download/$SOLANA_VERSION/$SOLANA_TAR

RUN wget $SOLANA_URL && tar -xvjf $SOLANA_TAR && rm -f $SOLANA_TAR && mv solana-release/bin/* /usr/local/bin/

COPY solana-run.sh /usr/bin/solana-run.sh
RUN chmod +x /usr/bin/solana-run.sh

COPY fetch-spl.sh /usr/bin/fetch-spl.sh
RUN chmod +x /usr/bin/fetch-spl.sh

ENTRYPOINT ["/usr/bin/solana-run.sh"]