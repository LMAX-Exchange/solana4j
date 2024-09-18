package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

/**
 * Represents a program for interacting with the BPF Loader Upgradeable program on the Solana blockchain.
 * This program allows for managing upgradeable programs, such as setting a new upgrade authority.
 */
public final class BpfLoaderUpgradeableProgram
{
    /**
     * The Program ID for the BPF Loader Upgradeable program.
     */
    private static final byte[] PROGRAM_ID = Base58.decode("BPFLoaderUpgradeab1e11111111111111111111111");

    /**
     * The public key associated with the BPF Loader Upgradeable program.
     */
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(PROGRAM_ID);

    /**
     * The instruction code for setting a new upgrade authority.
     */
    private static final int SET_AUTHORITY_INSTRUCTION = 4;

    /**
     * The transaction builder used to construct and manage Solana transactions.
     */
    private final TransactionBuilder tb;

    /**
     * Factory method to create an instance of {@code BpfLoaderUpgradeableProgram} with a specified transaction builder.
     *
     * @param tb The transaction builder to use for constructing transactions.
     * @return A new instance of {@code BpfLoaderUpgradeableProgram}.
     */
    public static BpfLoaderUpgradeableProgram factory(final TransactionBuilder tb)
    {
        return new BpfLoaderUpgradeableProgram(tb);
    }

    /**
     * Constructs a new instance of {@code BpfLoaderUpgradeableProgram} with the specified transaction builder.
     *
     * @param tb The transaction builder to use for constructing transactions.
     */
    BpfLoaderUpgradeableProgram(final TransactionBuilder tb)
    {
        this.tb = tb;
    }

    /**
     * Sets the upgrade authority for a given program address.
     *
     * @param programAddress           The public key of the program whose upgrade authority is being changed.
     * @param currentUpgradeAuthorityAddress  The current upgrade authority's public key.
     * @param maybeNewUpgradeAuthorityAddress An optional public key for the new upgrade authority. If not present, no new upgrade authority is set.
     * @return The current instance of {@code BpfLoaderUpgradeableProgram} to allow method chaining.
     */
    public BpfLoaderUpgradeableProgram setUpgradeAuthority(
            final PublicKey programAddress,
            final PublicKey currentUpgradeAuthorityAddress,
            final Optional<PublicKey> maybeNewUpgradeAuthorityAddress)
    {
        tb.append(ib ->
        {
            ib.program(PROGRAM_ACCOUNT)
                    .account(deriveAddress(programAddress).address(), false, true)
                    .account(currentUpgradeAuthorityAddress, true, false);
            maybeNewUpgradeAuthorityAddress.ifPresent(newUpgradeAuthorityAddress -> ib.account(newUpgradeAuthorityAddress, false, false));
            ib.data(4, bb -> bb.order(ByteOrder.LITTLE_ENDIAN).putInt(SET_AUTHORITY_INSTRUCTION));
        });

        return this;
    }

    /**
     * Derives a program-derived address for the specified program public key.
     *
     * @param program The public key of the program.
     * @return The derived program address.
     */
    public static ProgramDerivedAddress deriveAddress(final PublicKey program)
    {
        return SolanaEncoding.deriveProgramAddress(List.of(program.bytes()), PROGRAM_ACCOUNT);
    }
}
