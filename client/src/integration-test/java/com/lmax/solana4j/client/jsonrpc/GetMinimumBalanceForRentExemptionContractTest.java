package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getminimumbalanceforrentexemption
final class GetMinimumBalanceForRentExemptionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetMinimumBalanceForRentExemption() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getMinimumBalanceForRentExemption(1000).getResponse()).isGreaterThan(0L);
    }

    @Test
    void shouldReturnErrorForNegativeSize() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("commitment", Commitment.PROCESSED.name().toLowerCase(Locale.UK));

        final var response = SOLANA_API.getMinimumBalanceForRentExemption(-1, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid params: invalid value: integer `-1`, expected usize.");
    }
}
