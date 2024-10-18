package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

final class SolanaRpcClient
{
    static final String JSONRPC = "jsonrpc";
    static final String ID = "id";
    static final String METHOD = "method";
    static final String PARAMS = "params";

    private final String url;
    private final HttpClient httpClient;
    private final SolanaCodec solanaCodec = new SolanaCodec();

    SolanaRpcClient(final String url)
    {
        this.url = url;
        this.httpClient = HttpClient.newHttpClient();
    }

    <T> T queryForObject(final TypeReference<RpcWrapperDTO<T>> type, final String method, final Object... params)
    {
        try
        {
            final RpcWrapperDTO<T> wrapperDTO = performRequest(method, params, type);
            final T result = wrapperDTO.getResult();
            if (wrapperDTO.getError() != null)
            {
                throw new RuntimeException("Something went wrong: " + wrapperDTO.getError().getMessage());
            }
            return result;
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Something exceptional happened!", e);
        }
    }

    private <T> RpcWrapperDTO<T> performRequest(
            final String method,
            final Object[] params,
            final TypeReference<RpcWrapperDTO<T>> type) throws IOException
    {
        final HttpRequest request = buildPostRequest(solanaCodec.encodeRequest(method, params));
        try
        {
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
            {
                throw new RuntimeException("Unexpected Status Code!! Probably do something a little cleverer.");
            }
            return solanaCodec.decodeResponse(response.body().getBytes(StandardCharsets.UTF_8), type);

        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest buildPostRequest(final String payload)
    {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(URI.create(url));
        request.setHeader("Content-Type", "application/json");
        request.POST(HttpRequest.BodyPublishers.ofString(payload));

        return request.build();
    }

}
