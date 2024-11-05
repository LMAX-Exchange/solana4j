package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/minimumledgerslot
class MinimumLedgerSlotContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetMinimumLedgerSlot() throws SolanaJsonRpcClientException
    {
        // test validator so we expect to have all the history at the start of the test ...
        assertThat(api.minimumLedgerSlot().getResponse()).isEqualTo(0L);
    }
}
