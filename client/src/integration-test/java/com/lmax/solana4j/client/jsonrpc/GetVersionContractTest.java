package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetVersionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetFeatureSet() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getVersion().getResponse().getFeatureSet()).isGreaterThan(0L);
    }

    @Test
    void shouldGetVersion() throws SolanaJsonRpcClientException
    {
        // Unless we write a stub, we can't really hardcode the value here
        // A regular expression to check it's at least a valid semver will have to do
        assertThat(SOLANA_API.getVersion().getResponse().getVersion()).matches("\\d+\\.\\d+\\.\\d+");
    }
}
