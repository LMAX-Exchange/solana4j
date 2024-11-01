package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetTokenAccountBalanceContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetTokenAccountBalance() throws SolanaJsonRpcClientException
    {
        final var tokenAccountBalance = api.getTokenAccountBalance(tokenAccount).getResponse();

        assertThat(tokenAccountBalance.getAmount()).isEqualTo("100");
        assertThat(tokenAccountBalance.getDecimals()).isEqualTo(18);
        assertThat(tokenAccountBalance.getUiAmountString()).isEqualTo("0.0000000000000001");
        assertThat(tokenAccountBalance.getUiAmount()).isEqualTo(0.0000000000000001f);
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForNonTokenAccountBalance()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForUnknownTokenAccountBalance()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedTokenAccountBalance()
    {

    }

}