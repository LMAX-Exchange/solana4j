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
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
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
    void messageViewOfLegacyTransactionConsistent()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        final SolanaLegacyMessageView messageView = (SolanaLegacyMessageView) SolanaLegacyMessageView.fromBuffer(buffer);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);

        assertThat(messageView.isSigner(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT2))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT3))).isFalse();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isSigner(Solana.account(PROGRAM1))).isFalse();

        assertThat(messageView.signature(Solana.account(PAYER)).array()).isEqualTo(SIGNATURE_PAYER);
        assertThat(messageView.signature(Solana.account(ACCOUNT1)).array()).isEqualTo(SIGNATURE1);
        assertThat(messageView.signature(Solana.account(ACCOUNT2)).array()).isEqualTo(SIGNATURE2);

        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT3))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT4))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(PROGRAM1))).isInstanceOf(NoSuchElementException.class);

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

        assertThat(messageView.instructions().size()).isEqualTo(1);
        assertThat(messageView.instructions().get(0).program()).isEqualTo(Solana.account(PROGRAM1));
        assertThat(messageView.instructions().get(0).accounts())
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(ACCOUNT4),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(ACCOUNT3)
                ));
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(4);
        assertThat(messageView.instructions().get(0).accountIndexes()).usingRecursiveAssertion().isEqualTo(
                List.of(5, 1, 2, 3)
        );

        assertThat(messageView.isWriter(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT2))).isFalse();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT3))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1))).isFalse();

        assertThat(messageView.transaction()).isEqualTo(getTransaction(buffer));
    }

    // TODO: add test for a static writer account isWriter check
    @Test
    void messageViewOfV0TransactionConsistent()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedV0Message(buffer);

        final SolanaV0MessageView messageView = (SolanaV0MessageView) SolanaV0MessageView.fromBuffer(buffer);

        assertThat(messageView.version()).isEqualTo(0);

        assertThat(messageView.feePayer()).isEqualTo(Solana.account(PAYER));
        assertThat(messageView.recentBlockHash()).isEqualTo(Solana.blockhash(BLOCKHASH));

        assertThat(messageView.countAccountsSigned()).isEqualTo(3);
        assertThat(messageView.countAccountsSignedReadOnly()).isEqualTo(1);
        assertThat(messageView.countAccountsUnsignedReadOnly()).isEqualTo(2);

        assertThat(messageView.isSigner(Solana.account(PAYER))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT1))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT2))).isTrue();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT3))).isFalse();
        assertThat(messageView.isSigner(Solana.account(ACCOUNT4))).isFalse();
        assertThat(messageView.isSigner(Solana.account(PROGRAM1))).isFalse();

        assertThat(messageView.signature(Solana.account(PAYER)).array()).isEqualTo(SIGNATURE_PAYER);
        assertThat(messageView.signature(Solana.account(ACCOUNT1)).array()).isEqualTo(SIGNATURE1);
        assertThat(messageView.signature(Solana.account(ACCOUNT2)).array()).isEqualTo(SIGNATURE2);

        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT3))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(ACCOUNT4))).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> messageView.signature(Solana.account(PROGRAM1))).isInstanceOf(NoSuchElementException.class);

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

        assertThat(messageView.instructions()).size().isEqualTo(1);
        assertThat(messageView.instructions().get(0).program(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isEqualTo(Solana.account(PROGRAM1));
        assertThat(messageView.instructions().get(0).accounts(List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2)))
                .usingRecursiveAssertion()
                .isEqualTo(List.of(
                        Solana.account(ACCOUNT4),
                        Solana.account(ACCOUNT1),
                        Solana.account(ACCOUNT2),
                        Solana.account(ACCOUNT3)
                ));
        assertThat(messageView.instructions().get(0).programIndex()).isEqualTo(3);
        assertThat(messageView.instructions().get(0).accountIndexes())
                .usingRecursiveAssertion()
                .isEqualTo(
                        List.of(5, 1, 2, 4)
                );

        assertThat(messageView.isWriter(Solana.account(PAYER), List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT1), List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT2), List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isFalse();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT3), List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isTrue();
        assertThat(messageView.isWriter(Solana.account(ACCOUNT4), List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isFalse();
        assertThat(messageView.isWriter(Solana.account(PROGRAM1), List.of(ADDRESS_LOOK_TABLE1, ADDRESS_LOOK_TABLE2))).isFalse();

        assertThat(messageView.transaction()).isEqualTo(getTransaction(buffer));
    }
}