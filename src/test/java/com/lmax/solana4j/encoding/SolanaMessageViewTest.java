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
import static com.lmax.solana4j.Solana4jTestHelper.ADDRESS_LOOK_TABLE1;
import static com.lmax.solana4j.Solana4jTestHelper.ADDRESS_LOOK_TABLE2;
import static com.lmax.solana4j.Solana4jTestHelper.ADDRESS_LOOK_TABLE3;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.getTransaction;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedV0Message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolanaMessageViewTest
{
    @Test
    void correctFeePayerWrittenLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
    }

    @Test
    void correctFeePayerWrittenV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
    }

    @Test
    void version0HeaderWrittenV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.version()).isEqualTo(0);
    }

    @Test
    void correctBlockhashWrittenLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));
    }

    @Test
    void correctBlockhashWrittenV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));
    }

    @Test
    void correctlyReportCountsLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);
    }

    @Test
    void correctlyReportCountsV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);
    }

    @Test
    void correctlyReportSignersLegacyMessage()
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
    void correctlyReportSignersV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.isSigner(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT2))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT3))).isFalse();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isSigner(Solana.account(PROGRAM1))).isFalse();
    }

    @Test
    void correctlyReportWritersLegacyMessage()
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
    void correctlyReportsWriterForV0MessageForWriterInLookupAccounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .payer(Solana.account(PAYER))
                .recent(Solana.blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(Solana.account(PROGRAM1))
                                .account(Solana.account(ACCOUNT3), false, true)
                                .account(Solana.account(ACCOUNT1), true, true)
                                .data(DATA1.length, w -> w.put(DATA1))))
                .lookups(List.of(ADDRESS_LOOK_TABLE1))
                .seal()
                .signed()
                .build();

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.isWriter(Solana.account(ACCOUNT3), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
    }

    @Test
    void correctlyReportsWriterForV0MessageForWriterNonSignerInStaticAccounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .payer(Solana.account(PAYER))
                .recent(Solana.blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(Solana.account(PROGRAM1))
                                .account(Solana.account(ACCOUNT3), false, true)
                                .account(Solana.account(ACCOUNT1), true, true)
                                .data(DATA1.length, w -> w.put(DATA1))))
                .lookups(List.of(ADDRESS_LOOK_TABLE2))
                .seal()
                .signed()
                .build();

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.isWriter(Solana.account(ACCOUNT3), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
    }

    @Test
    void correctlyReportsWriterForV0MessageForWriterSignerInStaticAccounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .payer(Solana.account(PAYER))
                .recent(Solana.blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(Solana.account(PROGRAM1))
                                .account(Solana.account(ACCOUNT3), true, true)
                                .account(Solana.account(ACCOUNT1), true, true)
                                .data(DATA1.length, w -> w.put(DATA1))))
                .lookups(List.of(ADDRESS_LOOK_TABLE2))
                .seal()
                .signed()
                .build();

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.isWriter(Solana.account(ACCOUNT3), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
    }

    @Test
    void correctlyWriteTransactionBytesLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.transaction()).isEqualTo(getTransaction(buffer));
    }

    @Test
    void correctlyWriteTransactionBytesV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.transaction()).isEqualTo(getTransaction(buffer));
    }

    @Test
    void correctlyWriteSignaturesLegacyMessage()
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
    void correctlyWriteSignaturesV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.signature(Solana.account(PAYER)).array()).isEqualTo(SIGNATURE_PAYER);
        assertThat(messageView.signature(Solana.account(ACCOUNT1)).array()).isEqualTo(SIGNATURE1);
        assertThat(messageView.signature(Solana.account(ACCOUNT2)).array()).isEqualTo(SIGNATURE2);

        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT3))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT4))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(PROGRAM1))).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void correctlyReportAllAccountsAsStaticOrderedBySignersThenWritersV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.staticAccounts())
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(PAYER),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(ACCOUNT3),
                        Solana.account(PROGRAM1),
                        Solana.account(ACCOUNT4)
                ));
    }

    @Test
    void staticAccountsInV0MessageAreSubsetOfAllAccountsWhenAccountsAppearInLookupTables()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.staticAccounts())
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(PAYER),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(PROGRAM1)
                ));

        assertThat(messageView.allAccounts(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2)))
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(PAYER),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(PROGRAM1),
                        Solana.account(ACCOUNT3),
                        Solana.account(ACCOUNT4)
                ));
    }

    @Test
    void correctlyWriteProgramAndProgramIndexWithinAccountsTableLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);
        assertThat(messageView.instructions().get(0).program()).isEqualTo(Solana.account(PROGRAM1));
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(4);
    }

    @Test
    void correctlyWriteProgramAndProgramIndexWithinAccountsTableV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions()).size().isEqualTo(1);
        assertThat(messageView.instructions().get(0).program(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isEqualTo(Solana.account(PROGRAM1));
        // account lives in lookup table so index has changed compared with legacy message
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(3);
    }

    @Test
    void orderOfWritingAndReadingInstructionAccountReferencesPreservedLegacyMessage()
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

    @Test
    void orderOfWritingAndReadingInstructionAccountReferencesPreservedV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);
        assertThat(messageView.instructions().get(0).accounts(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2)))
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(ACCOUNT4),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(ACCOUNT3)
                ));
        assertThat(messageView.instructions().get(0).accountIndexes()).usingRecursiveAssertion().isEqualTo(List.of(5, 1, 2, 4));
    }

    @Test
    void willThrowIllegalArgumentExceptionWhenReadingAccountsOfV0MessageWithIncorrectLookupTablesSupplied()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);

        assertThatThrownBy(() -> messageView.instructions().get(0).accounts(List.of(ADDRESS_LOOK_TABLE3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void willThrowIllegalArgumentExceptionWhenReadingProgramOfV0MessageWithIncorrectLookupTablesSupplied()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);

        assertThatThrownBy(() -> messageView.instructions().get(0).program(List.of(ADDRESS_LOOK_TABLE3)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}