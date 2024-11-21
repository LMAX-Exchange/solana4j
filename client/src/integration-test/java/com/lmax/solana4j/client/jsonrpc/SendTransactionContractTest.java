package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

final class SendTransactionContractTest extends SolanaClientIntegrationTestBase
{
    private String failPreflightCheckTransactionBlobBase64;
    private String successfulTransactionBlobBase58;
    private String successfulTransactionBlobBase64;

    @BeforeEach
    void beforeEach() throws SolanaJsonRpcClientException
    {
        final String latestBlockhash = SOLANA_API.getLatestBlockhash().getResponse().getBlockhashBase58();

        final byte[] successfulTransactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(PAYER),
                Solana.blockhash(SOLANA_API.getLatestBlockhash().getResponse().getBlockhashBase58()),
                Solana.account(TOKEN_MINT),
                Solana.account(TOKEN_MINT_AUTHORITY),
                Solana.destination(Solana.account(TOKEN_ACCOUNT_2), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(PAYER), SolanaEncoding.decodeBase58(PAYER_PRIV)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(TOKEN_MINT_AUTHORITY), SolanaEncoding.decodeBase58(TOKEN_MINT_AUTHORITY_PRIV))
                )
        );

        successfulTransactionBlobBase58 = SolanaEncoding.encodeBase58(successfulTransactionBytes);
        successfulTransactionBlobBase64 = Base64.getEncoder().encodeToString(successfulTransactionBytes);

        final byte[] failPreflightCheckTransactionBytes = Solana4jJsonRpcTestHelper.createTransferTokenTransactionBlob(
                Solana.account(PAYER),
                Solana.blockhash(latestBlockhash),
                Solana.destination(Solana.account(TOKEN_ACCOUNT_1), 100000),
                Solana.account(TOKEN_ACCOUNT_2),
                Solana.account(TOKEN_ACCOUNT_OWNER),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(PAYER), SolanaEncoding.decodeBase58(PAYER_PRIV)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(TOKEN_ACCOUNT_OWNER), SolanaEncoding.decodeBase58(TOKEN_ACCOUNT_OWNER_PRIV))
                )
        );

        failPreflightCheckTransactionBlobBase64 = Base64.getEncoder().encodeToString(failPreflightCheckTransactionBytes);
    }

    @Test
    void shouldSendTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final SolanaClientResponse<String> response = SOLANA_API.sendTransaction(successfulTransactionBlobBase64);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldSendTransactionEncodingBase58OptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final SolanaClientResponse<String> response = SOLANA_API.sendTransaction(successfulTransactionBlobBase58, optionalParams);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void shouldSendTransactionSkipPreflightFalseOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("skipPreflight", false);
        optionalParams.addParam("encoding", "base64");

        // transaction will fail so if we don't skip preflight then we get immediate feedback on that
        final SolanaClientResponse<String> response = SOLANA_API.sendTransaction(failPreflightCheckTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isFalse();
    }


    @Test
    void shouldSendTransactionSkipPreflightTrueOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("skipPreflight", true);
        optionalParams.addParam("encoding", "base64");

        // transaction will fail so if we skip preflight then we have to wait for the transaction signature to never return a response
        final SolanaClientResponse<String> response = SOLANA_API.sendTransaction(failPreflightCheckTransactionBlobBase64, optionalParams);

        assertThat(response.isSuccess()).isTrue();

        waitForTransactionFailure(response.getResponse(), Optional.of(optionalParams));
    }

    @Test
    @Disabled
    void shouldSendTransactionSetPreflightCommitmentProcessed() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSendTransactionSetPreflightCommitmentConfirmed() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSendTransactionSetPreflightCommitmentFinalized() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSendTransactionMaxRetriesOptionalParam()
    {
        fail();
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);
        optionalParams.addParam("encoding", "base64");

        final var response = SOLANA_API.sendTransaction(successfulTransactionBlobBase64, optionalParams);

        Assertions.assertThat(response.isSuccess()).isFalse();
        Assertions.assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        Assertions.assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
