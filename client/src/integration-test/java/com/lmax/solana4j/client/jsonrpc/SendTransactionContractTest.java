package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SendTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    @Disabled
    void shouldSendTransaction() throws SolanaJsonRpcClientException
    {
        // need to create a valid transaction ...
        // lots of different optional params to set too ...
        final var transactionSignature = api.sendTransaction("blobWellFormed...Not");
    }

    @Test
    void shouldReturnErrorForMalformedTransaction() throws SolanaJsonRpcClientException
    {
        final var response = api.sendTransaction("thisisnotavalidtransactionblob");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("invalid base64 encoding: InvalidPadding");
    }
}
