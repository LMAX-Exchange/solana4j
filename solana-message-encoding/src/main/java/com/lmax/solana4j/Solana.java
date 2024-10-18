package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.InstructionBuilderBase;
import com.lmax.solana4j.api.Message;
import com.lmax.solana4j.api.MessageBuilder;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.util.Base58;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

/**
 * Entrypoint to the library's APIs.
 * <p>
 * This class provides the entrypoint to the underlying APIs that this library provides.
 * </p>
 */
public final class Solana
{

    private Solana()
    {
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
     * Creates a new transaction instruction using the builder provided.
     *
     * @param instructionBuilder the builder of the required instruction
     * @return a new instance of {@link TransactionInstruction}
     */
    public static TransactionInstruction instruction(final Consumer<InstructionBuilderBase> instructionBuilder)
    {
        return SolanaEncoding.instruction(instructionBuilder);
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
     * Creates a new Blockhash from the given byte array.
     *
     * @param bytes the byte array representing the Blockhash
     * @return a new instance of {@link Blockhash}
     */
    public static Blockhash blockhash(final byte[] bytes)
    {
        return SolanaEncoding.blockhash(bytes);
    }

    /**
     * Creates a new Blockhash from the given Base58-encoded string.
     *
     * @param blockhashBase58 the Base58-encoded string representing the Blockhash
     * @return a new instance of {@link Blockhash}
     */
    public static Blockhash blockhash(final String blockhashBase58)
    {
        return SolanaEncoding.blockhash(Base58.decode(blockhashBase58));
    }

    /**
     * Creates a new Slot from the given slot number.
     *
     * @param slot the slot number
     * @return a new instance of {@link Slot}
     */
    public static Slot slot(final long slot)
    {
        return SolanaEncoding.slot(slot);
    }

    /**
     * Creates a new destination for a token transfer.
     *
     * @param destination the public key of the destination
     * @param amount      the amount to transfer
     * @return a new instance of {@link Destination}
     */
    public static Destination destination(final PublicKey destination, final long amount)
    {
        return SolanaEncoding.destination(destination, amount);
    }
}
