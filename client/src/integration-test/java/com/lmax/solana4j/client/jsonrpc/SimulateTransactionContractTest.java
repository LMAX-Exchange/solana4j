package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;

class SimulateTransactionContractTest extends SolanaClientIntegrationTestBase
{
    private String transactionBlobBase64;
    private String transactionBlobBase58;

    @BeforeEach
    void beforeEach() throws SolanaJsonRpcClientException
    {
        final String latestBlockhash = api.getLatestBlockhash().getResponse().getBlockhashBase58();

        final byte[] transactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(payerAccount),
                Solana.blockhash(latestBlockhash),
                Solana.account(tokenMint),
                Solana.account(tokenMintAuthority),
                Solana.destination(Solana.account(tokenAccount1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(payerAccount), SolanaEncoding.decodeBase58(payerAccountPriv)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(tokenMintAuthority), SolanaEncoding.decodeBase58(tokenMintAuthorityPriv))
                )
        );

        transactionBlobBase64 = Base64.getEncoder().encodeToString(transactionBytes);
        transactionBlobBase58 = SolanaEncoding.encodeBase58(transactionBytes);
    }

    @Test
    @Disabled
    void shouldSimulateTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSimulateTransactionSigVerifyOptionalParam() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSimulateTransactionReplaceRecentBlockhashOptionalParam() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSimulateTransactionEncodingBase58OptionalParam() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSimulateTransactionInnerInstructionsOptionalParam() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    @Disabled
    void shouldSimulateTransactionAccountsConfigurationOptionalParam() throws SolanaJsonRpcClientException
    {
        fail();
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);
        optionalParams.addParam("encoding", "base64");

        final var response = api.sendTransaction(transactionBlobBase64, optionalParams);

        Assertions.assertThat(response.isSuccess()).isFalse();
        Assertions.assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        Assertions.assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
