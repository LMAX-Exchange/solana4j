package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GetTokenAccountsByOwnerContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetTokenAccountsByOwnerWithMintDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        // 9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG - (is the owner of the token account - see shouldGetTokenAccountInfoJsonParsedEncodingOptionalParam)
        final var response = api.getTokenAccountsByOwner("9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG", Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg")).getResponse();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getPublicKey()).isEqualTo(tokenAccount);
        assertThat(response.get(0).getAccountInfo().getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
    }

    @Test
    void shouldGetTokenAccountsByOwnerDataSliceOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("dataSlice", Map.of("length", 10, "offset", 10));
        optionalParams.addParam("encoding", "base64");

        final var response = api.getTokenAccountsByOwner(
                        "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                        Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"),
                        optionalParams)
                .getResponse();

        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoEncoded().get(0)).isEqualTo("ABdCsbDj/eGDhA==");
        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoEncoded().get(1)).isEqualTo("base64");
        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoParsed()).isNull();
    }

    @Test
    void shouldGetTokenAccountsByOwnerBase58EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final var response = api.getTokenAccountsByOwner(
                "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"),
                optionalParams);

        // base58 encoding account data to be less than 128 bytes - wouldn't this always basically be the case for token accounts?
        // not sure why they'd offer base58 encoding here at all, but whatever
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32600L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Encoded binary (base 58) data should be less than 128 bytes, please use Base64 encoding.");
    }

    @Test
    void shouldGetTokenAccountsByOwnerBase64ZstdEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64+zstd");

        final var response = api.getTokenAccountsByOwner(
                        "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                        Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"),
                        optionalParams)
                .getResponse();

        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoEncoded().get(0)).isEqualTo(
                "KLUv/QBYdQIARAQOIQyV8SAd6UAQABdCsbDj/eGDhMPKA10+BoTUkxpqT" +
                        "3sh2LuPD5boNKtrPUQ0F5ESGbgV0/8EAn2sHCpFC2QHZAABAAIABCcKIXAG");
        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoEncoded().get(1)).isEqualTo("base64+zstd");
        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoParsed()).isNull();
    }

    @Test
    void shouldGetTokenAccountsByOwnerJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var response = api.getTokenAccountsByOwner(
                        "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                        Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"),
                        optionalParams)
                .getResponse();

        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoEncoded()).isNull();
        // already checked these fields in another test
        assertThat(response.get(0).getAccountInfo().getData().getAccountInfoParsed()).isNotNull();
    }

    @Test
    void shouldGetTokenAccountsByOwnerWithProgramIdDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountsByOwner(
                        "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                        Map.entry("programId", "TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb"))
                .getResponse();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getPublicKey()).isEqualTo(tokenAccount);
        assertThat(response.get(0).getAccountInfo().getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
    }

    @Test
    void shouldReturnErrorForUnrecognisedTokenMint() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountsByOwner(
                "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                Map.entry("mint", "FakeMintwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"));

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: could not find mint");
    }

    @Test
    void shouldReturnErrorForUnrecognisedTokenProgramId() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountsByOwner(
                "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                Map.entry("programId", "TokenFakeNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb"));

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: unrecognized Token program id");
    }

    @Test
    void shouldReturnErrorForMalformedAccountDelegate() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountsByOwner("InvalidKeyXYZ123!@#%^&*()NotAValidPublicKey", Map.entry("mint", "FakeMintwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"));

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = api.getTokenAccountsByOwner(
                "9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG",
                Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"),
                optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
