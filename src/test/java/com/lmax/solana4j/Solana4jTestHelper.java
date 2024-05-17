package com.lmax.solana4j;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.ByteBufferSigner;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.lmax.solana4j.Solana.account;
import static com.lmax.solana4j.Solana.blockhash;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class Solana4jTestHelper
{
    public static final int ACCOUNT_LENGTH = 32;
    public static final int BLOCKHASH_LENGTH = 32;
    public static final int SIGNATURE_LENGTH = 64;
    public static final int LEGACY_HEADER_LENGTH = 3;

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
    public static final byte[] UNSIGNED = newBlob(SIGNATURE_LENGTH, (byte) -77);

    public static final Map<PublicKey, byte[]> SIGNINGS = Map.of(
            account(PAYER), SIGNATURE_PAYER,
            account(ACCOUNT1), SIGNATURE1,
            account(ACCOUNT2), SIGNATURE2,
            account(ACCOUNT3), SIGNATURE3,
            account(ACCOUNT4), SIGNATURE4,
            account(ACCOUNT5), SIGNATURE5,
            account(ACCOUNT6), SIGNATURE6,
            account(ACCOUNT7), SIGNATURE7,
            account(ACCOUNT8), SIGNATURE8
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

        return account(bytes);
    }

    public static Blockhash generateBlockhash(final byte value)
    {
        final byte[] bytes = new byte[32];
        Arrays.fill(bytes, value);

        return blockhash(bytes);
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

    static void writeSimpleUnsignedLegacyMessage(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1))))

                .seal()
                .unsigned()
                .build();
    }

    static void writeSimpleUnsignedLegacyMessageWithBigData(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .data(DATA3.length, w -> w.put(DATA3))))
                .seal()
                .unsigned()
                .build();
    }

    static void writeSimpleSignedLegacyMessageWithNoSignatures(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1))))
                .seal()
                .signed()
                .build();
    }

    static void writeSimpleFullySignedLegacyMessage(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1))))
                .seal()
                .signed()
                        .by(account(PAYER), getByteBufferSignerFor(PAYER))
                        .by(account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
                        .by(account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
                .build();
    }

    static void writeComplexUnsignedLegacyMessage(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                // 8, program1, 4, program2
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1)))
                        .append(ib -> ib
                                .program(account(PROGRAM2))
                                .account(account(ACCOUNT5), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT6), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT7), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .account(account(ACCOUNT8), false, true)
                                .data(DATA2.length, w -> w.put(DATA2)))
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT3), true, true)
                                .data(DATA1.length, w -> w.put(DATA1)))
                )
                .seal()
                .unsigned()
                .build();
    }

    static void writeComplexSignedLegacyMessageWithNoSignatures(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                // 8, program1, 4, program2
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1)))
                        .append(ib -> ib
                                .program(account(PROGRAM2))
                                .account(account(ACCOUNT5), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT6), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT7), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .account(account(ACCOUNT8), false, true)
                                .data(DATA2.length, w -> w.put(DATA2)))
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT3), true, true)
                                .data(DATA2.length, w -> w.put(DATA2)))
                )
                .seal()
                .signed()
                .build();
    }

    static void writeComplexPartiallySignedLegacyMessage(final ByteBuffer buffer)
    {
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                // 8, program1, 4, program2
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .data(DATA1.length, w -> w.put(DATA1)))
                        .append(ib -> ib
                                .program(account(PROGRAM2))
                                .account(account(ACCOUNT5), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .account(account(ACCOUNT6), true, true)
                                .account(account(ACCOUNT2), true, false)
                                .account(account(ACCOUNT7), true, false)
                                .account(account(ACCOUNT3), false, true)
                                .account(account(ACCOUNT8), false, true)
                                .data(DATA2.length, w -> w.put(DATA2)))
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT3), true, true)
                                .data(DATA2.length, w -> w.put(DATA2)))
                )
                .seal()
                .signed()
                        .by(account(PAYER), getByteBufferSignerFor(PAYER))
                        .by(account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1))
                        .by(account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2))
                        .by(account(ACCOUNT7), getByteBufferSignerFor(ACCOUNT7))
                .build();
    }

    public static void jumpToInstruction(final ByteBuffer buffer, final int index)
    {
        jumpToInstructions(buffer);

        final var countInstructions = readShortVecInt(buffer);
        if (index < 0 || index >= countInstructions)
        {
            fail("instruction index out of bounds");
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
            throw new UnsupportedOperationException("Solana v0 message format");
        }
        buffer.position(buffer.position() + (LEGACY_HEADER_LENGTH - 1));
    }

    public static void jumpToAccount(final ByteBuffer buffer, final int index)
    {
        jumpToAccountsTable(buffer);

        final var countAccounts = readShortVecInt(buffer);
        if (index < 0 || index >= countAccounts)
        {
            fail("account index out of bounds");
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
            fail("signature index out of bounds");
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
            result = result | ((b & 0x7f) << (7*(bytes-1)));
            b = buffer.get();
            bytes++;
        }
        result = result | (b << (7*(bytes-1)));
        return result;
    }

    private static ByteBufferSigner getByteBufferSignerFor(final byte[] account)
    {
        return (transaction, signature) -> signature.put(SIGNINGS.get(account(account)));
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
            assertThat(actual).as(field).isEqualTo(expected);
            return this;
        }

        public AssertingMessageReader expectShortVecInteger(final String field, final int expected)
        {
            final var actual = readShortVecInt(buffer);
            assertThat(actual).as(field).isEqualTo(expected);
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
            fail(String.format("%s; %s is none of %s", field, Arrays.toString(actual), options));
            return this;
        }

        public AssertingMessageReader expect(final String field, final byte[] bytes)
        {
            final var actual = new byte[bytes.length];
            buffer.get(actual);
            assertThat(actual).as(field).containsExactly(bytes);
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
}
