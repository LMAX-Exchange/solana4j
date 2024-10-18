package com.lmax.solana4j.client.api;

/**
 * Represents the balance of an SPL token in a Solana transaction or account.
 * This interface provides details about the token, including the account index,
 * the token mint, the owner of the account, the program managing the token, and the user-friendly token amount.
 */
public interface TokenBalance
{
    /**
     * Returns the index of the account in the transaction or account list.
     * The account index represents the position of the account within the transaction or account list.
     *
     * @return the index of the account holding the token balance
     */
    int getAccountIndex();

    /**
     * Returns the mint of the token.
     * The mint is the base58-encoded public key of the token, representing the token's issuance.
     *
     * @return the base58-encoded string representing the token mint
     */
    String getMint();

    /**
     * Returns the owner of the token account.
     * The owner is the base58-encoded public key of the account's owner.
     *
     * @return the base58-encoded string representing the owner of the token account
     */
    String getOwner();

    /**
     * Returns the program ID managing the token.
     * The program ID is the base58-encoded public key of the program responsible for managing the token, typically the SPL token program.
     *
     * @return the base58-encoded string representing the program ID managing the token
     */
    String getProgramId();

    /**
     * Returns the user-friendly token amount.
     * This method provides a user-friendly, scaled version of the token balance using decimals for readability.
     *
     * @return the {@link TokenAmount} representing the token balance in a user-friendly format
     */
    TokenAmount getUiTokenAmount();
}