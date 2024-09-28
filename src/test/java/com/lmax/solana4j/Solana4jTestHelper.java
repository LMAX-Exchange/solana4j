package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.ByteBufferSigner;
import com.lmax.solana4j.api.Message;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionInstruction;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Fail;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class Solana4jTestHelper
{
    public static final int ACCOUNT_LENGTH = 32;
    public static final int BLOCKHASH_LENGTH = 32;
    public static final int SIGNATURE_LENGTH = 64;
    public static final int LEGACY_HEADER_LENGTH = 3;
    public static final int V0_HEADER_LENGTH = 4;

    public static final byte[] PAYER = newBlob(ACCOUNT_LENGTH, (byte) 1);
    public static final byte[] BLOCKHASH = newBlob(BLOCKHASH_LENGTH, (byte) 2);
    public static final byte[] PROGRAM1 = newBlob(ACCOUNT_LENGTH, (byte) 3);
    public static final byte[] PROGRAM2 = newBlob(ACCOUNT_LENGTH, (byte) 4);
    public static final byte[] ACCOUNT1 = newBlob(ACCOUNT_LENGTH, (byte) 5);
    public static final byte[] ACCOUNT2 = newBlob(ACCOUNT_LENGTH, (byte) 6);
    public static final byte[] ACCOUNT3 = newBlob(ACCOUNT_LENGTH, (byte) 7);
    public static final byte[] ACCOUNT4 = newBlob(ACCOUNT_LENGTH, (byte) 8);
    public static final byte[] ACCOUNT5 = newBlob(ACCOUNT_LENGTH, (byte) 9);
    public static final byte[] ACCOUNT6 = newBlob(ACCOUNT_LENGTH, (byte) 10);
    public static final byte[] ACCOUNT7 = newBlob(ACCOUNT_LENGTH, (byte) 11);
    public static final byte[] ACCOUNT8 = newBlob(ACCOUNT_LENGTH, (byte) 12);
    public static final byte[] DATA1 = newBlob(10, (byte) 13);
    public static final byte[] DATA2 = newBlob(15, (byte) 14);
    public static final byte[] DATA3 = newBlob(300, (byte) 15);
    public static final byte[] SIGNATURE_PAYER = newBlob(SIGNATURE_LENGTH, (byte) 16);
    public static final byte[] SIGNATURE1 = newBlob(SIGNATURE_LENGTH, (byte) 17);
    public static final byte[] SIGNATURE2 = newBlob(SIGNATURE_LENGTH, (byte) 18);
    public static final byte[] SIGNATURE3 = newBlob(SIGNATURE_LENGTH, (byte) 19);
    public static final byte[] SIGNATURE4 = newBlob(SIGNATURE_LENGTH, (byte) 20);
    public static final byte[] SIGNATURE5 = newBlob(SIGNATURE_LENGTH, (byte) 21);
    public static final byte[] SIGNATURE6 = newBlob(SIGNATURE_LENGTH, (byte) 22);
    public static final byte[] SIGNATURE7 = newBlob(SIGNATURE_LENGTH, (byte) 23);
    public static final byte[] SIGNATURE8 = newBlob(SIGNATURE_LENGTH, (byte) 24);
    public static final byte[] LOOKUP_TABLE_ADDRESS1 = newBlob(ACCOUNT_LENGTH, (byte) 25);
    public static final byte[] LOOKUP_TABLE_ADDRESS2 = newBlob(ACCOUNT_LENGTH, (byte) 26);
    public static final byte[] UNSIGNED = newBlob(SIGNATURE_LENGTH, (byte) -77);
    public static final AddressLookupTable ADDRESS_LOOK_TABLE1 = Solana.addressLookupTable(
            Solana.account(LOOKUP_TABLE_ADDRESS1),
            List.of(Solana.account(ACCOUNT8), Solana.account(ACCOUNT3))
    );
    public static final AddressLookupTable ADDRESS_LOOK_TABLE2 = Solana.addressLookupTable(
            Solana.account(LOOKUP_TABLE_ADDRESS2),
            List.of(Solana.account(ACCOUNT1), Solana.account(ACCOUNT4))
    );
    public static final AddressLookupTable ADDRESS_LOOK_TABLE3 = Solana.addressLookupTable(
            Solana.account(LOOKUP_TABLE_ADDRESS2),
            List.of(Solana.account(ACCOUNT7))
    );


    public static final Map<PublicKey, byte[]> SIGNINGS = Map.of(
            Solana.account(PAYER), SIGNATURE_PAYER,
            Solana.account(ACCOUNT1), SIGNATURE1,
            Solana.account(ACCOUNT2), SIGNATURE2,
            Solana.account(ACCOUNT3), SIGNATURE3,
            Solana.account(ACCOUNT4), SIGNATURE4,
            Solana.account(ACCOUNT5), SIGNATURE5,
            Solana.account(ACCOUNT6), SIGNATURE6,
            Solana.account(ACCOUNT7), SIGNATURE7,
            Solana.account(ACCOUNT8), SIGNATURE8
    );

    public static AssertingMessageReader reader(final ByteBuffer buffer)
    {
        return new AssertingMessageReader(buffer);
    }

    public static byte[] newBlob(final int size, final byte value)
    {
        final byte[] bytes = new byte[size];
        Arrays.fill(bytes, value);

        return bytes;
    }

    public static PublicKey generatePublicKey(final byte value)
    {
        final byte[] bytes = new byte[32];
        Arrays.fill(bytes, value);

        return Solana.account(bytes);
    }

    public static Blockhash generateBlockhash(final byte value)
    {
        final byte[] bytes = new byte[32];
        Arrays.fill(bytes, value);

        return Solana.blockhash(bytes);
    }

    public static byte[] generateSignature(final byte value)
    {
        final byte[] bytes = new byte[64];
        Arrays.fill(bytes, value);

        return bytes;
    }

    public static byte[] fillArrayWithSignatures(final byte[] signatureValues)
    {
        final byte[] sigs = new byte[signatureValues.length * SIGNATURE_LENGTH];

        for (int i = 0; i < signatureValues.length; i++)
        {
            Arrays.fill(sigs, SIGNATURE_LENGTH * i, (SIGNATURE_LENGTH) * (i + 1), signatureValues[i]);
        }
        return sigs;
    }

    public static Message writeSimpleUnsignedLegacyMessage(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1))))

              .seal()
              .unsigned()
              .build();
    }

    public static Message writeSimpleUnsignedV0Message(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1))))
              // account3 and account4 should appear in lookup table accounts and be omitted from the accounts section
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .unsigned()
              .build();
    }

    public static Message writeSimpleUnsignedLegacyMessageWithBigData(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .data(DATA3.length, w -> w.put(DATA3))))
              .seal()
              .unsigned()
              .build();
    }

    public static Message writeSimpleUnsignedV0MessageWithBigData(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .data(DATA3.length, w -> w.put(DATA3))))
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .unsigned()
              .build();
    }

    public static Message writeSimpleSignedLegacyMessageWithNoSignatures(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1))))
              .seal()
              .signed()
              .build();
    }

    public static Message writeSimpleSignedV0MessageWithNoSignatures(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1))))
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .signed()
              .build();
    }

    public static Message writeSimpleFullySignedLegacyMessage(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1))))
              .seal()
              .signed()
              .by(Solana.account(PAYER), getByteBufferSignerFor(PAYER))
              .by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
              .by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
              .build();
    }

    public static Message writeSimpleFullySignedV0Message(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1))))
              // account3 and account4 should end up in lookup accounts section
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .signed()
              .by(Solana.account(PAYER), getByteBufferSignerFor(PAYER))
              .by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
              .by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
              .build();
    }

    public static Message writeSimpleFullySignedV0MessageWithoutLookupTables(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
                .v0()
                .payer(Solana.account(PAYER))
                .recent(Solana.blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(Solana.account(PROGRAM1))
                                .account(Solana.account(ACCOUNT4), false, false)
                                .account(Solana.account(ACCOUNT1), true, true)
                                .account(Solana.account(ACCOUNT2), true, false)
                                .account(Solana.account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1))))
                // account3 and account4 should end up in lookup accounts section
                .lookups(List.of())
                .seal()
                .signed()
                .by(Solana.account(PAYER), getByteBufferSignerFor(PAYER))
                .by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
                .by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
                .build();
    }

    public static Message writeComplexUnsignedLegacyMessage(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              // 8, program1, 4, program2
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM2))
                              .account(Solana.account(ACCOUNT5), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT6), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT7), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .account(Solana.account(ACCOUNT8), false, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT3), true, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
              )
              .seal()
              .unsigned()
              .build();
    }

    public static Message writeComplexUnsignedV0Message(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              // 8, program1, 4, program2
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM2))
                              .account(Solana.account(ACCOUNT5), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT6), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT7), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .account(Solana.account(ACCOUNT8), false, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT3), true, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
              )
              // account4 and account8 should appear in lookup table accounts and be omitted from the accounts section
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .unsigned()
              .build();
    }

    public static void writeComplexUnsignedLegacyMessageWithInstructionsAddedInBulk(final ByteBuffer buffer)
    {
        final TransactionInstruction instruction1 = Solana.instruction(ib -> ib
                .program(Solana.account(PROGRAM1))
                .account(Solana.account(ACCOUNT4), false, false)
                .account(Solana.account(ACCOUNT1), true, true)
                .account(Solana.account(ACCOUNT2), true, false)
                .account(Solana.account(ACCOUNT3), false, true)
                .data(DATA1.length, w -> w.put(DATA1)));

        final TransactionInstruction instruction2 = Solana.instruction(ib -> ib
                .program(Solana.account(PROGRAM2))
                .account(Solana.account(ACCOUNT5), false, false)
                .account(Solana.account(ACCOUNT1), true, true)
                .account(Solana.account(ACCOUNT6), true, true)
                .account(Solana.account(ACCOUNT2), true, false)
                .account(Solana.account(ACCOUNT7), true, false)
                .account(Solana.account(ACCOUNT3), false, true)
                .account(Solana.account(ACCOUNT8), false, true)
                .data(DATA2.length, w -> w.put(DATA2)));

        final TransactionInstruction instruction3 = Solana.instruction(ib -> ib
                .program(Solana.account(PROGRAM1))
                .account(Solana.account(ACCOUNT3), true, true)
                .data(DATA1.length, w -> w.put(DATA1)));

        Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .prebuiltInstructions(List.of(instruction1, instruction2, instruction3))
              .seal()
              .unsigned()
              .build();
    }

    public static void writeComplexUnsignedV0MessageWithInstructionsAddedInBulk(final ByteBuffer buffer)
    {
        final TransactionInstruction instruction1 = Solana.instruction(ib -> ib
                .program(Solana.account(PROGRAM1))
                .account(Solana.account(ACCOUNT4), false, false)
                .account(Solana.account(ACCOUNT1), true, true)
                .account(Solana.account(ACCOUNT2), true, false)
                .account(Solana.account(ACCOUNT3), false, true)
                .data(DATA1.length, w -> w.put(DATA1)));

        final TransactionInstruction instruction2 = Solana.instruction(ib -> ib
                .program(Solana.account(PROGRAM2))
                .account(Solana.account(ACCOUNT5), false, false)
                .account(Solana.account(ACCOUNT1), true, true)
                .account(Solana.account(ACCOUNT6), true, true)
                .account(Solana.account(ACCOUNT2), true, false)
                .account(Solana.account(ACCOUNT7), true, false)
                .account(Solana.account(ACCOUNT3), false, true)
                .account(Solana.account(ACCOUNT8), false, true)
                .data(DATA2.length, w -> w.put(DATA2)));

        final TransactionInstruction instruction3 = Solana.instruction(ib -> ib
                .program(Solana.account(PROGRAM1))
                .account(Solana.account(ACCOUNT3), true, true)
                .data(DATA1.length, w -> w.put(DATA1)));

        Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .prebuiltInstructions(List.of(instruction1, instruction2, instruction3))
              // account4 and account8 should appear in lookup table accounts and be omitted from the accounts section
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .unsigned()
              .build();
    }

    public static Message writeComplexSignedLegacyMessageWithNoSignatures(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              // 8, program1, 4, program2
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM2))
                              .account(Solana.account(ACCOUNT5), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT6), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT7), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .account(Solana.account(ACCOUNT8), false, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT3), true, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
              )
              .seal()
              .signed()
              .build();
    }

    public static Message writeComplexSignedV0MessageWithNoSignatures(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              // 8, program1, 4, program2
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM2))
                              .account(Solana.account(ACCOUNT5), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT6), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT7), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .account(Solana.account(ACCOUNT8), false, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT3), true, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
              )
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .signed()
              .build();
    }

    public static Message writeComplexPartiallySignedLegacyMessage(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .legacy()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              // 8, program1, 4, program2
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM2))
                              .account(Solana.account(ACCOUNT5), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT6), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT7), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .account(Solana.account(ACCOUNT8), false, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT3), true, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
              )
              .seal()
              .signed()
              .by(Solana.account(PAYER), getByteBufferSignerFor(PAYER))
              .by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
              .by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
              .by(Solana.account(ACCOUNT7), getByteBufferSignerFor(ACCOUNT7))
              .build();
    }

    public static Message writeComplexPartiallySignedV0Message(final ByteBuffer buffer)
    {
        return Solana.builder(buffer)
              .v0()
              .payer(Solana.account(PAYER))
              .recent(Solana.blockhash(BLOCKHASH))
              .instructions(tb -> tb
                      .append(ib -> ib
                              // 8, program1, 4, program2
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT4), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .data(DATA1.length, w -> w.put(DATA1)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM2))
                              .account(Solana.account(ACCOUNT5), false, false)
                              .account(Solana.account(ACCOUNT1), true, true)
                              .account(Solana.account(ACCOUNT6), true, true)
                              .account(Solana.account(ACCOUNT2), true, false)
                              .account(Solana.account(ACCOUNT7), true, false)
                              .account(Solana.account(ACCOUNT3), false, true)
                              .account(Solana.account(ACCOUNT8), false, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
                      .append(ib -> ib
                              .program(Solana.account(PROGRAM1))
                              .account(Solana.account(ACCOUNT3), true, true)
                              .data(DATA2.length, w -> w.put(DATA2)))
              )
              .lookups(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))
              .seal()
              .signed()
              .by(Solana.account(PAYER), getByteBufferSignerFor(PAYER))
              .by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
              .by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
              .by(Solana.account(ACCOUNT7), getByteBufferSignerFor(ACCOUNT7))
              .build();
    }

    public static void jumpToInstruction(final ByteBuffer buffer, final int index)
    {
        jumpToInstructions(buffer);

        final var countInstructions = readShortVecInt(buffer);
        if (index < 0 || index >= countInstructions)
        {
            Fail.fail("instruction index out of bounds");
        }

        for (int i = 0; i < index; i++)
        {
            skipInstruction(buffer);
        }
    }

    public static AssertingMessageReader fromInstruction(final ByteBuffer buffer, final int index)
    {
        jumpToInstruction(buffer, index);
        return reader(buffer);
    }

    public static void skipInstruction(final ByteBuffer buffer)
    {
        buffer.get(); // skipping the program account index
        final var countAccounts = readShortVecInt(buffer);
        buffer.position(buffer.position() + countAccounts); // skipping the account reference indexes
        final var countDataBytes = readShortVecInt(buffer);
        buffer.position(buffer.position() + countDataBytes); // skipping the instruction data
    }

    public static void jumpToInstructions(final ByteBuffer buffer)
    {
        jumpToBlockhash(buffer);

        buffer.position(buffer.position() + BLOCKHASH_LENGTH);
    }

    public static AssertingMessageReader fromInstructions(final ByteBuffer buffer)
    {
        jumpToInstructions(buffer);
        return reader(buffer);
    }

    public static void jumpToBlockhash(final ByteBuffer buffer)
    {
        fromAccountsTable(buffer);

        final var countAccounts = readShortVecInt(buffer);

        buffer.position(buffer.position() + (countAccounts * ACCOUNT_LENGTH));
    }

    public static AssertingMessageReader fromBlockhash(final ByteBuffer buffer)
    {
        jumpToBlockhash(buffer);
        return reader(buffer);
    }

    public static AssertingMessageReader fromAccountsTable(final ByteBuffer buffer)
    {
        jumpToAccountsTable(buffer);
        return reader(buffer);
    }

    public static void jumpToAccountsTable(final ByteBuffer buffer)
    {
        jumpToTransactionHeader(buffer);

        final var first = buffer.get();
        // legacy header is three bytes, v0 header is four bytes.
        if ((first & 0x80) != 0)
        {
            buffer.position(buffer.position() + (V0_HEADER_LENGTH - 1));
        }
        else
        {
            buffer.position(buffer.position() + (LEGACY_HEADER_LENGTH - 1));
        }
    }

    public static void jumpToAccountLookups(final ByteBuffer buffer)
    {
        jumpToInstructions(buffer);

        final var countInstructions = readShortVecInt(buffer);
        for (int i = 0; i < countInstructions; i++)
        {
            skipInstruction(buffer);
        }
    }

    public static AssertingMessageReader fromAccountLookups(final ByteBuffer buffer)
    {
        jumpToAccountLookups(buffer);

        return new AssertingMessageReader(buffer);
    }

    public static void jumpToAccount(final ByteBuffer buffer, final int index)
    {
        jumpToAccountsTable(buffer);

        final var countAccounts = readShortVecInt(buffer);
        if (index < 0 || index >= countAccounts)
        {
            Fail.fail("account index out of bounds");
        }
        buffer.position(buffer.position() + (index * ACCOUNT_LENGTH));
    }

    public static AssertingMessageReader fromAccount(final ByteBuffer buffer, final int index)
    {
        jumpToAccount(buffer, index);
        return reader(buffer);
    }

    public static void jumpToSignatures(final ByteBuffer buffer)
    {
        buffer.rewind();
    }

    public static AssertingMessageReader fromSignatures(final ByteBuffer buffer)
    {
        jumpToSignatures(buffer);
        return reader(buffer);
    }

    public static void jumpToSignature(final ByteBuffer buffer, final int index)
    {
        jumpToSignatures(buffer);

        final var signatures = buffer.get();

        if (index < 0 || index >= signatures)
        {
            Fail.fail("signature index out of bounds");
        }

        buffer.position(buffer.position() + (index * SIGNATURE_LENGTH));
    }

    public static AssertingMessageReader fromSignature(final ByteBuffer buffer, final int index)
    {
        jumpToSignature(buffer, index);
        return reader(buffer);
    }

    public static void jumpToTransactionHeader(final ByteBuffer buffer)
    {
        buffer.rewind();
        final byte signatures = buffer.get();
        buffer.position(buffer.position() + (signatures * SIGNATURE_LENGTH));
    }

    public static ByteBuffer getTransaction(final ByteBuffer buffer)
    {
        final var view = buffer.duplicate(); // don't move the buffer position

        jumpToTransactionHeader(view);

        return view.slice();
    }

    public static AssertingMessageReader fromTransactionHeader(final ByteBuffer buffer)
    {
        jumpToTransactionHeader(buffer);
        return reader(buffer);
    }

    public static int readShortVecInt(final ByteBuffer buffer)
    {
        int result = 0;
        byte b = buffer.get();
        int bytes = 1;
        while ((b & 0x80) == 0x80)
        {
            result = result | ((b & 0x7f) << (7 * (bytes - 1)));
            b = buffer.get();
            bytes++;
        }
        result = result | (b << (7 * (bytes - 1)));
        return result;
    }

    public static ByteBufferSigner getByteBufferSignerFor(final byte[] account)
    {
        return (transaction, signature) -> signature.put(SIGNINGS.get(Solana.account(account)));
    }

    public static class AssertingMessageReader
    {

        private final ByteBuffer buffer;

        public AssertingMessageReader(final ByteBuffer buffer)
        {
            this.buffer = requireNonNull(buffer);
        }

        public AssertingMessageReader expect(final String field, final byte expected)
        {
            final var actual = buffer.get();
            Assertions.assertThat(actual).as(field).isEqualTo(expected);
            return this;
        }

        public AssertingMessageReader expectShortVecInteger(final String field, final int expected)
        {
            final var actual = readShortVecInt(buffer);
            Assertions.assertThat(actual).as(field).isEqualTo(expected);
            return this;
        }

        public AssertingMessageReader expectOneOf(final String field, final Consumer<byte[]> actualConsumer, final byte[]... bytes)
        {
            if (bytes.length < 1)
            {
                throw new IllegalArgumentException("expected at least two options");
            }

            final int position = buffer.position();
            byte[] actual = new byte[0];

            for (final var option : bytes)
            {
                buffer.position(position);

                actual = new byte[option.length];
                buffer.get(actual);

                if (Arrays.equals(actual, option))
                {
                    actualConsumer.accept(actual);
                    return this;
                }
            }
            final var options = Arrays.stream(bytes).map(Arrays::toString).collect(Collectors.joining(", "));
            Fail.fail(String.format("%s; %s is none of %s", field, Arrays.toString(actual), options));
            return this;
        }

        public AssertingMessageReader expect(final String field, final byte[] bytes)
        {
            final var actual = new byte[bytes.length];
            buffer.get(actual);
            Assertions.assertThat(actual).as(field).containsExactly(bytes);
            return this;
        }

    }

    static byte indexOfAccount(final ByteBuffer buffer, final byte[] account)
    {
        requireNonNull(buffer);
        requireNonNull(account);

        if (account.length != ACCOUNT_LENGTH)
        {
            throw new IllegalArgumentException("invalid account length, expected " + ACCOUNT_LENGTH);
        }

        final var view = buffer.duplicate(); // don't move the buffer position

        jumpToAccountsTable(view);

        final var count = readShortVecInt(view);

        final var entry = new byte[ACCOUNT_LENGTH];
        for (byte i = 0; i < count; i++)
        {
            view.get(entry);

            if (Arrays.equals(account, entry))
            {
                return i;
            }
        }

        throw new NoSuchElementException("account " + Arrays.toString(account));
    }

    static byte indexOfAccount(final ByteBuffer buffer, final byte[] account, final List<AddressLookupTable> addressLookupTables)
    {
        requireNonNull(buffer);
        requireNonNull(account);

        if (account.length != ACCOUNT_LENGTH)
        {
            throw new IllegalArgumentException("invalid account length, expected " + ACCOUNT_LENGTH);
        }

        final var view = buffer.duplicate(); // don't move the buffer position

        jumpToAccountsTable(view);

        final var staticAccountsCount = readShortVecInt(view);

        final var entry = new byte[ACCOUNT_LENGTH];
        for (byte i = 0; i < staticAccountsCount; i++)
        {
            view.get(entry);

            if (Arrays.equals(account, entry))
            {
                return i;
            }
        }

        // if haven't found it in the static accounts table, look for it in the account lookups
        jumpToAccountLookups(view);

        final var numberOfAccountLookups = readShortVecInt(view);
        var lookupTableIndex = -1;
        for (byte i = 0; i < numberOfAccountLookups; i++)
        {
            final var lookupTableAddress = new byte[ACCOUNT_LENGTH];
            view.get(lookupTableAddress);

            final var addressLookupTable = addressLookupTables
                    .stream()
                    .filter(x -> Arrays.equals(x.getLookupTableAddress().bytes(), lookupTableAddress))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Could not find the address lookup table in the supplied address lookup tables."));

            final var numberOfWritableIndexes = readShortVecInt(view);
            for (int j = 0; j < numberOfWritableIndexes; j++)
            {
                lookupTableIndex += 1;
                final var index = readShortVecInt(view);

                if (Arrays.equals(addressLookupTable.getAddresses().get(index).bytes(), account))
                {

                    return (byte) (staticAccountsCount + lookupTableIndex);
                }
            }

            final var numberOfReadableIndexes = readShortVecInt(view);
            for (int j = 0; j < numberOfReadableIndexes; j++)
            {
                lookupTableIndex += 1;
                final var index = readShortVecInt(view);

                if (Arrays.equals(addressLookupTable.getAddresses().get(index).bytes(), account))
                {
                    return (byte) (staticAccountsCount + lookupTableIndex);
                }
            }
        }

        throw new NoSuchElementException("account " + Arrays.toString(account));
    }
}
