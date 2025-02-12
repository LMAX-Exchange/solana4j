package com.lmax.solana4j.client;

import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClient;

import java.net.http.HttpClient;

/**
 * The {@code SolanaClient} class provides a static factory method to create a {@link SolanaApi} object
 * to communicate with a solana node.
 */
public final class SolanaClient
{
    private SolanaClient()
    {
    }

    /**
     * Creates a new {@code SolanaApi} instance using the specified http client and rpc url.
     *
     * @param httpClient  the {@link HttpClient} instance to use for sending requests.
     *                    Providing this allows customization of http-related configurations such as
     *                    connection pooling, ssl context, and timeouts.
     * @param rpcUrl      the url of the solana json rpc endpoint.
     * @return            a new {@link SolanaApi} instance configured with the provided http client
     *                    and rpc url.
     */
    public static SolanaApi create(final HttpClient httpClient, final String rpcUrl)
    {
        return new SolanaJsonRpcClient(httpClient, rpcUrl);
    }
}