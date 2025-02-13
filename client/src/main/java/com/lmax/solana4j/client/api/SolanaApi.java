package com.lmax.solana4j.client.api;

import com.lmax.solana4j.client.jsonrpc.SolanaJsonRpcClientException;

import java.util.List;
import java.util.Map;

/**
 * Represents the API for interacting with a solana node.
 */
public interface SolanaApi
{
    /**
     * Sends a transaction to the blockchain with default optional parameters.
     *
     * @param transactionBlob the base64-encoded string representing the transaction
     * @return a {@link SolanaClientResponse} containing the signature of the transaction as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> sendTransaction(String transactionBlob) throws SolanaJsonRpcClientException;

    /**
     * Sends a transaction to the blockchain with optional parameters.
     *
     * @param transactionBlob the base64-encoded string representing the transaction
     * @param optionalParams  a map of optional parameters to customize the request,
     *                        such as `skipPreflight`, `preflightCommitment`, `maxRetries`, `encoding` and `minContextSlot`
     * @return a {@link SolanaClientResponse} containing the signature of the transaction as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> sendTransaction(String transactionBlob, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the transaction response for a given transaction signature with default optional parameters.
     *
     * @param transactionSignature the base58-encoded signature of the transaction
     * @return a {@link SolanaClientResponse} containing the {@link TransactionResponse}
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TransactionResponse> getTransaction(String transactionSignature) throws SolanaJsonRpcClientException;

    /**
     * Returns the transaction response for a given transaction signature with optional parameters.
     *
     * @param transactionSignature the base58-encoded signature of the transaction
     * @param optionalParams  a map of optional parameters to customize the request such as `commitment`, `maxSupportedTransactionVersion` and `encoding`
     * @return a {@link SolanaClientResponse} containing the {@link TransactionResponse}
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TransactionResponse> getTransaction(String transactionSignature, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Requests an airdrop of lamports to the specified address with default optional parameters.
     *
     * @param address        the base58-encoded public key of the recipient account
     * @param amountLamports the amount of lamports to be airdropped
     * @return a {@link SolanaClientResponse} containing the transaction signature as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> requestAirdrop(String address, long amountLamports) throws SolanaJsonRpcClientException;

    /**
     * Requests an airdrop of lamports to the specified address with optional parameters.
     *
     * @param address        the base58-encoded public key of the recipient account
     * @param amountLamports the amount of lamports to be airdropped
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`
     * @return a {@link SolanaClientResponse} containing the transaction signature as a base58-encoded string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> requestAirdrop(String address, long amountLamports, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the balance of an account in lamports with default optional parameters.
     *
     * @param address the base58-encoded public key of the account.
     * @return a {@link SolanaClientResponse} containing the balance of the account in lamports
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBalance(String address) throws SolanaJsonRpcClientException;

    /**
     * Returns the balance of an account in lamports with optional parameters.
     *
     * @param address        the base58-encoded public key of the account
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment` and `minContextSlot`
     * @return a {@link SolanaClientResponse} containing the balance of the account in lamports
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBalance(String address, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the token account balance of an spl token account with default optional parameters.
     *
     * @param address the base58-encoded public key of the token account
     * @return a {@link SolanaClientResponse} containing the {@link TokenAmount} representing the token balance of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TokenAmount> getTokenAccountBalance(String address) throws SolanaJsonRpcClientException;

    /**
     * Returns the token account balance of an spl token account with optional parameters.
     *
     * @param address        the base58-encoded public key of the token account
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`
     * @return a {@link SolanaClientResponse} containing the {@link TokenAmount} representing the token balance of the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<TokenAmount> getTokenAccountBalance(String address, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the account information at the specified address with default optional parameters.
     *
     * @param address the base58-encoded public key of the account
     * @return a {@link SolanaClientResponse} containing the {@link AccountInfo} representing information about the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<AccountInfo> getAccountInfo(String address) throws SolanaJsonRpcClientException;

    /**
     * Returns the account information for the specified address with optional parameters.
     *
     * @param address the base58-encoded public key of the account
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment` and `encoding`
     * @return a {@link SolanaClientResponse} containing the {@link AccountInfo} representing information about the account
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<AccountInfo> getAccountInfo(String address, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the current block height of the blockchain with default optional parameters.
     *
     * @return a {@link SolanaClientResponse} containing the current block height
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBlockHeight() throws SolanaJsonRpcClientException;

    /**
     * Returns the current block height with optional parameters.
     *
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment` and `minContextSlot`
     * @return a {@link SolanaClientResponse} containing the current block height
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getBlockHeight(SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the current slot number with default optional parameters.
     *
     * @return a {@link SolanaClientResponse} containing the current slot number
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getSlot() throws SolanaJsonRpcClientException;

    /**
     * Returns the current slot number with optional parameters.
     *
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`, and `minContextSlot`
     * @return a {@link SolanaClientResponse} containing the current slot number
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getSlot(SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the most recent blockhash with default optional parameters.
     *
     * @return a {@link SolanaClientResponse} containing the {@link Blockhash} representing the most recent blockhash
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Blockhash> getLatestBlockhash() throws SolanaJsonRpcClientException;

    /**
     * Returns the most recent blockhash with optional parameters.
     *
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment` and `minContextSlot`
     * @return a {@link SolanaClientResponse} containing the {@link Blockhash} representing the most recent blockhash
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Blockhash> getLatestBlockhash(SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the minimum balance required for rent exemption for an account of the given size, with default optional parameters.
     * This is the minimum balance needed to ensure the account is rent-exempt.
     *
     * @param size the size of the account in bytes
     * @return a {@link SolanaClientResponse} containing the minimum balance in lamports for rent exemption
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getMinimumBalanceForRentExemption(int size) throws SolanaJsonRpcClientException;

    /**
     * Returns the minimum balance required for rent exemption for an account of the given size, with optional parameters.
     * This is the minimum balance needed to ensure the account is rent-exempt.
     *
     * @param size           the size of the account in bytes
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`
     * @return a {@link SolanaClientResponse} containing the minimum balance in lamports for rent exemption
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> getMinimumBalanceForRentExemption(int size, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Returns the lowest slot that the node has information about in its ledger.
     *
     * @return a {@link SolanaClientResponse} containing the minimum slot as a long
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<Long> minimumLedgerSlot() throws SolanaJsonRpcClientException;

    /**
     * Returns the current health of the node.
     *
     * @return a {@link SolanaClientResponse} containing the health status as a string
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<String> getHealth() throws SolanaJsonRpcClientException;

    /**
     * Returns a list of transaction signatures for the specified address, with default optional parameters.
     *
     * @param address the base58-encoded public key of the address
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureForAddress} objects enumerating signatures relating to the address
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<List<SignatureForAddress>> getSignaturesForAddress(String address) throws SolanaJsonRpcClientException;

    /**
     * Retrieves a list of transaction signatures for a specified address, with optional parameters.
     *
     * @param address  the base58-encoded public key of the address
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`, `minContextSlot`, `limit`
     *                        `before` and `until`
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureForAddress} objects enumerating signatures relating to the address
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<List<SignatureForAddress>> getSignaturesForAddress(String address, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the statuses of one or more transaction signatures, with default optional parameters.
     *
     * @param transactionSignatures a list of base58-encoded transaction signatures.
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureStatus} objects detailing the signature statuses
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<List<SignatureStatus>> getSignatureStatuses(List<String> transactionSignatures) throws SolanaJsonRpcClientException;

    /**
     * Retrieves the statuses of one or more transaction signatures, with optional parameters.
     * Allows for additional filters or customizations when querying transaction statuses.
     *
     * @param transactionSignatures a list of base58-encoded transaction signatures.
     * @param optionalParams  a map of optional parameters to customize the request, such as `searchTransactionHistory`
     * @return a {@link SolanaClientResponse} containing a list of {@link SignatureStatus} objects detailing the signature statuses
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<List<SignatureStatus>> getSignatureStatuses(List<String> transactionSignatures, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieves token accounts owned by a specific address, filtered by the token mint or program id, with default optional parameters.
     *
     * @param accountDelegate the base58-encoded public key of the account owner
     * @param filter          a key-value pair specifying the filter criteria (e.g., token mint or program id)
     * @return a {@link SolanaClientResponse} containing a list of {@link TokenAccount} objects representing token accounts
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<List<TokenAccount>> getTokenAccountsByOwner(String accountDelegate, Map.Entry<String, String> filter) throws SolanaJsonRpcClientException;

    /**
     * Retrieves token accounts owned by a specific address, filtered by the token mint or program id, with default optional parameters.
     *
     * @param accountDelegate the base58-encoded public key of the account owner.
     * @param filter          a key-value pair specifying the filter criteria (e.g., token mint or program id)
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`, `minContextSlot` and `dataSlice`
     * @return a {@link SolanaClientResponse} containing a list of {@link TokenAccount} objects representing token accounts
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<List<TokenAccount>> getTokenAccountsByOwner(
            String accountDelegate,
            Map.Entry<String, String> filter,
            SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Simulates a transaction without broadcasting it to the blockchain, with default optional parameters.
     *
     * @param transaction the base64-encoded string representing the transaction
     * @return a {@link SolanaClientResponse} containing a {@link SimulateTransactionResponse} object with details of the simulation
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(String transaction) throws SolanaJsonRpcClientException;

    /**
     * Simulates a transaction without broadcasting it to the blockchain, with optional parameters.
     * This allows for additional customizations, such as setting the commitment level or enabling signature verification.
     *
     * @param transaction    the base64-encoded string representing the transaction
     * @param optionalParams  a map of optional parameters to customize the request, such as `commitment`, `sigVerify`, `replaceRecentBlockhash`,
     *                        `minContextSlot`, `encoding`, `innerInstructions` and `accounts`
     * @return a {@link SolanaClientResponse} containing a {@link SimulateTransactionResponse} object with details of the simulation
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<SimulateTransactionResponse> simulateTransaction(String transaction, SolanaClientOptionalParams optionalParams) throws SolanaJsonRpcClientException;

    /**
     * Retrieve the current version of Solana Core and supported Feature Set from the node.
     *
     * @return a {@link SolanaClientResponse} containing a {@link SimulateTransactionResponse} object with the version and feature set
     * @throws SolanaJsonRpcClientException if there is an error with the JSON-RPC request
     */
    SolanaClientResponse<SolanaVersion> getVersion() throws SolanaJsonRpcClientException;
}