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
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT5;
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

class SolanaV0MessageViewTest
{
    @Test
    void correctFeePayerWritten()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
    }

    @Test
    void version0HeaderWritten()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.version()).isEqualTo(0);
    }

    @Test
    void correctBlockhashWritten()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));
    }

    @Test
    void correctlyReportCounts()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);
    }

    @Test
    void correctlyReportSigners()
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
    void correctlyReportWriterForWriterInLookupAccounts()
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
        assertThat(messageView.isWriter(Solana.account(ACCOUNT1), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1), List.of(ADDRESS_LOOK_TABLE1))).isFalse();
    }

    @Test
    void correctlyReportWriterForWriterNonSignerInStaticAccounts()
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
                              .account(Solana.account(ACCOUNT5), false, false)
                              .data(DATA1.length, w -> w.put(DATA1))))
              .lookups(List.of(ADDRESS_LOOK_TABLE2))
              .seal()
              .signed()
              .build();

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.isWriter(Solana.account(ACCOUNT1), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT3), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT5), List.of(ADDRESS_LOOK_TABLE1))).isFalse();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1), List.of(ADDRESS_LOOK_TABLE1))).isFalse();
    }

    @Test
    void correctlyReportWriterForWriterSignerInStaticAccounts()
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
        assertThat(messageView.isWriter(Solana.account(ACCOUNT1), List.of(ADDRESS_LOOK_TABLE1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1), List.of(ADDRESS_LOOK_TABLE1))).isFalse();
    }

    @Test
    void correctlyWriteTransactionBytes()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.transaction()).isEqualTo(getTransaction(buffer));
    }

    @Test
    void correctlyWriteSignatures()
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
    void correctlyReportAllAccountsAsStaticOrderedBySignersThenWriters()
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
    void staticAccountsInAreSubsetOfAllAccountsWhenAccountsAppearInLookupTables()
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
    void correctlyWriteProgramAndProgramIndexWithinAccountsTable()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions()).size().isEqualTo(1);
        assertThat(messageView.instructions().get(0)
                              .program(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isEqualTo(Solana.account(PROGRAM1));
        // account lives in lookup table so index has changed compared with legacy message
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(3);
    }

    @Test
    void orderOfWritingAndReadingInstructionAccountReferencesPreserved()
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
        assertThat(messageView.instructions().get(0).accountIndexes()).usingRecursiveAssertion()
                                                                      .isEqualTo(List.of(5, 1, 2, 4));
    }

    @Test
    void willThrowIllegalArgumentExceptionWhenReadingAccountsWithIncorrectLookupTablesSupplied()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);

        assertThatThrownBy(() -> messageView.instructions().get(0).accounts(List.of(ADDRESS_LOOK_TABLE3)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void willThrowIllegalArgumentExceptionWhenReadingProgramWithIncorrectLookupTablesSupplied()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.instructions().size()).isEqualTo(1);

        assertThatThrownBy(() -> messageView.instructions().get(0).program(List.of(ADDRESS_LOOK_TABLE3)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}