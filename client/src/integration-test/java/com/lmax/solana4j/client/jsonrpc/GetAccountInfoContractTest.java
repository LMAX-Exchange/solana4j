package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getaccountinfo
class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetAccountInfoDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var accountInfo = api.getAccountInfo(nonceAccount).getResponse();

        assertThat(accountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "AQAAAAEAAAAYN7fDgyW630LDtnup4nlG5uUrhnBS077hAkv" +
                        "7jaK5U/Z1Da4WA4qlaCl2RvtERjayxzWWD30+4/7zXET6S+5XiBMAAAAAAAA=");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(300000L);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getSpace()).isEqualTo(80L);
    }

    @Test
    void shouldGetAccountInfoBase58EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final var accountInfo = api.getAccountInfo(nonceAccount, optionalParams).getResponse();

        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "df8aQUMTjFJyzt5AbTh4pvdagb6bKFTrhMcdQzDgPTruv" +
                        "sohU21nv5AkScqeYZwoUgJEkKK91g76GGeGSaFF5kHZRDZQewePtXrSQCr4Vt79");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base58");
    }

    @Test
    void shouldGetAccountInfoBase64ZstdEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64+zstd");

        final var accountInfo = api.getAccountInfo(nonceAccount, optionalParams).getResponse();

        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "KLUv/QBYgQIAAQAAAAEAAAAYN7fDgyW630LDtnup4nlG5uUrhnBS077hA" +
                        "kv7jaK5U/Z1Da4WA4qlaCl2RvtERjayxzWWD30+4/7zXET6S+5XiBMAAAAAAAA=");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base64+zstd");
    }

    @Test
    void shouldGetNonceAccountInfoJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var accountInfo = api.getAccountInfo(nonceAccount, optionalParams).getResponse();

        // program specific parser so we can try and read the data that's returned
        assertThat(accountInfo.getData().getAccountInfoEncoded()).isNull();

        final var parsedAccountInfo = accountInfo.getData().getAccountInfoParsed();
        assertThat(parsedAccountInfo.getProgram()).isEqualTo("nonce");
        assertThat(parsedAccountInfo.getSpace()).isEqualTo(80);

        // i think the best we can do here is really just return a Map<String, Object> and let the user do their own parsing
        // since the parsing is very much program specific
        assertThat(parsedAccountInfo.getParsedData().get("type")).isEqualTo("initialized");
        assertThat(parsedAccountInfo.getParsedData().get("info")).usingRecursiveComparison().isEqualTo(
                Map.of("authority", "2dY4b9YdnHaURDVJj339q7eczTdxnVyiGifA5yXG4yQN",
                        "blockhash", "Hb4pM3V4j9ipCsFNpymzViHZPiDMUH2KwLzrpSchiYVU",
                        "feeCalculator", Map.of("lamportsPerSignature", "5000"))
        );
    }

    @Test
    void shouldGetTokenAccountInfoJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var accountInfo = api.getAccountInfo(tokenAccount, optionalParams).getResponse();
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
                        "mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg",
                        "owner", "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                        "state", "initialized",
                        "tokenAmount", Map.of(
                                "amount", "100",
                                "decimals", 18,
                                "uiAmount", 1.0E-16,
                                "uiAmountString", "0.0000000000000001"
                        ))
        );
    }

    @Test
    void shouldGetAssociatedTokenAccountInfoJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var accountInfo = api.getAccountInfo(associatedTokenAccount, optionalParams).getResponse();
        assertThat(accountInfo.getOwner()).isEqualTo("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

        // no program specific parser defaults to base64
        assertThat(accountInfo.getData().getAccountInfoParsed()).isNull();
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "xXyb/Pp4GvdAvHw1VaWrTztH643kq1qJmSF1BCr3lAo0LuCZ23gzm/GK2+lyoRzQOfumbsW3RpLC" +
                        "/2vwv+LS/gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base64");
    }

    @Test
    void shouldGetMultiSigAccountInfoJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var accountInfo = api.getAccountInfo(multiSigAccount, optionalParams).getResponse();
        assertThat(accountInfo.getOwner()).isEqualTo("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

        // no program specific parser defaults to base64
        assertThat(accountInfo.getData().getAccountInfoParsed()).isNull();
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "AwUBcVto0sFH7UdVWpC/7lUXilk/lyHA/07a+si5uLjxF0fsuGdXLxKiOL7Bo50KT" +
                        "WX5z0Ih4yPW7HBXpX6cPS60ZzmXUswuOWhUOBzmerzPb4OyC7MarinH9ukLzXBYxBvU/" +
                        "J+8Zz94sWKDajmiBdER8ECeAXfBVf4V4KvhapejV7PbYZSzg95i+mlo9MqiMQA19Imn3a" +
                        "sahqKtjA4RCoYbFwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAA");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base64");
    }

    @Test
    void shouldGetAccountInfoDataSliceOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");
        optionalParams.addParam("dataSlice", Map.of("length", 10, "offset", 10));

        final var accountInfo = api.getAccountInfo(nonceAccount, optionalParams).getResponse();

        assertThat(accountInfo.getData().getAccountInfoEncoded().get(0)).isEqualTo("BKoYJaZaeRehAi");
        assertThat(accountInfo.getData().getAccountInfoEncoded().get(1)).isEqualTo("base58");
    }

    @Test
    void shouldReturnNullForUnknownAccount() throws SolanaJsonRpcClientException
    {
        final var response = api.getAccountInfo("9yznQg77FHgGqrcf5V9CMKXQ9tcCt4omVW6NbHesWyog");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse()).isNull();
    }

    @Test
    void shouldReturnErrorForMalformedAccount() throws SolanaJsonRpcClientException
    {
        final var response = api.getAccountInfo("iamnotarealaccount");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = api.getAccountInfo(nonceAccount, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
