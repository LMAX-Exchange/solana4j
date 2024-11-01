package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getaccountinfo
class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetAccountInfo() throws SolanaJsonRpcClientException
    {
        final var accountInfo = api.getAccountInfo(payerAccount).getResponse();

        assertThat(accountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
        assertThat(accountInfo.getData().get(0)).isEqualTo("");
        assertThat(accountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(600000L);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getSpace()).isEqualTo(0);
    }

    @Test
    void shouldGetAccountInfoWithOptionalParams() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams params = new SolanaJsonRpcClientOptionalParams();
        params.addParam("commitment", Commitment.FINALIZED.name().toLowerCase(Locale.UK));
        params.addParam("encoding", "base64");
        params.addParam("dataSlice", Map.of("length", 10, "offset", 0));
        params.addParam("minContextSlot", 1);

        final var accountInfo = api.getAccountInfo(payerAccount, params).getResponse();

        assertThat(accountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
        assertThat(accountInfo.getData().get(0)).isEqualTo("");
        assertThat(accountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(600000L);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getSpace()).isEqualTo(0);
    }

    @Test
    void shouldGetTokenAccountInfo() throws SolanaJsonRpcClientException
    {
        final var tokenAccountInfo = api.getAccountInfo(tokenAccount).getResponse();

        assertThat(tokenAccountInfo.getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
        assertThat(tokenAccountInfo.getData().get(0)).isEqualTo(
                "DiEMlfEgHelAEAAXQrGw4/3hg4TDygNdPgaE1JMaak97Idi7jw+W6DSraz1ENBeREhm4FdP/BAJ9rBwqRQtkB2Q" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        assertThat(tokenAccountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(tokenAccountInfo.getLamports()).isEqualTo(400000L);
        assertThat(tokenAccountInfo.isExecutable()).isEqualTo(false);
        assertThat(tokenAccountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(tokenAccountInfo.getSpace()).isEqualTo(165);
    }

    @Test
    void shouldGetTokenAccountInfoWithOptionalParams() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams params = new SolanaJsonRpcClientOptionalParams();
        params.addParam("commitment", Commitment.FINALIZED.name().toLowerCase(Locale.UK));
        params.addParam("encoding", "base64");
        params.addParam("dataSlice", Map.of("length", 10, "offset", 10));
        params.addParam("minContextSlot", 1);

        final var tokenAccountInfo = api.getAccountInfo(tokenAccount, params).getResponse();

        assertThat(tokenAccountInfo.getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
        assertThat(tokenAccountInfo.getData().get(0)).isEqualTo("ABdCsbDj/eGDhA==");
        assertThat(tokenAccountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(tokenAccountInfo.getLamports()).isEqualTo(400000L);
        assertThat(tokenAccountInfo.isExecutable()).isEqualTo(false);
        assertThat(tokenAccountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(tokenAccountInfo.getSpace()).isEqualTo(165);
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForUnknownAccount()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedAccount()
    {

    }
}
