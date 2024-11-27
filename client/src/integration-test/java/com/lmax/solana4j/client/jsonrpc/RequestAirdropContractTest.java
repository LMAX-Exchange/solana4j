package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/requestairdrop
final class RequestAirdropContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldRequestAirdrop() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.requestAirdrop(PAYER, Sol.lamports(BigDecimal.ONE)).getResponse()).isNotBlank();
    }

    @Test
    void shouldReturnErrorForMalformedAccount() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("commitment", Commitment.PROCESSED.name().toLowerCase(Locale.UK));

        final var response = SOLANA_API.requestAirdrop("iamnotarealaccount", Sol.lamports(BigDecimal.ONE), optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }
}
