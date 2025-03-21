package com.lmax.solana4j.encoding;


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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.function.Consumer;

/**
 * Exposes the library's APIs.
 */
public final class SolanaEncoding
{

    /**
     * The maximum size of a message in bytes.
     */
    public static final int MAX_MESSAGE_SIZE = 1280 - 40 - 8;

    private SolanaEncoding()
    {
    }

    /**
     * Creates a new message builder for the given buffer.
     *
     * @param buffer the ByteBuffer to use for building the message
     * @return a new instance of {@link MessageBuilder}
     */
    public static MessageBuilder builder(final ByteBuffer buffer)
    {
        return new SolanaMessageBuilder(buffer);
    }

    /**
     * Creates a new transaction instructions using the builder provided.
     *
     * @param instructionBuilder the builder of the required instruction
     * @return a new instance of {@link TransactionInstruction}
     */
    public static TransactionInstruction instruction(final Consumer<InstructionBuilderBase> instructionBuilder)
    {
        final SolanaTransactionBuilder.SolanaTransactionInstructionBuilderBase builder = new SolanaTransactionBuilder.SolanaTransactionInstructionBuilderBase();
        instructionBuilder.accept(builder);
        return builder.build();
    }

    /**
     * Creates a new signed message builder for the given message buffer.
     *
     * @param message the ByteBuffer containing the message
     * @return a new instance of {@link SignedMessageBuilder}
     */
    public static SignedMessageBuilder forSigning(final ByteBuffer message)
    {
        return new SolanaSignedMessageBuilder(message);
    }

    /**
     * Reads a message from the given buffer.
     *
     * @param message the ByteBuffer containing the message
     * @return a new instance of {@link Message}
     */
    public static Message read(final ByteBuffer message)
    {
        return new SolanaMessageReader(message).read();
    }

    /**
     * Creates a new public key from the given byte array.
     *
     * @param bytes the byte array representing the public key
     * @return a new instance of {@link PublicKey}
     */
    public static PublicKey account(final byte[] bytes)
    {
        return new SolanaAccount(bytes);
    }

    /**
     * Creates a new public key from the given string.
     *
     * @param accountBase58 the base58 string representing the public key
     * @return a new instance of {@link PublicKey}
     */
    public static PublicKey account(final String accountBase58)
    {
        return new SolanaAccount(Base58.decode(accountBase58));
    }

    /**
     * Derives a program address from the given seeds and program id.
     *
     * @param seeds     the list of byte arrays representing the seeds
     * @param programId the public key of the program id
     * @return a new instance of {@link ProgramDerivedAddress}
     */
    public static ProgramDerivedAddress deriveProgramAddress(final List<byte[]> seeds, final PublicKey programId)
    {
        return SolanaProgramDerivedAddress.deriveProgramAddress(seeds, programId);
    }

    /**
     * Creates a new blockhash from the given byte array.
     *
     * @param bytes the byte array representing the blockhash
     * @return a new instance of {@link Blockhash}
     */
    public static Blockhash blockhash(final byte[] bytes)
    {
        return new SolanaBlockhash(bytes);
    }

    /**
     * Creates a new blockhash from the given string.
     *
     * @param blockhashBase58 the string representing the blockhash
     * @return a new instance of {@link Blockhash}
     */
    public static Blockhash blockhash(final String blockhashBase58)
    {
        return new SolanaBlockhash(Base58.decode(blockhashBase58));
    }

    /**
     * Creates a new slot from the given slot number.
     *
     * @param slot the slot number
     * @return a new instance of {@link Slot}
     */
    public static Slot slot(final long slot)
    {
        final ByteBuffer recentSlotBuffer = ByteBuffer.allocate(8);
        recentSlotBuffer.order(ByteOrder.LITTLE_ENDIAN);
        recentSlotBuffer.putLong(slot);

        return new SolanaSlot(recentSlotBuffer.array());
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
        return new SolanaDestination(destination, amount);
    }

    /**
     * Creates a new address lookup table from the given lookup table address and address lookups.
     *
     * @param lookupTableAddress the public key of the lookup table address
     * @param addressLookups     the list of public keys for address lookups
     * @return a new instance of {@link AddressLookupTable}
     */
    public static AddressLookupTable addressLookupTable(final PublicKey lookupTableAddress, final List<PublicKey> addressLookups)
    {
        return new SolanaAddressLookupTable(lookupTableAddress, addressLookups);
    }

    /**
     * Decodes a base58-encoded string into a byte array.
     *
     * @param base58 the Base58-encoded string to decode
     * @return a byte array representing the decoded value of the Base58 string
     * @throws IllegalArgumentException if the input string is not a valid Base58 encoded string
     */
    public static byte[] decodeBase58(final String base58)
    {
        return Base58.decode(base58);
    }

    /**
     * Encodes a byte array into a base58-encoded string.
     *
     * @param bytes the byte array to encode into a base58 string
     * @return a base58-encoded string representing the input byte array
     */
    public static String encodeBase58(final byte[] bytes)
    {
        return Base58.encode(bytes);
    }


    /**
     * Copies the contents of a {@link ByteBuffer} into a new byte array.
     * <p>
     * This method creates a new byte array that contains the exact content of the provided {@code ByteBuffer}.
     * The position, limit, and capacity of the original {@code ByteBuffer} are not modified.
     * </p>
     *
     * @param buffer the {@code ByteBuffer} to copy. Must not be {@code null}.
     * @return a byte array containing the copied data from the {@code ByteBuffer}.
     * @throws NullPointerException if the provided {@code buffer} is {@code null}.
     */
    public static byte[] copyBuffer(final ByteBuffer buffer)
    {
        return ByteBufferPrimitiveArray.copy(buffer);
    }
}
