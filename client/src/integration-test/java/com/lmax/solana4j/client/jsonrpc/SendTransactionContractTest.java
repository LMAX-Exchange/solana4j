package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.fail;

class SendTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    @Disabled
    void shouldSendTransaction() throws SolanaJsonRpcClientException
    {
        final var transactionSignature = api.sendTransaction("blobWellFormed...Not");
        fail();
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedTransaction()
    {
        fail();
    }
}
