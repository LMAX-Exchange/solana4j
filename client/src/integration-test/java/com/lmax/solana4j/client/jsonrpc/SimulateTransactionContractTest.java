package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// https://solana.com/docs/rpc/http/simulatetransaction
final class SimulateTransactionContractTest extends SolanaClientIntegrationTestBase
{
    private String mintToTransactionBlobBase58;
    private String mintToTransactionBlobBase64;
    private String transactionBlobBase64BadSignatures;
    private String transactionBlobBase64ReplaceBlockhash;

    @BeforeEach
    void beforeEach() throws SolanaJsonRpcClientException
    {
        final String latestBlockhash = SOLANA_API.getLatestBlockhash().getResponse().getBlockhashBase58();

        final byte[] successfulMintToTransactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(PAYER),
                Solana.blockhash(latestBlockhash),
                Solana.account(TOKEN_MINT),
                Solana.account(TOKEN_MINT_AUTHORITY),
                Solana.destination(Solana.account(TOKEN_ACCOUNT_1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(PAYER), SolanaEncoding.decodeBase58(PAYER_PRIV)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(TOKEN_MINT_AUTHORITY), SolanaEncoding.decodeBase58(TOKEN_MINT_AUTHORITY_PRIV))
                )
        );

        mintToTransactionBlobBase58 = SolanaEncoding.encodeBase58(successfulMintToTransactionBytes);
        mintToTransactionBlobBase64 = Base64.getEncoder().encodeToString(successfulMintToTransactionBytes);

