package com.lmax.solana4j.api;

/**
 * Interface representing a program derived address.
 * <p>
 * A program derived address (PDA) is an address derived from a program ID and a set of seeds.
 * PDAs are used for various purposes, including managing program state and associated accounts.
 * </p>
 */
public interface ProgramDerivedAddress
{

    /**
     * Returns the public key of the derived address.
     *
     * @return the {@link PublicKey} of the derived address
     */
    PublicKey address();

    /**
     * Returns the public key of the program ID used to derive the address.
     *
     * @return the {@link PublicKey} of the program ID
     */
    PublicKey programId();

    /**
     * Returns the nonce used to derive the address.
     *
     * @return the nonce used in the derivation process
     */
    int nonce();
}
