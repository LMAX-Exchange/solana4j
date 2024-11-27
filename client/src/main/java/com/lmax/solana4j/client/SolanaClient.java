package com.lmax.solana4j.client;

import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClient;

import java.time.Duration;

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
     * <p>
     * This overloaded version of the {@code create} method uses default timeout values:
     * 30 seconds for both connection and request timeouts.
     * </p>
     * @param rpcUrl The endpoint URL of the Solana node.
     * @return An instance of {@link SolanaApi} that communicates with the specified Solana node,
     *         using default timeouts.
     */
    public static SolanaApi create(final String rpcUrl)
    {
        return create(rpcUrl, Duration.ofSeconds(30), Duration.ofSeconds(30));
    }

    /**
     * Creates a new instance of {@link SolanaApi} that connects to the provided Solana node.
     * This method returns an API client that facilitates interaction with the Solana blockchain.
     *
     * @param rpcUrl           The endpoint URL of the Solana node.
     * @param connectionTimeout The maximum time to wait for a connection to be established.
     * @param requestTimeout    The maximum time to wait for a response from the Solana node.
     * @return An instance of {@link SolanaApi} that communicates with the specified Solana node.
     */
    public static SolanaApi create(final String rpcUrl, final Duration connectionTimeout, final Duration requestTimeout)
    {
        return new SolanaJsonRpcClient(rpcUrl, connectionTimeout, requestTimeout);
    }
}