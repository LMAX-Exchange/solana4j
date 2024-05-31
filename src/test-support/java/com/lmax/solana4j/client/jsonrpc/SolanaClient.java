package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.TokenAmount;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.util.Locale;
import java.util.Map;

public class SolanaClient implements SolanaApi
{
    final SolanaRpcClient rpcClient;

    public SolanaClient(final String rpcUrl)
    {
        this.rpcClient = new SolanaRpcClient(rpcUrl);
    }

    @Override
    public String requestAirdrop(final String address, final long amountLamports)
    {
        return rpcClient.queryForObject(new TypeReference<>()
        {
        }, "requestAirdrop", address, amountLamports);
    }

    @Override
    public TransactionResponse getTransactionResponse(final String transactionSignature, final Commitment commitment)
    {
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
                                        {
                                        },
                "getTransaction",
                // solana config set --url http://localhost:8899 on the node
                transactionSignature, Map.of(
                        "commitment", commitment.name().toLowerCase(Locale.UK),
                        "econding", "jsonParsed",
                        "maxSupportedTransactionVersion", 0)
        );
    }

    @Override
    public String sendTransaction(final String transactionBlob, final Commitment commitment)
    {
        // there is something wrong here
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<String>>()
                                        {
                                        },
                "sendTransaction",
                transactionBlob,
                Map.of("preflightCommitment", commitment.name().toLowerCase(Locale.UK))
        );
    }

    @Override
    public Long getBalance(final String address, final Commitment commitment)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public TokenAmount getTokenAccountBalance(final String address, final Commitment commitment)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccountInfo getAccountInfo(final String address, final Commitment commitment)
    {
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<AccountInfoDTO>>()
                                        {
                                        },
                "getAccountInfo",
                address,
                Map.of("encoding", "base64",
                        "commitment", commitment.name().toLowerCase(Locale.UK))
        ).getValue();
    }

    @Override
    public Long getBlockHeight()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getSlot(final Commitment commitment)
    {
        return rpcClient.queryForObject(new TypeReference<>()
                                        {
                                        },
                "getSlot",
                Map.of("commitment", commitment.name().toLowerCase(Locale.UK)));
    }

    @Override
    public Blockhash getRecentBlockHash()
    {
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<BlockhashDTO>>()
        {
        }, "getRecentBlockhash").getValue();
    }

    @Override
    public Long getMinimalBalanceForRentExemption(final int size)
    {
        throw new UnsupportedOperationException();
    }
}
