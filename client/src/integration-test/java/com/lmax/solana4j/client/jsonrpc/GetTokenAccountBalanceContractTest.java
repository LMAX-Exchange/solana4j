package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/gettokenaccountbalance
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
    void shouldReturnErrorForUnknownAccount() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountBalance("4zDkK3Y6HBxzJ9tqWbZ9RG5aPGRJ5B7G4VQV67gHUMoP");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: could not find account");
    }

    @Test
    void shouldReturnErrorForNonTokenAccount() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountBalance(payerAccount);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: not a Token account");
    }

    @Test
    void shouldReturnErrorForMalformedTokenAccount() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountBalance("iamnotarealaccount");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }
}