package com.lmax.solana4j.client;

import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClient;

/**
 * The {@code SolanaClient} class provides a static factory method to interact with the Solana blockchain.
 * This class simplifies the creation of a {@link SolanaApi} instance,
 * which allows interaction with Solana nodes.
 */
public final class SolanaClient
{
    private SolanaClient()
    {
    }

    /**
     * Creates a new instance of {@link SolanaApi} that connects to the provided Solana node.
     * This method returns an API client that facilitates interaction with the Solana blockchain.
     *
     * @param rpcUrl The endpoint URL of the Solana node.
     * @return An instance of {@link SolanaApi} that communicates with the specified Solana node.
     */
    public static SolanaApi jsonRpc(final String rpcUrl)
    {
        return new SolanaJsonRpcClient(rpcUrl);
    }
}