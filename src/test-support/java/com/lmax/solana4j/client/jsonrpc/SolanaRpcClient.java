package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SolanaRpcClient
{
    protected static final String JSONRPC = "jsonrpc";
    protected static final String ID = "id";
    protected static final String METHOD = "method";
    protected static final String PARAMS = "params";

    private final String url;
    private final CloseableHttpClient httpClient;
    private final SolanaCodec solanaCodec = new SolanaCodec();

    public SolanaRpcClient(final String url)
    {
        this.url = url;
        this.httpClient = HttpClientBuilder.create().build();
    }

    public <T> T queryForObject(final TypeReference<RpcWrapperDTO<T>> type, final String method, final Object... params)
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
        final HttpPost request = buildPostRequest(solanaCodec.encodeRequest(method, params));
        try (CloseableHttpResponse httpResponse = httpClient.execute(request))
        {
            if (httpResponse.getStatusLine().getStatusCode() != 200)
            {
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
            }
            return solanaCodec.decodeResponse(httpResponse.getEntity().getContent(), type);
        }
    }

    private HttpPost buildPostRequest(final String payload)
    {
        final HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(payload, UTF_8));
        post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        post.setConfig(RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(6000).build());

        return post;
    }

}
