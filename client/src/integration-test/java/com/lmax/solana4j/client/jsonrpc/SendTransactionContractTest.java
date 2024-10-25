package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.fail;

// https://solana.com/docs/rpc/http/sendtransaction
class SendTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    @Disabled
    void shouldSendTransaction()
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