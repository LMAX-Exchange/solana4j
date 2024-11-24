package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.SignatureForAddress;
import com.lmax.solana4j.client.api.SignatureStatus;
import com.lmax.solana4j.client.api.SimulateTransactionResponse;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import com.lmax.solana4j.client.api.TokenAccount;
import com.lmax.solana4j.client.api.TokenAmount;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClientOptionalParams.defaultOptionalParams;

/**
 * Implementation of the {@link SolanaApi} interface for interacting with the Solana blockchain via JSON-RPC.
 * This client provides methods to perform various operations such as requesting airdrops, sending transactions,
 * retrieving account information, and more.
 */
public class SolanaJsonRpcClient implements SolanaApi
{
    private final String rpcUrl;
    private final HttpClient httpClient;
    private final SolanaCodec solanaCodec;
    private final Duration requestTimeout;

    /**
     * Constructs a new {@code SolanaJsonRpcClient} with the specified RPC URL, connection timeout, and request timeout.
     *
     * @param rpcUrl            the URL of the Solana JSON-RPC endpoint
     * @param connectionTimeout the timeout duration for establishing connections
     * @param requestTimeout    the timeout duration for completing requests
     */
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

    /**
     * Constructs a new {@code SolanaJsonRpcClient} with the specified RPC URL and a flag indicating whether to fail
     * on unknown properties during JSON deserialization.
     *
     * @param rpcUrl                   the URL of the Solana JSON-RPC endpoint
     * @param failOnUnknownProperties  if {@code true}, unknown properties in JSON responses will cause deserialization to fail
     */
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
        return queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                              {
                              },
                dto -> dto, "requestAirdrop", address, amountLamports,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<String> requestAirdrop(final String address, final long amountLamports, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                              {
                              },
                dto -> dto, "requestAirdrop", address, amountLamports,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<String> sendTransaction(final String transactionBlob) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                              {
                              },
                dto -> dto,
                "sendTransaction",
                transactionBlob,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<String> sendTransaction(final String transactionBlob, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                              {
                              },
                dto -> dto,
                "sendTransaction",
                transactionBlob,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<TransactionResponse> getTransaction(final String transactionSignature) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
                              {
                              },
                dto -> dto, "getTransaction", transactionSignature,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<TransactionResponse> getTransaction(final String transactionSignature, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
                              {
                              },
                dto -> dto, "getTransaction", transactionSignature,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<Long> getBalance(final String address) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                BalanceDTO::getValue, "getBalance", address,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<Long> getBalance(final String address, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                BalanceDTO::getValue, "getBalance", address,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<TokenAmount> getTokenAccountBalance(final String address) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                TokenAmountDTO::getValue, "getTokenAccountBalance", address,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<TokenAmount> getTokenAccountBalance(final String address, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                TokenAmountDTO::getValue, "getTokenAccountBalance", address,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<AccountInfo> getAccountInfo(final String address) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                AccountInfoDTO::getValue, "getAccountInfo", address,
                defaultOptionalParams()
        );
    }

    @Override
    public SolanaClientResponse<AccountInfo> getAccountInfo(final String address, final SolanaClientOptionalParams solanaClientOptionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                AccountInfoDTO::getValue, "getAccountInfo", address,
                solanaClientOptionalParams.getParams()
        );
    }

    @Override
    public SolanaClientResponse<Long> getBlockHeight() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "getBlockHeight",
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<Long> getBlockHeight(final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "getBlockHeight",
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<Long> getSlot() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "getSlot",
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<Long> getSlot(final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "getSlot",
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<Blockhash> getLatestBlockhash() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                BlockhashDTO::getValue, "getLatestBlockhash",
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<Blockhash> getLatestBlockhash(final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                BlockhashDTO::getValue, "getLatestBlockhash",
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<Long> getMinimumBalanceForRentExemption(final int size) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "getMinimumBalanceForRentExemption", size,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<Long> getMinimumBalanceForRentExemption(final int size, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "getMinimumBalanceForRentExemption", size,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<Long> minimumLedgerSlot() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<Long>>()
                              {
                              },
                dto -> dto, "minimumLedgerSlot");
    }

    @Override
    public SolanaClientResponse<String> getHealth() throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                              {
                              },
                dto -> dto, "getHealth");
    }

    @Override
    public SolanaClientResponse<List<SignatureForAddress>> getSignaturesForAddress(final String addressBase58) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<List<SignatureForAddressDTO>>>()
                              {
                              },
                ArrayList::new, "getSignaturesForAddress", addressBase58);
    }

    @Override
    public SolanaClientResponse<List<SignatureForAddress>> getSignaturesForAddress(
            final String addressBase58,
            final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<List<SignatureForAddressDTO>>>()
                              {
                              },
                ArrayList::new, "getSignaturesForAddress", addressBase58,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<List<SignatureStatus>> getSignatureStatuses(final List<String> transactionSignatures) throws SolanaJsonRpcClientException
    {
        final var defaultOptionalParams = defaultOptionalParams();
        // it's not such an optional field, apparently
        defaultOptionalParams.put("searchTransactionHistory", false);

        return queryForObject(new TypeReference<>()
                              {
                              },
                SignatureStatusesDTO::getValue, "getSignatureStatuses", transactionSignatures,
                defaultOptionalParams);
    }

    @Override
    public SolanaClientResponse<List<SignatureStatus>> getSignatureStatuses(
            final List<String> transactionSignatures,
            final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                SignatureStatusesDTO::getValue, "getSignatureStatuses", transactionSignatures,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<List<TokenAccount>> getTokenAccountsByOwner(final String accountDelegate, final Map.Entry<String, String> filter) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                TokenAccountsByOwnerDTO::getValue, "getTokenAccountsByOwner", accountDelegate, filter,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<List<TokenAccount>> getTokenAccountsByOwner(
            final String accountDelegate,
            final Map.Entry<String, String> filter,
            final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                TokenAccountsByOwnerDTO::getValue, "getTokenAccountsByOwner", accountDelegate, filter,
                optionalParams.getParams());
    }

    @Override
    public SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(final String transaction) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                SimulateTransactionResponseDTO::getValue, "simulateTransaction", transaction,
                defaultOptionalParams());
    }

    @Override
    public SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(final String transaction, final SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException
    {
        return queryForObject(new TypeReference<>()
                              {
                              },
                SimulateTransactionResponseDTO::getValue, "simulateTransaction", transaction,
                optionalParams.getParams());
    }

    private <S, T> SolanaClientResponse<S> queryForObject(
            final TypeReference<RpcWrapperDTO<T>> type,
            final Function<T, S> dtoMapper,
            final String method,
            final Object... params) throws SolanaJsonRpcClientException
    {
        final HttpRequest request = prepareRequest(method, params);
        final HttpResponse<String> httpResponse = sendRequest(request);

        final Result<SolanaClientResponse.SolanaClientError, T> response = decodeResponse(type, httpResponse);
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

    private <T> Result<SolanaClientResponse.SolanaClientError, T> decodeResponse(
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
