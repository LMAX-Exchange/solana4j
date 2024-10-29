package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.ErrorCode;
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
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

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
    public SolanaClientResponse<String> requestAirdrop(final String address, final long amountLamports)
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<String>>()
        {
        }, "requestAirdrop", address, amountLamports);

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess());
        }
    }

    @Override
    public SolanaClientResponse<TransactionResponse> getTransaction(final String transactionSignature)
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
                                                  {
                                                  },
                "getTransaction",
                transactionSignature,
                Map.of(
                        "econding", "jsonParsed",
                        "maxSupportedTransactionVersion", 0
                )
        );

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess());
        }
    }

    @Override
    public SolanaClientResponse<String> sendTransaction(final String transactionBlob)
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                                          {
                                          },
                "sendTransaction",
                transactionBlob,
                Map.of("preflightCommitment", Commitment.FINALIZED.toString().toLowerCase(Locale.UK))
        );

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess());
        }
    }

    @Override
    public SolanaClientResponse<Long> getBalance(final String address)
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<BalanceDTO>>()
                                          {
                                          },
                "getBalance",
                address);

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess().getValue());
        }
    }

    @Override
    public SolanaClientResponse<TokenAmount> getTokenAccountBalance(final String address)
    {
        final var result = queryForObject(
                new TypeReference<RpcWrapperDTO<TokenAmountDTO>>()
                {
                },
                "getTokenAccountBalance",
                address);

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess().getValue());
        }
    }

    @Override
    public SolanaClientResponse<AccountInfo> getAccountInfo(final String address)
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<AccountInfoDTO>>()
                                          {
                                          },
                "getAccountInfo",
                address,
                Map.of("encoding", "base64")
        );

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess().getValue());
        }
    }

    @Override
    public SolanaClientResponse<Long> getBlockHeight()
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
        {
        }, "getBlockHeight");

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess());
        }
    }

    @Override
    public SolanaClientResponse<Long> getSlot()
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
        {
        }, "getSlot");

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess());
        }
    }

    @Override
    public SolanaClientResponse<Blockhash> getLatestBlockhash()
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<BlockhashDTO>>()
        {
        }, "getLatestBlockhash");

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess().getValue());
        }
    }

    @Override
    public SolanaClientResponse<Long> getMinimumBalanceForRentExemption(final int size)
    {
        final var result = queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
        {
        }, "getMinimumBalanceForRentExemption", size);

        if (result.isError())
        {
            return new SolanaJsonRpcClientResponse<>(result.getError().getErrorCode(), result.getError().getErrorMessage());
        }
        else
        {
            return new SolanaJsonRpcClientResponse<>(result.getSuccess());
        }
    }

    private <T> Result<SolanaClientError, T> queryForObject(final TypeReference<RpcWrapperDTO<T>> type, final String method, final Object... params)
    {
        return performRequest(method, params, type);
    }

    private <T> Result<SolanaClientError, T> performRequest(
            final String method,
            final Object[] params,
            final TypeReference<RpcWrapperDTO<T>> type)
    {
        return prepareRequest(method, params)
                .onSuccess(success -> sendRequest(method, params, success))
                .onSuccess(success -> decodeResponse(method, params, type, success));
    }

    private Result<SolanaClientError, HttpRequest> prepareRequest(final String method, final Object[] params)
    {
        try
        {
            return Result.success(buildPostRequest(solanaCodec.encodeRequest(method, params)));
        }
        catch (final JsonProcessingException e)
        {
            return Result.error(new SolanaJsonRpcClientError(
                    ErrorCode.JSON_PROCESSING_ERROR,
                    String.format("Unable to encode JSON RPC request for method %s and params %s.", method, Arrays.toString(params)))
            );
        }
    }

    private Result<SolanaClientError, HttpResponse<String>> sendRequest(final String method, final Object[] params, final HttpRequest request)
    {
        try
        {
            final HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200)
            {
                return Result.error(new SolanaJsonRpcClientError(
                        ErrorCode.JSON_RPC_UNEXPECTED_STATUS_CODE,
                        String.format("Unexpected status code returned from the JSON RPC for method %s and params %s.", method, Arrays.toString(params)))
                );
            }
            else
            {
                return Result.success(httpResponse);
            }
        }
        catch (final IOException | InterruptedException e)
        {
            return Result.error(new SolanaJsonRpcClientError(
                    ErrorCode.JSON_RPC_COMMUNICATIONS_FAILURE,
                    String.format("Unable to communicate with the JSON RPC for method %s and params %s.", method, Arrays.toString(params)))
            );
        }
    }

    private <T> Result<SolanaClientError, T> decodeResponse(
            final String method,
            final Object[] params,
            final TypeReference<RpcWrapperDTO<T>> type,
            final HttpResponse<String> httpResponse)
    {
        try
        {
            final RpcWrapperDTO<T> rpcResult = solanaCodec.decodeResponse(httpResponse.body().getBytes(StandardCharsets.UTF_8), type);
            if (rpcResult.getError() != null)
            {
                return Result.error(new SolanaJsonRpcClientError(
                        ErrorCode.JSON_RPC_REPORTED_ERROR,
                        String.format("An error was reported by the JSON RPC with code %s and message %s.", rpcResult.getError().getCode(), rpcResult.getError().getMessage()))
                );
            }
            return Result.success(rpcResult.getResult());
        }
        catch (final IOException e)
        {
            return Result.error(new SolanaJsonRpcClientError(
                    ErrorCode.JSON_PROCESSING_ERROR,
                    String.format("Unable to decode JSON RPC response for method %s and params %s.", method, Arrays.toString(params)))
            );
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
