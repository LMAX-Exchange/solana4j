package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lmax.solana4j.solanaclient.api.AccountInfo;
import com.lmax.solana4j.solanaclient.api.Blockhash;
import com.lmax.solana4j.solanaclient.api.Commitment;
import com.lmax.solana4j.solanaclient.api.SolanaApi;
import com.lmax.solana4j.solanaclient.api.TokenAmount;
import com.lmax.solana4j.solanaclient.api.TransactionResponse;

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
    public TransactionResponse getTransactionResponse(final String transactionSignature)
    {
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<TransactionResponseDTO>>()
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
        return rpcClient.queryForObject(new TypeReference<>()
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
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<BalanceDTO>>()
                                        {
                                        },
                "getBalance",
                address).getValue();
    }

    @Override
    public TokenAmount getTokenAccountBalance(final String address)
    {
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<TokenAmountDTO>>()
                                        {
                                        },
                "getTokenAccountBalance",
                address).getValue();
    }

    @Override
    public AccountInfo getAccountInfo(final String address)
    {
        return rpcClient.queryForObject(new TypeReference<RpcWrapperDTO<AccountInfoDTO>>()
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
        return rpcClient.queryForObject(new TypeReference<>()
        {
        }, "getBlockHeight");
    }

    @Override
    public Long getSlot()
    {
        return rpcClient.queryForObject(new TypeReference<>()
                                        {
                                        }, "getSlot");
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
        return rpcClient.queryForObject(new TypeReference<>()
        {
        }, "getMinimumBalanceForRentExemption", size);
    }
}
