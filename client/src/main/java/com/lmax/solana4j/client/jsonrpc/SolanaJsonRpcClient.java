package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.TokenAmount;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class SolanaJsonRpcClient implements SolanaApi
{
    private final String rpcUrl;
    private final HttpClient httpClient;
    private final SolanaCodec solanaCodec;

    public SolanaJsonRpcClient(final String rpcUrl)
    {
        this.rpcUrl = rpcUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.solanaCodec = new SolanaCodec(false);
    }

    SolanaJsonRpcClient(final String rpcUrl, final boolean failOnUnknownProperties)
    {
        this.rpcUrl = rpcUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.solanaCodec = new SolanaCodec(failOnUnknownProperties);
    }

    @Override
    public String requestAirdrop(final String address, final long amountLamports)
    {
        return queryForObject(new TypeReference<>()
        {
        }, "requestAirdrop", address, amountLamports);
    }

    @Override
    public TransactionResponse getTransaction(final String transactionSignature)
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
                                        {
                                        },
                "getTransaction",
                transactionSignature,
                Map.of(
                        "econding", "jsonParsed",
                        "maxSupportedTransactionVersion", 0
                )
        );
    }

    @Override
    public String sendTransaction(final String transactionBlob)
    {
        return queryForObject(new TypeReference<>()
                                        {
                                        },
                "sendTransaction",
                transactionBlob,
                Map.of("preflightCommitment", Commitment.FINALIZED.toString().toLowerCase(Locale.UK))
        );
    }

    @Override
    public Long getBalance(final String address)
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<BalanceDTO>>()
                                        {
                                        },
                "getBalance",
                address).getValue();
    }

    @Override
    public TokenAmount getTokenAccountBalance(final String address)
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<TokenAmountDTO>>()
                                        {
                                        },
                "getTokenAccountBalance",
                address).getValue();
    }

    @Override
    public AccountInfo getAccountInfo(final String address)
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<AccountInfoDTO>>()
                                        {
                                        },
                "getAccountInfo",
                address,
                Map.of("encoding", "base64")
        ).getValue();
    }

    @Override
    public Long getBlockHeight()
    {
        return queryForObject(new TypeReference<>()
        {
        }, "getBlockHeight");
    }

    @Override
    public Long getSlot()
    {
        return queryForObject(new TypeReference<>()
        {
        }, "getSlot");
    }

    @Override
    public Blockhash getLatestBlockhash()
    {
        return queryForObject(new TypeReference<RpcWrapperDTO<BlockhashDTO>>()
        {
        }, "getLatestBlockhash").getValue();
    }

    @Override
    public Long getMinimumBalanceForRentExemption(final int size)
    {
        return queryForObject(new TypeReference<>()
        {
        }, "getMinimumBalanceForRentExemption", size);
    }

    private <T> T queryForObject(final TypeReference<RpcWrapperDTO<T>> type, final String method, final Object... params)
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
        request.uri(URI.create(rpcUrl));
        request.setHeader("Content-Type", "application/json");
        request.POST(HttpRequest.BodyPublishers.ofString(payload));

        return request.build();
    }
}
