package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.client.api.SimulateTransactionResponse;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// https://solana.com/docs/rpc/http/simulatetransaction
@Disabled
final class SimulateTransactionContractTest extends SolanaClientIntegrationTestBase
{
    private String transactionBlobBase58;
    private String transactionBlobBase64;
    private String transactionBlobBase64BadSignatures;
    private String transactionBlobBase64ReplaceBlockhash;

    @BeforeEach
    void beforeEach() throws SolanaJsonRpcClientException
    {
        final String latestBlockhash = SOLANA_API.getLatestBlockhash().getResponse().getBlockhashBase58();

        final byte[] successfulTransactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
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

        transactionBlobBase58 = SolanaEncoding.encodeBase58(successfulTransactionBytes);
        transactionBlobBase64 = Base64.getEncoder().encodeToString(successfulTransactionBytes);

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
        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64).getResponse();

        Assertions.assertThat(response.getUnitsConsumed()).isEqualTo(958);
        Assertions.assertThat(response.getInnerInstructions()).isNull();
        Assertions.assertThat(response.getReplacementBlockhash()).isNull();
        Assertions.assertThat(response.getAccounts()).isNull();
        Assertions.assertThat(response.getReturnData()).isNull();
        Assertions.assertThat(response.getLogs()).hasSize(4);
        Assertions.assertThat(response.getErr()).isNull();
    }

    @Test
    void shouldSimulateTransactionSigVerifyOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("sigVerify", true);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64BadSignatures);

        Assertions.assertThat(response.isSuccess()).isTrue();
        Assertions.assertThat(response.getResponse().getErr()).isNotNull();
    }

    @Test
    void shouldSimulateTransactionReplaceRecentBlockhashOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("replaceRecentBlockhash", true);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64ReplaceBlockhash, optionalParams);

        Assertions.assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldSimulateTransactionEncodingBase58OptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase58, optionalParams);

        Assertions.assertThat(response.getResponse().getUnitsConsumed()).isEqualTo(958);
        Assertions.assertThat(response.getResponse().getLogs()).hasSize(4);
        Assertions.assertThat(response.getResponse().getErr()).isNull();
    }

    @Test
    void shouldSimulateTransactionInnerInstructionsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("innerInstructions", true);

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64, optionalParams);

        Assertions.assertThat(response.getResponse().getInnerInstructions()).hasSize(0);
    }

    @Test
    void shouldSimulateTransactionAccountsConfigurationOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("accounts", Map.of("encoding", "base64", "address", List.of(TOKEN_ACCOUNT_1)));

        final var response = SOLANA_API.simulateTransaction(transactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = SOLANA_API.sendTransaction(transactionBlobBase64, optionalParams);

        Assertions.assertThat(response.isSuccess()).isFalse();
        Assertions.assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        Assertions.assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}

