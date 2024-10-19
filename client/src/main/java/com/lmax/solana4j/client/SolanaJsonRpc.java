package com.lmax.solana4j.client;

import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.jsonrpc.SolanaClient;

/**
 * The {@code SolanaJsonRpc} class provides a static factory method to interact with the Solana blockchain using JSON-RPC.
 * This class simplifies the creation of a {@link com.lmax.solana4j.client.api.SolanaApi} instance, which allows interaction
 * with Solana nodes via the JSON-RPC protocol.
 */
public final class SolanaJsonRpc
{

    private SolanaJsonRpc()
    {
    }

    /**
     * Creates a new instance of {@link com.lmax.solana4j.client.api.SolanaApi} that connects to the provided Solana JSON-RPC URL.
     * This method returns an API client that facilitates interaction with the Solana blockchain through JSON-RPC.
     *
     * @param rpcUrl The RPC endpoint URL of the Solana blockchain.
     * @return An instance of {@link com.lmax.solana4j.client.api.SolanaApi} that communicates with the specified Solana RPC endpoint.
     */
    public static SolanaApi api(final String rpcUrl)
    {
        return new SolanaClient(rpcUrl);
    }
}