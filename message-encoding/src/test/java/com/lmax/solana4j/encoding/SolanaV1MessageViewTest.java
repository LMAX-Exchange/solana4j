package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.NoSuchElementException;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_LENGTH;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedV1Message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolanaV1MessageViewTest
{
    @Test
    void shouldReportVersion1()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.version()).isEqualTo(1);
    }

    @Test
    void shouldWriteCorrectFeePayer()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
    }

    @Test
    void shouldWriteCorrectBlockhash()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));
    }

    @Test
    void shouldCorrectlyReportCounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);
    }

    @Test
    void shouldCorrectlyReportSigners()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.isSigner(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT2))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT3))).isFalse();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isSigner(Solana.account(PROGRAM1))).isFalse();
    }

    @Test
    void shouldCorrectlyReportWriters()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.isWriter(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT2))).isFalse();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT3))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1))).isFalse();
    }

    @Test
    void shouldCorrectlyWriteTransactionBytes()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        final var expectedTransaction = buffer.duplicate();
        expectedTransaction.position(0);
        expectedTransaction.limit(expectedTransaction.limit() - (3 * SIGNATURE_LENGTH));

        assertThat(messageView.transaction()).isEqualTo(expectedTransaction.slice());
    }

    @Test
    void shouldCorrectlyWriteSignatures()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.signature(Solana.account(PAYER)).array()).isEqualTo(SIGNATURE_PAYER);
        assertThat(messageView.signature(Solana.account(ACCOUNT1)).array()).isEqualTo(SIGNATURE1);
        assertThat(messageView.signature(Solana.account(ACCOUNT2)).array()).isEqualTo(SIGNATURE2);

        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT3))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT4))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(PROGRAM1))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldCorrectlyWriteProgramAndProgramIndexWithinAccountsTable()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);
        assertThat(messageView.instructions().get(0).program()).isEqualTo(Solana.account(PROGRAM1));
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(4);
    }

    @Test
    void shouldPreserveOrderOfWritingAndReadingInstructionAccountReferences()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);
        assertThat(messageView.instructions().get(0).accounts())
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(ACCOUNT4),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(ACCOUNT3)
                ));

        assertThat(messageView.instructions().get(0).accountIndexes()).usingRecursiveAssertion().isEqualTo(List.of(5, 1, 2, 3));
    }

    @Test
    void shouldWriteCorrectConfig()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v1()
                .payer(Solana.account(PAYER))
                .recent(Solana.blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(Solana.account(PROGRAM1))
                                .data(DATA1.length, w -> w.put(DATA1))))
                .priorityFee(5_000)
                .computeUnitLimit(250_000)
                .loadedAccountsDataSizeLimit(32 * 1024 * 1024)
                .requestedHeapSize(256 * 1024)
                .seal()
                .signed()
                .by(Solana.account(PAYER), (transaction, signature) -> signature.put(SIGNATURE_PAYER))
                .build();

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.hasPriorityFee()).isTrue();
        assertThat(messageView.priorityFee()).isEqualTo(5_000L);
        assertThat(messageView.hasComputeUnitLimit()).isTrue();
        assertThat(messageView.computeUnitLimit()).isEqualTo(250_000);
        assertThat(messageView.hasLoadedAccountsDataSizeLimit()).isTrue();
        assertThat(messageView.loadedAccountsDataSizeLimit()).isEqualTo(32 * 1024 * 1024);
        assertThat(messageView.hasRequestedHeapSize()).isTrue();
        assertThat(messageView.requestedHeapSize()).isEqualTo(256 * 1024);
    }

    @Test
    void shouldNotReportAbsentOptionalConfigFields()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(buffer);

        assertThat(messageView.hasPriorityFee()).isFalse();
        assertThatThrownBy(() -> messageView.priorityFee()).isInstanceOf(IllegalStateException.class);

        assertThat(messageView.hasRequestedHeapSize()).isFalse();
        assertThatThrownBy(() -> messageView.requestedHeapSize()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldReadMessageSuppliedAtNonZeroBufferPosition()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final var outer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE + 32);
        outer.position(32);
        outer.put(buffer.duplicate());
        outer.limit(outer.position());
        outer.position(32);

        final SolanaV1MessageView messageView = (SolanaV1MessageView) SolanaV1MessageView.fromBuffer(outer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));
        assertThat(messageView.staticAccounts()).containsExactly(
                Solana.account(PAYER),
                Solana.account(ACCOUNT1),
                Solana.account(ACCOUNT2),
                Solana.account(ACCOUNT3),
                Solana.account(PROGRAM1),
                Solana.account(ACCOUNT4));
        assertThat(messageView.signature(Solana.account(PAYER)).array()).isEqualTo(SIGNATURE_PAYER);
        assertThat(messageView.signature(Solana.account(ACCOUNT1)).array()).isEqualTo(SIGNATURE1);
        assertThat(messageView.signature(Solana.account(ACCOUNT2)).array()).isEqualTo(SIGNATURE2);

        final var expectedTransaction = buffer.duplicate();
        expectedTransaction.limit(expectedTransaction.limit() - (3 * SIGNATURE_LENGTH));

        assertThat(messageView.transaction()).isEqualTo(expectedTransaction.slice());
    }

    @Test
    void shouldRejectV1MessageWithTrailingData()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        writeSimpleFullySignedV1Message(buffer);

        final var trailing = ByteBuffer.allocate(buffer.limit() + 7);
        trailing.put(buffer.duplicate());
        trailing.put(new byte[7]);
        trailing.flip();

        assertThatThrownBy(() -> SolanaV1MessageView.fromBuffer(trailing))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("trailing bytes");
    }

    @Test
    void shouldRejectV1MessageWithNoAccounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.put((byte) 0x81);
        buffer.put((byte) 1);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        buffer.putInt(0x0C);
        buffer.put(BLOCKHASH);
        buffer.put((byte) 0);
        buffer.put((byte) 0);
        buffer.putInt(250_000);
        buffer.putInt(32 * 1024 * 1024);
        buffer.put(new byte[SIGNATURE_LENGTH]);
        buffer.flip();

        assertThatThrownBy(() -> SolanaV1MessageView.fromBuffer(buffer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("account addresses");
    }
}
