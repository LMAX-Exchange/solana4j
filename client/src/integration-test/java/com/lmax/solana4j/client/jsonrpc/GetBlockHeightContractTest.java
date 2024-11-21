package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getblockheight
final class GetBlockHeightContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetBlockHeight() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getBlockHeight().getResponse()).isGreaterThan(0L);
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = SOLANA_API.getBlockHeight(optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
