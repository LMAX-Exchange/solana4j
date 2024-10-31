package com.lmax.solana4j.client.api;

import com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClientException;

/**
 * Represents the API for interacting with the Solana blockchain.
 * This interface provides methods for sending transactions, querying account balances, requesting airdrops,
 * fetching block and slot information, and retrieving account data.
 */
public interface SolanaApi
{
    /**
     * Sends a transaction to the Solana blockchain.
     * The transaction is encoded as a base64-encoded string (transaction blob).
     *
     * @param transactionBlob the base64-encoded string representing the transaction
     * @return the signature of the transaction, which is a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> sendTransaction(String transactionBlob) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the transaction response for a given transaction signature.
     *
     * @param transactionSignature the base58-encoded signature of the transaction
     * @return the {@link TransactionResponse} containing details of the transaction
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TransactionResponse> getTransaction(String transactionSignature) throws SolanaJsonRpcClientException;

    /**
     * Requests an airdrop of lamports to the specified address.
     * This is used for test purposes to receive lamports (the smallest unit of SOL).
     *
     * @param address the base58-encoded public key of the recipient account
     * @param amountLamports the amount of lamports to be airdropped
     * @return the transaction signature as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> requestAirdrop(String address, long amountLamports) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the balance of an account in lamports.
     *
     * @param address the base58-encoded public key of the account
     * @return the balance of the account in lamports
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBalance(String address) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the token account balance of an SPL token account.
     *
     * @param address the base58-encoded public key of the token account
     * @return the {@link TokenAmount} representing the token balance of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TokenAmount> getTokenAccountBalance(String address) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the account information for the specified address.
     * The account information includes the balance, ownership, and data stored in the account.
     *
     * @param address the base58-encoded public key of the account
     * @return the {@link AccountInfo} containing details of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<AccountInfo> getAccountInfo(String address) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the current block height of the Solana blockchain.
     * The block height represents the number of blocks preceding the current block.
     *
     * @return the current block height
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBlockHeight() throws SolanaJsonRpcClientException;

    /**
     * Retrieves the current slot number.
     * A slot is a specific point in time or block on the Solana blockchain.
     *
     * @return the current slot number
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getSlot() throws SolanaJsonRpcClientException;

    /**
     * Retrieves the most recent blockhash.
     * The blockhash ensures that a transaction is processed within a specific time window.
     *
     * @return the {@link Blockhash} representing the most recent blockhash
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Blockhash> getLatestBlockhash() throws SolanaJsonRpcClientException;

    /**
     * Retrieves the minimum balance required for rent exemption for an account of the given size.
     * This is the minimum balance needed to ensure the account is rent-exempt.
     *
     * @param size the size of the account in bytes
     * @return the minimum balance in lamports for rent exemption
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getMinimumBalanceForRentExemption(int size) throws SolanaJsonRpcClientException;
}
