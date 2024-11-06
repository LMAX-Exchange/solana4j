package com.lmax.solana4j.client.jsonrpc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Disabled
class GetTokenAccountsByOwnerContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetTokenAccountsByOwnerWithMintDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        // 9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG - is the owner of the token account which we can query of just promise to the test reader
        final var response = api.getTokenAccountsByOwner("9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG", Map.entry("mint", "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg"));
    }

    @Test
    void shouldGetTokenAccountsByOwnerWithProgramIdDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var response = api.getTokenAccountsByOwner("9Hf5hHKtt8ghuZRN3WfivSjVCS2fveRrkr6NHtFS6dhG", Map.entry("programId", "TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb"));
    }

    @Test
    void shouldReturnErrorForUnrecognisedTokenMint() throws SolanaJsonRpcClientException
    {

    }

    @Test
    void shouldReturnErrorForUnrecognisedTokenProgramId() throws SolanaJsonRpcClientException
    {

    }

    @Test
    void shouldReturnErrorForMalformedAccountDelegate() throws SolanaJsonRpcClientException
    {

    }
}
