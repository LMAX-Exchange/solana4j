package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getaccountinfo
final class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetAccountInfoDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var accountInfo = SOLANA_API.getAccountInfo(TOKEN_MINT).getResponse();

        assertThat(accountInfo.getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "AQAAAFA1CQIm+hHzTXNa/YZ1Z19+UKIaAOLFJLLDvQODwCs1Cg" +
                        "AAAAAAAAASAQEAAAB6OXSiO1mUI304QU8IJGTYsmjXkEECbyV3Ar74PD8D6Q==");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(500000L);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getSpace()).isEqualTo(82L);
    }

    @Test
    void shouldGetAccountInfoBase58EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final var accountInfo = SOLANA_API.getAccountInfo(TOKEN_MINT, optionalParams).getResponse();

        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "DK9N1s2buYcYyPKFQH2cEHDY4vjQAfXCXCs6YvHFPJRB52sVXJr" +
                        "Wzn4WR9zajYpY4tzroKfpkqCbx3KL9QnuS6oL7PYWUbxsYPovr4KKChSMbMi");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base58");
    }

    @Test
    void shouldGetAccountInfoBase64ZstdEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64+zstd");

        final var accountInfo = SOLANA_API.getAccountInfo(TOKEN_MINT, optionalParams).getResponse();

        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "KLUv/QBYkQIAAQAAAFA1CQIm+hHzTXNa/YZ1Z19+UKIaAOLFJLLDvQODwC" +
                        "s1CgAAAAAAAAASAQEAAAB6OXSiO1mUI304QU8IJGTYsmjXkEECbyV3Ar74PD8D6Q==");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base64+zstd");
    }

    @Test
    void shouldGetTokenAccountInfoJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var accountInfo = SOLANA_API.getAccountInfo(TOKEN_ACCOUNT_1, optionalParams).getResponse();
        assertThat(accountInfo.getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");

        // program specific parser so we can try and read the data that's returned
        assertThat(accountInfo.getData().getAccountInfoEncoded()).isNull();

        final var parsedAccountInfo = accountInfo.getData().getAccountInfoParsed();
        assertThat(parsedAccountInfo.getProgram()).isEqualTo("spl-token-2022");
        assertThat(parsedAccountInfo.getSpace()).isEqualTo(165);

        // i think the best we can do here is really just return a Map<String, Object> and let the user do their own parsing
        // since the parsing is very much program specific
        assertThat(parsedAccountInfo.getParsedData().get("type")).isEqualTo("account");
        assertThat(parsedAccountInfo.getParsedData().get("info")).usingRecursiveComparison().isEqualTo(
                Map.of("isNative", false,
                        "mint", "2tokpcExDmewsSNRKuTLVLMUseiSkEdBQWBjeQLmuFaS",
                        "owner", "7H1itW7F72uJbaXK2R4gP7J18HrQ2M683kL9YgUeeUHr",
                        "state", "initialized",
                        "tokenAmount", Map.of(
                                "amount", "10",
                                "decimals", 18,
                                "uiAmount", 1.0E-17,
                                "uiAmountString", "0.00000000000000001"
                        ))
        );
    }

    @Test
    void shouldGetMintAccountInfoJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var accountInfo = SOLANA_API.getAccountInfo(TOKEN_MINT, optionalParams).getResponse();
        assertThat(accountInfo.getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");

        // program specific parser so we can try and read the data that's returned
        assertThat(accountInfo.getData().getAccountInfoEncoded()).isNull();

        final var parsedAccountInfo = accountInfo.getData().getAccountInfoParsed();
        assertThat(parsedAccountInfo.getProgram()).isEqualTo("spl-token-2022");
        assertThat(parsedAccountInfo.getSpace()).isEqualTo(82);

        // i think the best we can do here is really just return a Map<String, Object> and let the user do their own parsing
        // since the parsing is very much program specific
        assertThat(parsedAccountInfo.getParsedData().get("type")).isEqualTo("mint");
        assertThat(parsedAccountInfo.getParsedData().get("info"))
                .usingRecursiveComparison()
                .ignoringFields("supply").isEqualTo(
                Map.of("decimals", 18,
                        "freezeAuthority", "9E7Z5oSAiwBvchbUjm3E9x2BTn8RzXyyZdaAjpBJvuUc",
                        "isInitialized", true,
                        "mintAuthority", "6Q6XBfRrdf6jrK2DraQ8XnYzkGsFz9c15DdUKS5aJHoJ",
                        "supply", "10")
        );
    }

    @Test
    void shouldGetAccountInfoDataSliceOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");
        optionalParams.addParam("dataSlice", Map.of("length", 10, "offset", 10));

        final var accountInfo = SOLANA_API.getAccountInfo(TOKEN_MINT, optionalParams).getResponse();

        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo("21VYjtQD6JiatN");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base58");
    }

    @Test
    void shouldReturnNullForUnknownAccount() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.getAccountInfo("9yznQg77FHgGqrcf5V9CMKXQ9tcCt4omVW6NbHesWyog");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).isNull();
    }

    @Test
    void shouldReturnErrorForMalformedAccount() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.getAccountInfo("iamnotarealaccount");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = SOLANA_API.getAccountInfo(TOKEN_MINT, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
