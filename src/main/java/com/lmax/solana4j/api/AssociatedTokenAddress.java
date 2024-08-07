package com.lmax.solana4j.api;

/**
 * Interface representing an associated token address in the Solana blockchain.
 * <p>
 * This interface extends {@link ProgramDerivedAddress} and provides additional methods
 * to retrieve the mint and owner public keys associated with the token address.
 * </p>
 */
public interface AssociatedTokenAddress extends ProgramDerivedAddress
{

    /**
     * Returns the public key of the mint associated with this token address.
     *
     * @return the {@link PublicKey} of the mint
     */
    PublicKey mint();

    /**
     * Returns the public key of the owner associated with this token address.
     *
     * @return the {@link PublicKey} of the owner
     */
    PublicKey owner();
}

