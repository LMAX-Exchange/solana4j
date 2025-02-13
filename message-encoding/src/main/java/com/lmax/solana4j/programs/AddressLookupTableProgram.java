package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Program for managing address lookup tables on the blockchain.
 */
public final class AddressLookupTableProgram
{

    private static final byte[] ADDRESS_LOOKUP_TABLE_PROGRAM = SolanaEncoding.decodeBase58("AddressLookupTab1e1111111111111111111111111");

    /**
     * The public key for the address lookup table program account.
     * <p>
     * This constant defines the public key associated with the solana account for the address lookup table program.
     * It is set to the value returned by {@link Solana#account(byte[])} using the {@link #ADDRESS_LOOKUP_TABLE_PROGRAM}.
     * </p>
     */
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(ADDRESS_LOOKUP_TABLE_PROGRAM);

    /**
     * The instruction code for creating a lookup table.
     * <p>
     * This constant defines the instruction code used to create a new address lookup table in Solana.
     * </p>
     */
    public static final int CREATE_LOOKUP_TABLE_INSTRUCTION = 0;

    /**
     * The instruction code for extending a lookup table.
     * <p>
     * This constant defines the instruction code used to extend an existing address lookup table in Solana.
     * </p>
     */
    public static final int EXTEND_LOOKUP_TABLE_INSTRUCTION = 2;

    /**
     * The size of the lookup table metadata in bytes.
     * <p>
     * This constant defines the fixed size of the metadata for an address lookup table in Solana.
     * </p>
     */
    public static final int LOOKUP_TABLE_META_SIZE = 56;

    private AddressLookupTableProgram()
    {
    }

    /**
     * Factory method for creating a new instance of {@code AddressLookupTableProgramFactory}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code AddressLookupTableProgramFactory}
     */
    public static AddressLookupTableProgramFactory factory(final TransactionBuilder tb)
    {
        return new AddressLookupTableProgramFactory(tb);
    }

    /**
     * Inner factory class to handle the creation and extension of address lookup tables using a {@link TransactionBuilder}.
     */
    public static final class AddressLookupTableProgramFactory
    {

        private final TransactionBuilder tb;

        /**
         * Private constructor to initialize the factory with the given transaction builder.
         *
         * @param tb the transaction builder
         */
        private AddressLookupTableProgramFactory(final TransactionBuilder tb)
        {
            this.tb = tb;
        }

        /**
         * Creates a new address lookup table.
         *
         * @param programDerivedAddress the program derived address
         * @param authority             the authority public key
         * @param payer                 the payer public key
         * @param recentSlot            the recent slot
         * @return this {@code AddressLookupTableProgramFactory} instance
         */
        public AddressLookupTableProgramFactory createLookupTable(final ProgramDerivedAddress programDerivedAddress, final PublicKey authority, final PublicKey payer, final Slot recentSlot)
        {
            tb.append(AddressLookupTableProgram.createLookupTable(programDerivedAddress, authority, payer, recentSlot));
            return this;
        }

        /**
         * Extends an existing address lookup table with new addresses.
         *
         * @param lookupTable the lookup table public key
         * @param authority   the authority public key
         * @param payer       the payer public key
         * @param addresses   the list of new addresses to add
         * @return this {@code AddressLookupTableProgramFactory} instance
         */
        public AddressLookupTableProgramFactory extendLookupTable(final PublicKey lookupTable, final PublicKey authority, final PublicKey payer, final List<PublicKey> addresses)
        {
            tb.append(AddressLookupTableProgram.extendLookupTable(lookupTable, authority, payer, addresses));
            return this;
        }
    }

    /**
     * Creates a new address lookup table.
     *
     * @param programDerivedAddress the program derived address
     * @param authority             the authority public key
     * @param payer                 the payer public key
     * @param recentSlot            the recent slot
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction createLookupTable(final ProgramDerivedAddress programDerivedAddress, final PublicKey authority, final PublicKey payer, final Slot recentSlot)
    {
        return Solana.instruction(ib -> ib
                .program(PROGRAM_ACCOUNT)
                .account(programDerivedAddress.address(), false, true)
                .account(authority, true, false)
                .account(payer, true, false)
                .account(SystemProgram.SYSTEM_PROGRAM_ACCOUNT, false, false)
                .data(13, bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN).putInt(CREATE_LOOKUP_TABLE_INSTRUCTION);
                    recentSlot.write(bb);
                    bb.put((byte) programDerivedAddress.nonce());
                })
        );
    }

    /**
     * Extends an existing address lookup table with new addresses.
     *
     * @param lookupTable the lookup table public key
     * @param authority   the authority public key
     * @param payer       the payer public key
     * @param addresses   the list of new addresses to add
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction extendLookupTable(final PublicKey lookupTable, final PublicKey authority, final PublicKey payer, final List<PublicKey> addresses)
    {
        return Solana.instruction(ib -> ib
                .program(PROGRAM_ACCOUNT)
                .account(lookupTable, false, true)
                .account(authority, true, false)
                .account(payer, true, false)
                .account(SystemProgram.SYSTEM_PROGRAM_ACCOUNT, false, false)
                .data(12 + (addresses.size() * 32), bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN).putInt(EXTEND_LOOKUP_TABLE_INSTRUCTION);
                    bb.putLong(addresses.size());
                    addresses.forEach(address -> address.write(bb));
                })
        );
    }

    /**
     * Derives a program address for the given authority and slot.
     *
     * @param authority the authority public key
     * @param slot      the slot
     * @return the derived program address
     */
    public static ProgramDerivedAddress deriveAddress(final PublicKey authority, final Slot slot)
    {
        return SolanaEncoding.deriveProgramAddress(List.of(authority.bytes(), slot.bytes()), AddressLookupTableProgram.PROGRAM_ACCOUNT);
    }

    /**
     * Deserializes an address lookup table from the given data.
     *
     * @param lookupTableAddress the lookup table public key
     * @param lookupAddressData  the serialized lookup table data
     * @return the deserialized address lookup table
     */
    public static AddressLookupTable deserializeAddressLookupTable(final PublicKey lookupTableAddress, final byte[] lookupAddressData)
    {
        final int serializedAddressesLength = lookupAddressData.length - LOOKUP_TABLE_META_SIZE;

        assert serializedAddressesLength >= 0;
        assert serializedAddressesLength % 32 == 0;

        final int numberOfSerializedAddresses = serializedAddressesLength / 32;

        final ByteBuffer lookupTable = ByteBuffer.allocate(lookupAddressData.length);
        lookupTable.put(lookupAddressData);
        lookupTable.position(LOOKUP_TABLE_META_SIZE);

        final List<PublicKey> addresses = new ArrayList<>();
        for (int i = 0; i < numberOfSerializedAddresses; i++)
        {
            final byte[] account = new byte[32];
            lookupTable.get(account, 0, 32);
            final PublicKey address = Solana.account(account);
            addresses.add(address);
        }

        return SolanaEncoding.addressLookupTable(lookupTableAddress, addresses);
    }
}
