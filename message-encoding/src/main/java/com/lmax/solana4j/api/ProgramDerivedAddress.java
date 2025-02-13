package com.lmax.solana4j.api;

/**
 * Interface representing a program derived address.
 * <p>
 * A program derived address is an address derived from a program id and a set of seeds.
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
     * Returns the public key of the program id used to derive the address.
     *
     * @return the {@link PublicKey} of the program id
     */
    PublicKey programId();

    /**
     * Returns the nonce used to derive the address.
     *
     * @return the nonce used in the derivation process
     */
    int nonce();
}
