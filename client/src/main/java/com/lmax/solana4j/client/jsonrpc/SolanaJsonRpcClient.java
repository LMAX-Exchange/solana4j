package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientError;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import com.lmax.solana4j.client.api.TokenAmount;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class SolanaJsonRpcClient implements SolanaApi
{
    private final String rpcUrl;
    private final HttpClient httpClient;
    private final SolanaCodec solanaCodec;
    private final Duration requestTimeout;

    public SolanaJsonRpcClient(final String rpcUrl, final Duration connectionTimeout, final Duration requestTimeout)
    {
        this.rpcUrl = rpcUrl;
        this.httpClient = HttpClient
                .newBuilder()
                .connectTimeout(connectionTimeout)
                .build();
        this.requestTimeout = requestTimeout;
        this.solanaCodec = new SolanaCodec(false);
    }

    SolanaJsonRpcClient(final String rpcUrl, final boolean failOnUnknownProperties)
    {
        this.rpcUrl = rpcUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.requestTimeout = Duration.ofSeconds(30);
        this.solanaCodec = new SolanaCodec(failOnUnknownProperties);
    }

    @Override
    public SolanaClientResponse<String> requestAirdrop(final String address, final long amountLamports) throws SolanaJsonRpcClientException
    {
        return queryForObject(
                new TypeReference<RpcWrapperDTO<String>>()
                {
                },
                "requestAirdrop",
                dto -> dto,
                address,
                amountLamports);
    }

    @Override
    public SolanaClientResponse<TransactionResponse> getTransaction(final String transactionSignature) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
                              {
                              },
                "getTransaction",
                dto -> dto,
                transactionSignature,
                Map.of(
                        "econding", "jsonParsed",
                        "maxSupportedTransactionVersion", 0
                )
        );
    }

    @Override
    public SolanaClientResponse<String> sendTransaction(final String transactionBlob) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                              {
                              },
                "sendTransaction",
                dto -> dto,
                transactionBlob,
                Map.of("preflightCommitment", Commitment.FINALIZED.toString().toLowerCase(Locale.UK))
        );
    }

    @Override
    public SolanaClientResponse<Long> getBalance(final String address) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                "getBalance",
                BalanceDTO::getValue,
                address);
    }

    @Override
    public SolanaClientResponse<TokenAmount> getTokenAccountBalance(final String address) throws SolanaJsonRpcClientException
    {
        return queryForObject(
                new TypeReference<>()
                {
                },
                "getTokenAccountBalance",
                TokenAmountDTO::getValue,
                address);
    }

    @Override
    public SolanaClientResponse<AccountInfo> getAccountInfo(final String address) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<AccountInfoDTO>>()
                              {
                              },
                "getAccountInfo",
                AccountInfoDTO::getValue,
                address,
                Map.of("encoding", "base64")
        );
    }

    @Override
    public SolanaClientResponse<Long> getBlockHeight() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                "getBlockHeight",
                dto -> dto);

    }

    @Override
    public SolanaClientResponse<Long> getSlot() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                "getSlot",
                dto -> dto);
    }

    @Override
    public SolanaClientResponse<Blockhash> getLatestBlockhash() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<BlockhashDTO>>()
                              {
                              },
                "getLatestBlockhash",
                BlockhashDTO::getValue);
    }

    @Override
    public SolanaClientResponse<Long> getMinimumBalanceForRentExemption(final int size) throws SolanaJsonRpcClientException
    {
        return queryForObject(
                new TypeReference<RpcWrapperDTO<Long>>()
                {
                },
                "getMinimumBalanceForRentExemption",
                dto -> dto,
                size);
    }

//    @Override
//    public SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(final String transaction) throws SolanaJsonRpcClientException
//    {
//        return queryForObject(
//                new TypeReference<RpcWrapperDTO<SimulateTransactionResponseDTO>>()
//                {
//                },
//                "simulateTransaction",
//                SimulateTransactionResponseDTO::getValue,
//                transaction,
//                Map.of("encoding", "base64", "replaceRecentBlockhash", true));
//    }

    private <S, T> SolanaClientResponse<S> queryForObject(
            final TypeReference<RpcWrapperDTO<T>> type,
            final String method,
            final Function<T, S> dtoMapper,
            final Object... params) throws SolanaJsonRpcClientException
    {
        final HttpRequest request = prepareRequest(method, params);
        final HttpResponse<String> httpResponse = sendRequest(request);

        final Result<SolanaClientError, T> response = decodeResponse(type, httpResponse);
        if (response.isError())
        {
            return SolanaJsonRpcClientResponse.creatErrorResponse(response.getError());
        }

        return SolanaJsonRpcClientResponse.createSuccessResponse(dtoMapper.apply(response.getSuccess()));
    }

    private HttpRequest prepareRequest(final String method, final Object[] params) throws SolanaJsonRpcClientException
    {
        try
        {
            return buildPostRequest(solanaCodec.encodeRequest(method, params));
        }
        catch (final JsonProcessingException e)
        {
            throw new SolanaJsonRpcClientException(String.format("An error occurred building the JSON RPC request for method %s.", method), e);
        }
    }

    private HttpResponse<String> sendRequest(final HttpRequest request) throws SolanaJsonRpcClientException
    {
        try
        {
            final HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200)
            {
                throw new SolanaJsonRpcClientException(String.format("Unexpected status code %s returned from the JSON RPC for request %s.", httpResponse.statusCode(), request));
            }
            else
            {
                return httpResponse;
            }
        }
        catch (final IOException | InterruptedException e)
        {
            throw new SolanaJsonRpcClientException(String.format("Unable to communicate with the JSON RPC for request %s.", request), e, true);
        }
    }

    private <T> Result<SolanaClientError, T> decodeResponse(
            final TypeReference<RpcWrapperDTO<T>> type,
            final HttpResponse<String> httpResponse) throws SolanaJsonRpcClientException
    {
        try
        {
            final RpcWrapperDTO<T> rpcResult = solanaCodec.decodeResponse(httpResponse.body().getBytes(StandardCharsets.UTF_8), type);
            if (rpcResult.getError() != null)
            {
                return Result.error(new SolanaJsonRpcClientError(rpcResult.getError().getCode(), rpcResult.getError().getMessage()));
            }
            return Result.success(rpcResult.getResult());
        }
        catch (final IOException e)
        {
            throw new SolanaJsonRpcClientException(String.format("Unable to decode JSON RPC response %s.", httpResponse), e);
        }
    }

    private HttpRequest buildPostRequest(final String payload)
    {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.timeout(requestTimeout);
        request.uri(URI.create(rpcUrl));
        request.setHeader("Content-Type", "application/json");
        request.POST(HttpRequest.BodyPublishers.ofString(payload));

        return request.build();
    }
}
