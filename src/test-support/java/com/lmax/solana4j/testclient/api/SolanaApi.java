package com.lmax.solana4j.testclient.api;

public interface SolanaApi
{
    String sendTransaction(String transactionBlob, Commitment commitment);

    String requestAirdrop(String address, long amountLamports);

    TransactionResponse getTransactionResponse(String transactionSignature, Commitment commitment);

    Long getBalance(String address, Commitment commitment);

    TokenAmount getTokenAccountBalance(String address, Commitment commitment);

    AccountInfo getAccountInfo(String address, Commitment commitment);

    Long getBlockHeight();

    Long getSlot(Commitment commitment);

    Blockhash getRecentBlockHash();

    Long getMinimalBalanceForRentExemption(int size);
}
