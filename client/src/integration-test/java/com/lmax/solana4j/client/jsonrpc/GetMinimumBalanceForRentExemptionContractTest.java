package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getminimumbalanceforrentexemption
class GetMinimumBalanceForRentExemptionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetMinimumBalanceForRentExemption() throws SolanaJsonRpcClientException
    {
        assertThat(api.getMinimumBalanceForRentExemption(1000).getResponse()).isGreaterThan(0L);
    }

    @Test
    void shouldReturnErrorForNegativeSize() throws SolanaJsonRpcClientException
    {
        final var response = api.getMinimumBalanceForRentExemption(-1);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid params: invalid value: integer `-1`, expected usize.");
    }
}
