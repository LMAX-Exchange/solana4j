package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.InnerTransactionBuilder;
import com.lmax.solana4j.api.Message;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Utility class for Solana blockchain operations.
 * <p>
 * This class provides various utility methods for creating, reading, and manipulating Solana blockchain data structures.
 * It utilizes the Solana encoding to facilitate operations on different blockchain entities such as accounts, messages,
 * transactions, and more.
 * </p>
 * <p>
 * This class is designed to be non-instantiable and serves as a static utility holder.
 * </p>
 */
public final class Solana
{

    private Solana()
    {
        // Private constructor to prevent instantiation
    }

    /**
     * The maximum size for a message.
     * <p>
     * This constant defines the maximum allowable size for a message in the Solana encoding.
     * It is set to {@link SolanaEncoding#MAX_MESSAGE_SIZE}.
     * </p>
     */
    public static final int MAX_MESSAGE_SIZE = SolanaEncoding.MAX_MESSAGE_SIZE;

    /**
     * Creates a new message builder for the given buffer.
     *
     * @param buffer the {@link ByteBuffer} to use for building the message
     * @return a new instance of {@link MessageBuilder}
     */
    public static MessageBuilder builder(final ByteBuffer buffer)
    {
        return SolanaEncoding.builder(buffer);
    }

    /**
     * Creates a new inner transaction builder.
     *
     * @return a new instance of {@link InnerTransactionBuilder}
     */
    public static InnerTransactionBuilder innerTxBuilder()
    {
        return SolanaEncoding.innerTxBuilder();
    }

    /**
     * Creates a new signed message builder for the given message buffer.
     *
     * @param message the {@link ByteBuffer} containing the message
     * @return a new instance of {@link SignedMessageBuilder}
     */
    public static SignedMessageBuilder forSigning(final ByteBuffer message)
    {
        return SolanaEncoding.forSigning(message);
    }

    /**
     * Reads a message from the given buffer.
     *
     * @param message the {@link ByteBuffer} containing the message
     * @return a new instance of {@link Message}
     */
    public static Message read(final ByteBuffer message)
    {
        return SolanaEncoding.read(message);
    }

    /**
     * Creates a new public key from the given byte array.
     *
     * @param bytes the byte array representing the public key
     * @return a new instance of {@link PublicKey}
     */
    public static PublicKey account(final byte[] bytes)
    {
        return SolanaEncoding.account(bytes);
    }

    /**
     * Creates an address lookup table for the given lookup table address and list of addresses.
     *
     * @param lookupTableAddress the public key of the lookup table
     * @param addresses          the list of public keys representing the addresses
     * @return a new instance of {@link AddressLookupTable}
     */
    public static AddressLookupTable addressLookupTable(final PublicKey lookupTableAddress, final List<PublicKey> addresses)
    {
        return SolanaEncoding.addressLookupTable(lookupTableAddress, addresses);
    }

    /**
     * Derives a program address from the given seeds and program ID.
     *
     * @param seeds     the list of byte arrays representing the seeds
     * @param programId the public key of the program ID
     * @return a new instance of {@link ProgramDerivedAddress}
     */
    public static ProgramDerivedAddress programDerivedAddress(final List<byte[]> seeds, final PublicKey programId)
    {
        return SolanaEncoding.deriveProgramAddress(seeds, programId);
    }

    /**
     * Derives an associated token address for the given owner, mint, and token program account.
     *
     * @param owner               the public key of the owner
     * @param mint                the public key of the mint
     * @param tokenProgramAccount the public key of the token program account
     * @return a new instance of {@link AssociatedTokenAddress}
     */
    public static AssociatedTokenAddress associatedTokenAddress(final PublicKey owner, final PublicKey mint, final PublicKey tokenProgramAccount)
    {
        return SolanaEncoding.deriveAssociatedTokenAddress(owner, mint, tokenProgramAccount);
    }

    /**
     * Creates a new blockhash from the given byte array.
     *
     * @param bytes the byte array representing the blockhash
     * @return a new instance of {@link Blockhash}
     */
    public static Blockhash blockhash(final byte[] bytes)
    {
        return SolanaEncoding.blockhash(bytes);
    }

    /**
     * Creates a new slot from the given slot number.
     *
     * @param slot the slot number
     * @return a new instance of {@link Slot}
     */
    public static Slot slot(final long slot)
    {
        return SolanaEncoding.slot(slot);
    }
}
