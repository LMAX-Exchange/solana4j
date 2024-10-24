FROM anzaxyz/agave:v2.0.14

USER root

COPY solana-run.sh /usr/bin/solana-run.sh

RUN chmod +x /usr/bin/solana-run.sh

ENTRYPOINT ["/usr/bin/solana-run.sh"]