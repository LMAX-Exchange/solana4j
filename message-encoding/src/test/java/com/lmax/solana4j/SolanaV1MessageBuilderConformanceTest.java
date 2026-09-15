package com.lmax.solana4j;

import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import static com.lmax.solana4j.Solana.account;
import static com.lmax.solana4j.Solana.blockhash;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT5;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LENGTH;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH_LENGTH;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.DATA2;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_LENGTH;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.UNSIGNED;
import static com.lmax.solana4j.Solana4jTestHelper.generatePublicKey;
import static com.lmax.solana4j.Solana4jTestHelper.reader;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedV1Message;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleUnsignedV1Message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolanaV1MessageBuilderConformanceTest
{

    @Test
    void shouldRequireByteBuffer()
    {
        assertThatThrownBy(() ->
                Solana.builder(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectTooSmallBuffer()
    {
        assertThatThrownBy(() ->
                writeSimpleUnsignedV1Message(ByteBuffer.allocate(20)))
            .isInstanceOf(BufferOverflowException.class);
    }

    @Test
    void shouldRejectWhenPayerMissing()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .computeUnitLimit(1_400_000)
                    .loadedAccountsDataSizeLimit(64 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectWhenComputeUnitLimitMissing()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .loadedAccountsDataSizeLimit(64 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectWhenLoadedAccountsDataSizeLimitMissing()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .computeUnitLimit(1_400_000)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldWriteToBuffer()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleUnsignedV1Message(buffer);

        assertThat(buffer.position()).isEqualTo(0);
        assertThat(buffer.limit()).isLessThan(Solana.MAX_V1_MESSAGE_SIZE);
    }

    @Test
    void shouldWriteSimpleUnsignedMessageHeader()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleUnsignedV1Message(buffer);

        reader(buffer)
                .expect("version prefix", (byte) 0x81)
                .expect("count signed accounts", (byte) 3)
                .expect("count signed read-only accounts", (byte) 1)
                .expect("count unsigned read-only accounts", (byte) 2);

        final var configMask = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        configMask.position(4);
        assertThat(configMask.getInt()).isEqualTo(0x0C);
    }

    @Test
    void shouldWriteSimpleUnsignedMessageCounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleUnsignedV1Message(buffer);

        buffer.position(40);

        reader(buffer)
                .expect("count instructions", (byte) 1)
                .expect("count addresses", (byte) 6);
    }

    @Test
    void shouldWriteSimpleUnsignedMessageAccountsTable()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleUnsignedV1Message(buffer);

        buffer.position(42);

        reader(buffer)
                .expect("payer account", PAYER)
                .expect("second account", ACCOUNT1)
                .expect("third account", ACCOUNT2)
                .expect("fourth account", ACCOUNT3)
                .expect("fifth account", PROGRAM1)
                .expect("sixth account", ACCOUNT4);
    }

    @Test
    void shouldWriteSimpleUnsignedMessageBlockhash()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleUnsignedV1Message(buffer);

        buffer.position(8);

        reader(buffer).expect("block hash", BLOCKHASH);
    }

    @Test
    void shouldWriteConfigValuesInBitOrder()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleUnsignedV1Message(buffer);

        final var view = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        view.position(42 + (6 * ACCOUNT_LENGTH));

        assertThat(view.getInt()).isEqualTo(1_400_000);
        assertThat(view.getInt()).isEqualTo(64 * 1024 * 1024);
    }

    @Test
    void shouldSplitInstructionHeadersAndPayloads()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v1()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(account(ACCOUNT4), false, false)
                                .account(account(ACCOUNT1), true, true)
                                .data(DATA1.length, w -> w.put(DATA1)))
                        .append(ib -> ib
                                .program(account(PROGRAM2))
                                .account(account(ACCOUNT5), false, true)
                                .data(DATA2.length, w -> w.put(DATA2))))
                .priorityFee(1_000)
                .computeUnitLimit(250_000)
                .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                .requestedHeapSize(256 * 1024)
                .seal()
                .unsigned()
                .build();

        final var view = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN);

        assertThat(view.get()).isEqualTo((byte) 0x81);
        assertThat(view.get()).isEqualTo((byte) 2);
        assertThat(view.get()).isEqualTo((byte) 0);
        assertThat(view.get()).isEqualTo((byte) 3);
        assertThat(view.getInt()).isEqualTo(0x1F);

        final var blockhash = new byte[BLOCKHASH_LENGTH];
        view.get(blockhash);
        assertThat(blockhash).containsExactly(BLOCKHASH);

        assertThat(view.get()).isEqualTo((byte) 2);
        assertThat(view.get()).isEqualTo((byte) 6);

        final var address = new byte[ACCOUNT_LENGTH];
        view.get(address);
        assertThat(address).containsExactly(PAYER);
        view.get(address);
        assertThat(address).containsExactly(ACCOUNT1);
        view.get(address);
        assertThat(address).containsExactly(ACCOUNT5);
        view.get(address);
        assertThat(address).containsExactly(PROGRAM1);
        view.get(address);
        assertThat(address).containsExactly(ACCOUNT4);
        view.get(address);
        assertThat(address).containsExactly(PROGRAM2);

        assertThat(view.getLong()).isEqualTo(1_000L);
        assertThat(view.getInt()).isEqualTo(250_000);
        assertThat(view.getInt()).isEqualTo(32 * 1024 * 1024);
        assertThat(view.getInt()).isEqualTo(256 * 1024);

        assertThat(view.get()).isEqualTo((byte) 3);
        assertThat(view.get()).isEqualTo((byte) 2);
        assertThat(view.getShort()).isEqualTo((short) DATA1.length);
        assertThat(view.get()).isEqualTo((byte) 5);
        assertThat(view.get()).isEqualTo((byte) 1);
        assertThat(view.getShort()).isEqualTo((short) DATA2.length);

        assertThat(view.get()).isEqualTo((byte) 4);
        assertThat(view.get()).isEqualTo((byte) 1);
        final var firstData = new byte[DATA1.length];
        view.get(firstData);
        assertThat(firstData).containsExactly(DATA1);

        assertThat(view.get()).isEqualTo((byte) 2);
        final var secondData = new byte[DATA2.length];
        view.get(secondData);
        assertThat(secondData).containsExactly(DATA2);

        assertThat(view.remaining()).isEqualTo(2 * SIGNATURE_LENGTH);
    }

    @Test
    void shouldLeaveSimpleUnsignedMessageSignaturesUntouched()
    {
        final var bytes = new byte[Solana.MAX_V1_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeSimpleUnsignedV1Message(buffer);

        final var signatures = new byte[3 * SIGNATURE_LENGTH];
        buffer.position(buffer.limit() - (3 * SIGNATURE_LENGTH));
        buffer.get(signatures);

        final var expected = new byte[3 * SIGNATURE_LENGTH];
        Arrays.fill(expected, (byte) -77);

        assertThat(signatures).containsExactly(expected);
    }

    @Test
    void shouldLeaveSignaturesUntouchedWhenNoSignersSign()
    {
        final var bytes = new byte[Solana.MAX_V1_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        Solana.builder(buffer)
                .v1()
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
                .computeUnitLimit(1_400_000)
                .loadedAccountsDataSizeLimit(64 * 1024 * 1024)
                .seal()
                .signed()
                .build();

        final var signatures = new byte[3 * SIGNATURE_LENGTH];
        buffer.position(buffer.limit() - (3 * SIGNATURE_LENGTH));
        buffer.get(signatures);

        final var expected = new byte[3 * SIGNATURE_LENGTH];
        Arrays.fill(expected, (byte) -77);

        assertThat(signatures).containsExactly(expected);
    }

    @Test
    void shouldUpdateAllSignaturesWhenSimpleFullySigned()
    {
        final var bytes = new byte[Solana.MAX_V1_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeSimpleFullySignedV1Message(buffer);

        final var accountsTable = buffer.duplicate();
        accountsTable.position(42);

        final var signatures = buffer.duplicate();
        signatures.position(buffer.limit() - (3 * SIGNATURE_LENGTH));

        final var expectedSigners = List.of(PAYER, ACCOUNT1, ACCOUNT2);
        final var expectedSignatures = List.of(SIGNATURE_PAYER, SIGNATURE1, SIGNATURE2);

        for (int i = 0; i < 3; i++)
        {
            final var signer = new byte[ACCOUNT_LENGTH];
            accountsTable.get(signer);

            final var actualSignature = new byte[SIGNATURE_LENGTH];
            signatures.get(actualSignature);

            assertThat(signer).containsExactly(expectedSigners.get(i));
            assertThat(actualSignature).containsExactly(expectedSignatures.get(i));
        }
    }

    @Test
    void shouldAddFirstSignatureToPartiallySignedMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedV1Message(buffer);

        Solana.forSigning(buffer)
                    .by(account(PAYER), (transaction, signature) -> signature.put(SIGNATURE_PAYER))
                    .build();

        buffer.position(buffer.limit() - (3 * SIGNATURE_LENGTH));

        reader(buffer)
                .expect("first signature (payer)", SIGNATURE_PAYER)
                .expect("second signature", UNSIGNED)
                .expect("third signature", UNSIGNED);
    }

    @Test
    void shouldAddMidSignatureToPartiallySignedMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedV1Message(buffer);

        Solana.forSigning(buffer)
                .by(account(ACCOUNT1), (transaction, signature) -> signature.put(SIGNATURE1))
                .build();

        buffer.position(buffer.limit() - (3 * SIGNATURE_LENGTH));

        reader(buffer)
                .expect("first signature (payer)", UNSIGNED)
                .expect("second signature", SIGNATURE1)
                .expect("third signature", UNSIGNED);
    }

    @Test
    void shouldAddEndSignatureToPartiallySignedMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedV1Message(buffer);

        Solana.forSigning(buffer)
                .by(account(ACCOUNT2), (transaction, signature) -> signature.put(SIGNATURE2))
                .build();

        buffer.position(buffer.limit() - (3 * SIGNATURE_LENGTH));

        reader(buffer)
                .expect("first signature (payer)", UNSIGNED)
                .expect("second signature", UNSIGNED)
                .expect("third signature", SIGNATURE2);
    }

    @Test
    void shouldRejectTooManyInstructions()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final byte[] none = new byte[0];

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .instructions(tb ->
                    {
                        for (int i = 0; i < 65; i++)
                        {
                            tb.append(ib -> ib
                                    .program(account(PROGRAM1))
                                    .data(none.length, w -> w.put(none)));
                        }
                    })
                    .computeUnitLimit(250_000)
                    .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectTooManyAccounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final byte[] none = new byte[0];

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .instructions(tb ->
                    {
                        for (int i = 0; i < 63; i++)
                        {
                            final var account = generatePublicKey((byte) (30 + i));
                            tb.append(ib -> ib
                                    .program(account(PROGRAM1))
                                    .account(account, false, false)
                                    .data(none.length, w -> w.put(none)));
                        }
                    })
                    .computeUnitLimit(250_000)
                    .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldAcceptBoundaryCounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final byte[] none = new byte[0];

        Solana.builder(buffer)
                .v1()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb ->
                {
                    for (int i = 0; i < 62; i++)
                    {
                        final var boundaryAccount = generatePublicKey((byte) (30 + i));
                        tb.append(ib -> ib
                                .program(account(PROGRAM1))
                                .account(boundaryAccount, false, false)
                                .data(none.length, w -> w.put(none)));
                    }
                    tb.append(ib -> ib
                            .program(account(PROGRAM1))
                            .account(account(PAYER), true, true)
                            .data(none.length, w -> w.put(none)));
                    tb.append(ib -> ib
                            .program(account(PROGRAM1))
                            .data(none.length, w -> w.put(none)));
                })
                .computeUnitLimit(250_000)
                .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                .seal()
                .unsigned()
                .build();

        buffer.position(40);

        reader(buffer)
                .expect("count instructions", (byte) 64)
                .expect("count addresses", (byte) 64);
    }

    @Test
    void shouldRejectInvalidComputeUnitLimitValues()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer).v1().computeUnitLimit(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("compute unit limit must be positive");
        assertThatThrownBy(() -> Solana.builder(buffer).v1().computeUnitLimit(-1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("compute unit limit must be positive");
    }

    @Test
    void shouldRejectInvalidRequestedHeapSizeValues()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer).v1().requestedHeapSize(32 * 1024 + 1))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> Solana.builder(buffer).v1().requestedHeapSize(31 * 1024))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> Solana.builder(buffer).v1().requestedHeapSize(257 * 1024))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldAcceptBoundaryRequestedHeapSizes()
    {
        final var minimumBuffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);
        Solana.builder(minimumBuffer)
                .v1()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .data(DATA1.length, w -> w.put(DATA1))))
                .computeUnitLimit(250_000)
                .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                .requestedHeapSize(32 * 1024)
                .seal()
                .unsigned()
                .build();

        assertThat(minimumBuffer.limit()).isLessThanOrEqualTo(Solana.MAX_V1_MESSAGE_SIZE);

        final var maximumBuffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);
        Solana.builder(maximumBuffer)
                .v1()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .data(DATA1.length, w -> w.put(DATA1))))
                .computeUnitLimit(250_000)
                .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                .requestedHeapSize(256 * 1024)
                .seal()
                .unsigned()
                .build();

        assertThat(maximumBuffer.limit()).isLessThanOrEqualTo(Solana.MAX_V1_MESSAGE_SIZE);
    }

    @Test
    void shouldRejectTooManySignatures()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final byte[] none = new byte[0];

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .instructions(tb -> tb
                            .append(ib ->
                            {
                                for (int i = 0; i < 12; i++)
                                {
                                    ib.account(generatePublicKey((byte) (40 + i)), true, true);
                                }
                                ib.program(account(PROGRAM1))
                                        .data(none.length, w -> w.put(none));
                            }))
                    .computeUnitLimit(250_000)
                    .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldAcceptMaximumSignatures()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final byte[] none = new byte[0];

        Solana.builder(buffer)
                .v1()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib ->
                        {
                            for (int i = 0; i < 11; i++)
                            {
                                ib.account(generatePublicKey((byte) (40 + i)), true, true);
                            }
                            ib.program(account(PROGRAM1))
                                    .data(none.length, w -> w.put(none));
                        }))
                .computeUnitLimit(250_000)
                .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                .seal()
                .unsigned()
                .build();

        buffer.position(40);

        reader(buffer)
                .expect("count instructions", (byte) 1)
                .expect("count addresses", (byte) 13);
    }

    @Test
    void shouldRejectTooManyAccountsPerInstruction()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final byte[] none = new byte[0];

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .instructions(tb -> tb
                            .append(ib ->
                            {
                                for (int i = 0; i < 256; i++)
                                {
                                    ib.account(account(ACCOUNT1), false, false);
                                }
                                ib.program(account(PROGRAM1))
                                        .data(none.length, w -> w.put(none));
                            }))
                    .computeUnitLimit(250_000)
                    .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldRejectMessageExceedingMaxV1Size()
    {
        final var buffer = ByteBuffer.allocate(2 * Solana.MAX_V1_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .v1()
                    .payer(account(PAYER))
                    .recent(blockhash(BLOCKHASH))
                    .instructions(tb -> tb
                            .append(ib -> ib
                                    .program(account(PROGRAM1))
                                    .data(4_500, w -> w.put(new byte[4_500]))))
                    .computeUnitLimit(250_000)
                    .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }
}
