package com.lmax.solana4j.solanaclient.api;

public interface SolanaApi
{
    String sendTransaction(String transactionBlob);

    String requestAirdrop(String address, long amountLamports);

    TransactionResponse getTransactionResponse(String transactionSignature);

    Long getBalance(String address);

    TokenAmount getTokenAccountBalance(String address);

    AccountInfo getAccountInfo(String address);

    Long getBlockHeight();

    Long getSlot();

    Blockhash getLatestBlockhash();

    Long getMinimalBalanceForRentExemption(int size);
}
