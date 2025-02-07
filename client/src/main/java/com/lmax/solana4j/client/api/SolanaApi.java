package com.lmax.solana4j.client.api;

import com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClientException;

import java.util.List;
import java.util.Map;

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
     * Additional optional parameters can be passed to customize the transaction request.
     *
     * @param transactionBlob the base64-encoded string representing the transaction
     * @param optionalParams  a map of optional parameters to customize the transaction request,
     *                        such as `skipPreflight`, `preflightCommitment`, or other Solana JSON-RPC options
     * @return the signature of the transaction, which is a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> sendTransaction(String transactionBlob, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Sends a transaction to the Solana blockchain without optional parameters.
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
     * Retrieves the transaction response for a given transaction signature with optional parameters.
     *
     * @param transactionSignature the base58-encoded signature of the transaction
     * @param optionalParams       optional parameters for customizing the request
     * @return the {@link TransactionResponse} containing details of the transaction
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TransactionResponse> getTransaction(String transactionSignature, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Requests an airdrop of lamports to the specified address.
     * This is used for test purposes to receive lamports (the smallest unit of SOL).
     *
     * @param address        the base58-encoded public key of the recipient account
     * @param amountLamports the amount of lamports to be airdropped
     * @return the transaction signature as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> requestAirdrop(String address, long amountLamports) throws SolanaJsonRpcClientException;

    /**
     * Requests an airdrop of lamports to the specified address with optional parameters.
     *
     * @param address        the base58-encoded public key of the recipient account
     * @param amountLamports the amount of lamports to be airdropped
     * @param optionalParams optional parameters for customizing the request
     * @return the transaction signature as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> requestAirdrop(String address, long amountLamports, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the balance of an account in lamports.
     *
     * @param address the base58-encoded public key of the account
     * @return the balance of the account in lamports
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBalance(String address) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the balance of an account in lamports with optional parameters.
     *
     * @param address        the base58-encoded public key of the account
     * @param optionalParams optional parameters for customizing the request
     * @return the balance of the account in lamports
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBalance(String address, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the token account balance of an SPL token account.
     *
     * @param address the base58-encoded public key of the token account
     * @return the {@link TokenAmount} representing the token balance of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TokenAmount> getTokenAccountBalance(String address) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the token account balance of an SPL token account with optional parameters.
     *
     * @param address        the base58-encoded public key of the token account
     * @param optionalParams optional parameters for customizing the request
     * @return the {@link TokenAmount} representing the token balance of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TokenAmount> getTokenAccountBalance(String address, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

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
     * Retrieves the account information for the specified address with optional parameters.
     *
     * @param address the base58-encoded public key of the account
     * @param params  optional parameters for customizing the request
     * @return the {@link AccountInfo} containing details of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<AccountInfo> getAccountInfo(String address, SolanaClientOptionalParams params) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the current block height of the Solana blockchain.
     * The block height represents the number of blocks preceding the current block.
     *
     * @return the current block height
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBlockHeight() throws SolanaJsonRpcClientException;

    /**
     * Retrieves the current block height with optional parameters.
     *
     * @param optionalParams optional parameters for customizing the request
     * @return the current block height
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBlockHeight(SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the current slot number.
     * A slot is a specific point in time or block on the Solana blockchain.
     *
     * @return the current slot number
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getSlot() throws SolanaJsonRpcClientException;

    /**
     * Retrieves the current slot number with optional parameters.
     *
     * @param optionalParams optional parameters for customizing the request
     * @return the current slot number
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getSlot(SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the most recent blockhash.
     * The blockhash ensures that a transaction is processed within a specific time window.
     *
     * @return the {@link Blockhash} representing the most recent blockhash
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Blockhash> getLatestBlockhash() throws SolanaJsonRpcClientException;

    /**
     * Retrieves the most recent blockhash with optional parameters.
     *
     * @param optionalParams optional parameters for customizing the request
     * @return the {@link Blockhash} representing the most recent blockhash
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Blockhash> getLatestBlockhash(SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the minimum balance required for rent exemption for an account of the given size.
     * This is the minimum balance needed to ensure the account is rent-exempt.
     *
     * @param size the size of the account in bytes
     * @return the minimum balance in lamports for rent exemption
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getMinimumBalanceForRentExemption(int size) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the minimum balance required for rent exemption for an account of the given size,
     * with additional parameters to customize the request.
     * This is the minimum balance needed to ensure the account is rent-exempt.
     *
     * @param size           the size of the account in bytes
     * @param optionalParams additional parameters to customize the rent exemption query
     * @return the minimum balance in lamports for rent exemption
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getMinimumBalanceForRentExemption(int size, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the lowest slot that the node has information about in its ledger.
     *
     * @return a {@link SolanaClientResponse} containing the minimum slot as a {@link Long}, or an error
     * if the operation was unsuccessful.
     * @throws SolanaJsonRpcClientException if there is an error in the JSON-RPC request or response.
     */
    SolanaClientResponse<Long> minimumLedgerSlot() throws SolanaJsonRpcClientException;


    /**
     * Returns the current health of the node. A healthy node is one that is within
     * HEALTH_CHECK_SLOT_DISTANCE slots of the latest cluster confirmed slot.
     *
     * @return a {@link SolanaClientResponse} containing the health status as a {@link String}. A
     * response of "ok" typically indicates a healthy node.
     * @throws SolanaJsonRpcClientException if there is an error in the JSON-RPC request or response.
     */
    SolanaClientResponse<String> getHealth() throws SolanaJsonRpcClientException;

    /**
     * Retrieves a list of transaction signatures for a specified address.
     * This method is used to query the transaction history of the given address.
     *
     * @param addressBase58 the base58-encoded public key of the address.
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureForAddress} objects.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<List<SignatureForAddress>> getSignaturesForAddress(String addressBase58) throws SolanaJsonRpcClientException;

    /**
     * Retrieves a list of transaction signatures for a specified address with optional parameters.
     * Allows for additional filters or customizations when querying the transaction history.
     *
     * @param addressBase58  the base58-encoded public key of the address.
     * @param optionalParams optional parameters for the query, such as limiting results or setting commitment levels.
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureForAddress} objects.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<List<SignatureForAddress>> getSignaturesForAddress(String addressBase58, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the statuses of one or more transaction signatures.
     * The statuses include confirmation levels, error information, and slot numbers.
     *
     * @param transactionSignatures a list of base58-encoded transaction signatures.
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureStatus} objects.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<List<SignatureStatus>> getSignatureStatuses(List<String> transactionSignatures) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the statuses of one or more transaction signatures with optional parameters.
     * Allows for additional filters or customizations when querying transaction statuses.
     *
     * @param transactionSignatures a list of base58-encoded transaction signatures.
     * @param optionalParams        optional parameters for the query, such as searching transaction history.
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureStatus} objects.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<List<SignatureStatus>> getSignatureStatuses(List<String> transactionSignatures, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves token accounts owned by a specific address, filtered by criteria.
     * Useful for finding SPL token accounts based on token mint or program ID.
     *
     * @param accountDelegate the base58-encoded public key of the account owner.
     * @param filter          a key-value pair specifying the filter criteria (e.g., token mint or program ID).
     * @return a {@link SolanaClientResponse} containing a list of {@link TokenAccount} objects.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<List<TokenAccount>> getTokenAccountsByOwner(String accountDelegate, Map.Entry<String, String> filter) throws SolanaJsonRpcClientException;

    /**
     * Retrieves token accounts owned by a specific address, filtered by criteria, with optional parameters.
     * Allows for additional customization, such as setting a specific commitment level.
     *
     * @param accountDelegate the base58-encoded public key of the account owner.
     * @param filter          a key-value pair specifying the filter criteria (e.g., token mint or program ID).
     * @param optionalParams  optional parameters for the query.
     * @return a {@link SolanaClientResponse} containing a list of {@link TokenAccount} objects.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<List<TokenAccount>> getTokenAccountsByOwner(
            String accountDelegate,
            Map.Entry<String, String> filter,
            SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Simulates a transaction without broadcasting it to the Solana blockchain.
     * This method is useful for debugging or testing transaction execution.
     *
     * @param transaction the base64-encoded string representing the transaction.
     * @return a {@link SolanaClientResponse} containing a {@link SimulateTransactionResponse} object with the simulation results.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(String transaction) throws SolanaJsonRpcClientException;

    /**
     * Simulates a transaction without broadcasting it to the Solana blockchain, with optional parameters.
     * This allows for additional customizations, such as setting the commitment level or enabling signature verification.
     *
     * @param transaction    the base64-encoded string representing the transaction.
     * @param optionalParams additional parameters for the simulation.
     * @return a {@link SolanaClientResponse} containing a {@link SimulateTransactionResponse} object with the simulation results.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(String transaction, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieve the current version of Solana Core and supported Feature Set from the Solana node.
     * @return a {@link SolanaClientResponse} containing a {@link SimulateTransactionResponse} object with the version and feature set.
     * @throws SolanaJsonRpcClientException if the request fails.
     */
    SolanaClientResponse<SolanaVersion> getVersion() throws SolanaJsonRpcClientException;
}