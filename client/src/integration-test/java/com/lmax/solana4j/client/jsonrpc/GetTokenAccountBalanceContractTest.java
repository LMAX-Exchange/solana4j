package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/gettokenaccountbalance
final class GetTokenAccountBalanceContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetTokenAccountBalance() throws SolanaJsonRpcClientException
    {
        final var tokenAccountBalance = SOLANA_API.getTokenAccountBalance(TOKEN_ACCOUNT_1).getResponse();

        assertThat(tokenAccountBalance.getAmount()).isEqualTo("10");
        assertThat(tokenAccountBalance.getDecimals()).isEqualTo(18);
        assertThat(tokenAccountBalance.getUiAmountString()).isEqualTo("0.00000000000000001");
        assertThat(tokenAccountBalance.getUiAmount()).isEqualTo(0.00000000000000001f);
    }

    @Test
    void shouldReturnErrorForUnknownAccount() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.getTokenAccountBalance("4zDkK3Y6HBxzJ9tqWbZ9RG5aPGRJ5B7G4VQV67gHUMoP");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: could not find account");
    }

    @Test
    void shouldReturnErrorForNonTokenAccount() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.getTokenAccountBalance(SOL_ACCOUNT);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: not a Token account");
    }

    @Test
    void shouldReturnErrorForMalformedTokenAccount() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("commitment", Commitment.PROCESSED.name().toLowerCase(Locale.UK));

        final var response = SOLANA_API.getTokenAccountBalance("iamnotarealaccount", optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }
}