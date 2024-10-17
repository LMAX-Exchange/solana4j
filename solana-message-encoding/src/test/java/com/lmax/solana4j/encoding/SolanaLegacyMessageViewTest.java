package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.NoSuchElementException;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.getTransaction;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedLegacyMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolanaLegacyMessageViewTest
{
    @Test
    void correctFeePayerWritten()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
    }

    @Test
    void correctBlockhashWritten()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));
    }

    @Test
    void correctlyReportCounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);
    }

    @Test
    void correctlyReportSigners()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.isSigner(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT2))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT3))).isFalse();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isSigner(Solana.account(PROGRAM1))).isFalse();
    }

    @Test
    void correctlyReportWriters()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.isWriter(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT2))).isFalse();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT3))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1))).isFalse();
    }

    @Test
    void correctlyWriteTransactionBytes()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.transaction()).isEqualTo(getTransaction(buffer));
    }

    @Test
    void correctlyWriteSignatures()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.signature(Solana.account(PAYER)).array()).isEqualTo(SIGNATURE_PAYER);
        assertThat(messageView.signature(Solana.account(ACCOUNT1)).array()).isEqualTo(SIGNATURE1);
        assertThat(messageView.signature(Solana.account(ACCOUNT2)).array()).isEqualTo(SIGNATURE2);

        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT3))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT4))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(PROGRAM1))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void correctlyWriteProgramAndProgramIndexWithinAccountsTable()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);
        assertThat(messageView.instructions().get(0).program()).isEqualTo(Solana.account(PROGRAM1));
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(4);
    }

    @Test
    void orderOfWritingAndReadingInstructionAccountReferencesPreserved()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

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
}