        final byte[] badSignaturesTransactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(PAYER),
                Solana.blockhash(latestBlockhash),
                Solana.account(TOKEN_MINT),
                Solana.account(TOKEN_MINT_AUTHORITY),
                Solana.destination(Solana.account(TOKEN_ACCOUNT_1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(PAYER), SolanaEncoding.decodeBase58(PAYER)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(TOKEN_MINT_AUTHORITY), SolanaEncoding.decodeBase58(TOKEN_MINT_AUTHORITY))
                )
        );

        transactionBlobBase64BadSignatures = Base64.getEncoder().encodeToString(badSignaturesTransactionBytes);

        final byte[] replaceBlockhashTransactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(PAYER),
                Solana.blockhash("DVPPT9tfXkTVjA371ND18Vs2U27CMah8hCMbG2yofzqH"),
                Solana.account(TOKEN_MINT),
                Solana.account(TOKEN_MINT_AUTHORITY),
                Solana.destination(Solana.account(TOKEN_ACCOUNT_1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(PAYER), SolanaEncoding.decodeBase58(PAYER_PRIV)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(TOKEN_MINT_AUTHORITY), SolanaEncoding.decodeBase58(TOKEN_MINT_AUTHORITY_PRIV))
                )
        );

        transactionBlobBase64ReplaceBlockhash = Base64.getEncoder().encodeToString(replaceBlockhashTransactionBytes);
    }

    @Test
    void shouldSimulateTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase64).getResponse();

        assertThat(response.getUnitsConsumed()).isEqualTo(958);
        assertThat(response.getLogs().size()).isEqualTo(4);
        // inner instructions are program invocations that occur inside the execution of a main (outer)
        // transaction instruction
        assertThat(response.getInnerInstructions()).isNull();
        assertThat(response.getReplacementBlockhash()).isNull();
        assertThat(response.getAccounts()).isNull();
        // requires interacting with a smart contract that calls BPFs system call sol_set_return_data()
        assertThat(response.getReturnData()).isNull();
        assertThat(response.getErr()).isNull();
    }

    @Test
    void shouldSimulateTransactionSigVerifyTrueOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("sigVerify", true);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64BadSignatures, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        // error because the signatures are invalid
        assertThat(response.getError().getErrorCode()).isEqualTo(-32003L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Transaction signature verification failure");
    }

    @Test
    void shouldSimulateTransactionSigVerifyFalseOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("sigVerify", false);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64BadSignatures, optionalParams);

        // sigVerify false so not looking at signatures
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldSimulateTransactionReplaceRecentBlockhashFalseOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("replaceRecentBlockhash", false);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64ReplaceBlockhash, optionalParams);

        assertThat(response.isSuccess()).isTrue();
        // error because the invalid blockhash not replaced
        assertThat(response.getResponse().getErr()).isNotNull();
    }


    @Test
    void shouldSimulateTransactionReplaceRecentBlockhashTrueOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("replaceRecentBlockhash", true);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64ReplaceBlockhash, optionalParams);

        assertThat(response.isSuccess()).isTrue();
        // no error because the invalid blockhash replaced
        assertThat(response.getResponse().getReplacementBlockhash()).isNotNull();
        assertThat(response.getResponse().getErr()).isNull();
    }

    @Test
    void shouldSimulateTransactionEncodingBase58OptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase58, optionalParams);

        assertThat(response.getResponse().getUnitsConsumed()).isEqualTo(958);
        assertThat(response.getResponse().getLogs().size()).isEqualTo(4);
        assertThat(response.getResponse().getErr()).isNull();
    }

    @Test
    void shouldSimulateTransactionInnerInstructionsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("innerInstructions", true);

        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase64, optionalParams);

        // inner instructions are program invocations that occur inside the execution of a main (outer)
        // transaction instruction
        assertThat(response.getResponse().getInnerInstructions().size()).isEqualTo(0);
        // requires interacting with a smart contract that calls BPFs system call sol_set_return_data()
        assertThat(response.getResponse().getReturnData()).isNull();
    }

    @Test
    void shouldSimulateTransactionAccountsConfigurationBase64OptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("accounts", Map.of("encoding", "base64", "addresses", List.of(TOKEN_ACCOUNT_1)));

        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isTrue();

        assertThat(response.getResponse().getAccounts().size()).isEqualTo(1);

        final var accountInfo = response.getResponse().getAccounts().get(0);
        assertThat(accountInfo.getSpace()).isEqualTo(165L);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getOwner()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
        assertThat(accountInfo.getLamports()).isEqualTo(400000L);

        final var data = accountInfo.getData();
        assertThat(data.getAccountInfoParsed()).isNull();
        assertThat(data.getAccountInfoEncoded().get(0)).isEqualTo(
                "HCEswh8utCXQBqnsIw8mz0OFQo9N7bLx3WTVx5o6l4tdQDspqrYRFt5A4y1L6LHNdNnOQ7IdPHtkqYF/Z+a01" +
                        "RQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        assertThat(data.getAccountInfoEncoded().get(1)).isEqualTo("base64");
    }

    @Test
    void shouldSimulateTransactionAccountsConfigurationBase58OptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("accounts", Map.of("encoding", "base58", "addresses", List.of(TOKEN_ACCOUNT_1)));

        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("base58 encoding not supported");
    }

    @Test
    void shouldSimulateTransactionAccountsConfigurationBase64ZstdOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("accounts", Map.of("encoding", "base64+zstd", "addresses", List.of(TOKEN_ACCOUNT_1)));

        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isTrue();

        assertThat(response.getResponse().getAccounts().size()).isEqualTo(1);

        final var data = response.getResponse().getAccounts().get(0).getData();
        assertThat(data.getAccountInfoParsed()).isNull();
        assertThat(data.getAccountInfoEncoded().get(0)).isEqualTo(
                "KLUv/QBYdQIARAQcISzCHy60JdAGqewjDybPQ4VCj03tsvHdZNXHmjqXi11A" +
                        "OymqthEW3kDjLUvosc102c5Dsh08e2SpgX9n5rTVFAABAAIABCcKIXAG");
        assertThat(data.getAccountInfoEncoded().get(1)).isEqualTo("base64+zstd");
    }

    @Test
    void shouldSimulateTransactionAccountsConfigurationJsonParsedOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("accounts", Map.of("encoding", "jsonParsed", "addresses", List.of(TOKEN_ACCOUNT_1)));

        final var response = SOLANA_API.simulateTransaction(mintToTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isTrue();

        assertThat(response.getResponse().getAccounts().size()).isEqualTo(1);

        final var data = response.getResponse().getAccounts().get(0).getData();
        assertThat(data.getAccountInfoEncoded()).isNull();

        final var parsedAccountInfo = data.getAccountInfoParsed();
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
                                "amount", "20",
                                "decimals", 18,
                                "uiAmount", 2.0E-17,
                                "uiAmountString", "0.00000000000000002"
                        ))
        );    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = SOLANA_API.sendTransaction(mintToTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}

