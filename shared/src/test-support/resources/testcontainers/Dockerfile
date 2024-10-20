FROM solanalabs/solana:v1.18.25

USER root

COPY solana-run.sh /usr/bin/solana-run.sh
RUN chmod +x /usr/bin/solana-run.sh

ENTRYPOINT ["/usr/bin/solana-run.sh"]