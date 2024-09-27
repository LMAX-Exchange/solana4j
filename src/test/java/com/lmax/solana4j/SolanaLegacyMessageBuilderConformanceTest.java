package com.lmax.solana4j;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.lmax.solana4j.Solana.account;
import static com.lmax.solana4j.Solana.blockhash;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT5;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT6;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT7;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT8;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LENGTH;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.DATA3;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE5;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_LENGTH;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE_PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNINGS;
import static com.lmax.solana4j.Solana4jTestHelper.UNSIGNED;
import static com.lmax.solana4j.Solana4jTestHelper.fromAccountsTable;
import static com.lmax.solana4j.Solana4jTestHelper.fromBlockhash;
import static com.lmax.solana4j.Solana4jTestHelper.fromInstruction;
import static com.lmax.solana4j.Solana4jTestHelper.fromInstructions;
import static com.lmax.solana4j.Solana4jTestHelper.fromSignatures;
import static com.lmax.solana4j.Solana4jTestHelper.fromTransactionHeader;
import static com.lmax.solana4j.Solana4jTestHelper.indexOfAccount;
import static com.lmax.solana4j.Solana4jTestHelper.jumpToAccount;
import static com.lmax.solana4j.Solana4jTestHelper.jumpToInstruction;
import static com.lmax.solana4j.Solana4jTestHelper.jumpToSignature;
import static com.lmax.solana4j.Solana4jTestHelper.jumpToTransactionHeader;
import static com.lmax.solana4j.Solana4jTestHelper.reader;
import static com.lmax.solana4j.Solana4jTestHelper.writeComplexPartiallySignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeComplexSignedLegacyMessageWithNoSignatures;
import static com.lmax.solana4j.Solana4jTestHelper.writeComplexUnsignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeComplexUnsignedLegacyMessageWithInstructionsAddedInBulk;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleSignedLegacyMessageWithNoSignatures;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleUnsignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleUnsignedLegacyMessageWithBigData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolanaLegacyMessageBuilderConformanceTest
{

    @Test
    void requiresByteBuffer()
    {
        assertThatThrownBy(() ->
                Solana.builder(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsTooSmallBuffer()
    {
        assertThatThrownBy(() ->
                writeSimpleUnsignedLegacyMessage(ByteBuffer.allocate(20)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsWhenPayerMissing()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        assertThatThrownBy(() -> Solana.builder(buffer)
                    .legacy()
                    .seal()
                    .unsigned()
                    .build())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void writesToBuffer()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        assertThat(buffer.position()).isEqualTo(0);
        assertThat(buffer.limit()).isLessThan(Solana.MAX_MESSAGE_SIZE);
    }

    @Test
    void checkSimpleUnsignedMessageHasThreeSigners()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        reader(buffer).expect("number of signers", (byte) 3);
    }

    @Test
    void checkComplexUnsignedMessageHasSixSigners()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        reader(buffer).expect("number of signers", (byte) 6);
    }

    @Test
    void checkComplexUnsignedMessageHasSixSignersWithInstructionsAddedInBulk()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessageWithInstructionsAddedInBulk(buffer);

        reader(buffer).expect("number of signers", (byte) 6);
    }

    @Test
    void checkSimpleUnsignedMessageSignaturesAreUntouched()
    {
        final var bytes = new byte[Solana.MAX_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeSimpleUnsignedLegacyMessage(buffer);

        final var signatures = new byte[3 * SIGNATURE_LENGTH];
        buffer.position(1).get(signatures);

        final var expected = new byte[3 * SIGNATURE_LENGTH];
        Arrays.fill(expected, (byte) -77);

        assertThat(signatures).containsExactly(expected);
    }

    @Test
    void checkSimpleSignedMessageWithNoSignersThatSignaturesAreUntouched()
    {
        final var bytes = new byte[Solana.MAX_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeSimpleSignedLegacyMessageWithNoSignatures(buffer);

        final var signatures = new byte[3 * SIGNATURE_LENGTH];
        buffer.position(1).get(signatures);

        final var expected = new byte[3 * SIGNATURE_LENGTH];
        Arrays.fill(expected, (byte) -77);

        assertThat(signatures).containsExactly(expected);
    }

    @Test
    void checkSimpleFullySignedMessageSignaturesAreAllUpdated()
    {
        final var bytes = new byte[Solana.MAX_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeSimpleFullySignedLegacyMessage(buffer);

        final var signatures = new byte[3 * SIGNATURE_LENGTH];
        buffer.position(1).get(signatures);

        jumpToTransactionHeader(buffer);

        final var signersCount = buffer.get();

        for (int i = 0; i < signersCount; i++)
        {
            jumpToAccount(buffer, i);
            final var account = new byte[ACCOUNT_LENGTH];
            buffer.get(account);

            jumpToSignature(buffer, i);
            final var actualSignature = new byte[SIGNATURE_LENGTH];
            buffer.get(actualSignature);

            final var expectedSignature = SIGNINGS.getOrDefault(account(account), UNSIGNED);

            assertThat(actualSignature).isEqualTo(expectedSignature);
        }
    }

    @Test
    void checkComplexUnsignedMessageSignaturesAreUntouched()
    {
        final var bytes = new byte[Solana.MAX_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeComplexUnsignedLegacyMessage(buffer);

        final var signatures = new byte[6 * SIGNATURE_LENGTH];
        buffer.position(1).get(signatures);

        final var expected = new byte[6 * SIGNATURE_LENGTH];
        Arrays.fill(expected, (byte) -77);

        assertThat(signatures).containsExactly(expected);
    }

    @Test
    void checkComplexSignedMessageWithNoSignersThatSignaturesAreUntouched()
    {
        final var bytes = new byte[Solana.MAX_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeComplexSignedLegacyMessageWithNoSignatures(buffer);

        final var signatures = new byte[6 * SIGNATURE_LENGTH];
        buffer.position(1).get(signatures);

        final var expected = new byte[6 * SIGNATURE_LENGTH];
        Arrays.fill(expected, (byte) -77);

        assertThat(signatures).containsExactly(expected);
    }

    @Test
    void checkComplexPartiallySignedMessageSignaturesArePartiallyUpdated()
    {
        final var bytes = new byte[Solana.MAX_MESSAGE_SIZE];
        Arrays.fill(bytes, (byte) -77);
        final var buffer = ByteBuffer.wrap(bytes);

        writeComplexPartiallySignedLegacyMessage(buffer);

        final var signatures = new byte[6 * SIGNATURE_LENGTH];
        buffer.position(1).get(signatures);

        jumpToTransactionHeader(buffer);

        final var signersCount = buffer.get();

        final var expectedSigners = Set.of(account(PAYER), account(ACCOUNT1), account(ACCOUNT2), account(ACCOUNT7));

        for (int i = 0; i < signersCount; i++)
        {
            jumpToAccount(buffer, i);
            final var account = new byte[ACCOUNT_LENGTH];
            buffer.get(account);

            jumpToSignature(buffer, i);
            final var actualSignature = new byte[SIGNATURE_LENGTH];
            buffer.get(actualSignature);

            final var expectedSignature = SIGNINGS.get(account(account));

            assertThat(expectedSignature).as("unknown account").isNotNull();

            assertThat(actualSignature)
                    .isEqualTo(expectedSigners.contains(account(account))
                               ? expectedSignature
                               : UNSIGNED);
        }
    }

    @Test
    void checkSimpleUnsignedMessageIsLegacyFormat()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        jumpToTransactionHeader(buffer);

        // first byte of header
        final var first = buffer.get();

        // high bit must be clear for legacy transactions
        assertThat(first & 0x80).isZero();
    }

    @Test
    void checkComplexUnsignedMessageIsLegacyFormat()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        jumpToTransactionHeader(buffer);

        // first byte of header
        final var first = buffer.get();

        // high bit must be clear for legacy transactions
        assertThat(first & 0x80).isZero();
    }

    @Test
    void checkSimpleUnsignedMessageHeader()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        fromTransactionHeader(buffer)
                .expect("count signed accounts", (byte) 3)
                .expect("count signed read-only accounts", (byte) 1)
                .expect("count unsigned read-only accounts", (byte) 2);
    }

    @Test
    void checkComplexUnsignedMessageHeader()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        fromTransactionHeader(buffer)
                .expect("count signed accounts", (byte) 6)
                .expect("count signed read-only accounts", (byte) 2)
                .expect("count unsigned read-only accounts", (byte) 4);
    }

    @Test
    void checkSimpleUnsignedMessageAccountsTable()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        // (There does not appear to be a defined sequence for accounts within
        //  the same subsection of the accounts table.
        //  Some TypeScript client code sorts the account keys Base58 encoded
        //  lexicographically within the same subsection. (?!))
        fromAccountsTable(buffer)
                .expectShortVecInteger("count accounts", 6)
        // signed read-write
                .expect("payer account", PAYER) // (payer must be first account)
                .expect("second account", ACCOUNT1)
        // signed read-only
                .expect("third account", ACCOUNT2)
        // unsigned read-write
                .expect("fourth account", ACCOUNT3);
        // unsigned read-only
        final var unsignedReadOnlyAccounts = new HashSet<>();

        reader(buffer)
                .expectOneOf("fifth account", actual -> unsignedReadOnlyAccounts.add(account(actual)), PROGRAM1, ACCOUNT4)
                .expectOneOf("sixth account", actual -> unsignedReadOnlyAccounts.add(account(actual)), PROGRAM1, ACCOUNT4);

        assertThat(unsignedReadOnlyAccounts)
                .containsExactlyInAnyOrder(account(PROGRAM1), account(ACCOUNT4));
    }

    @Test
    void checkComplexUnsignedMessageAccountsTable()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        // (There does not appear to be a defined sequence for accounts within
        //  the same subsection of the accounts table.
        //  Some TypeScript client code sorts the account keys Base58 encoded
        //  lexicographically within the same subsection. (?!))

        fromAccountsTable(buffer)
                .expectShortVecInteger("count accounts", 11)
                // signed read-write
                .expect("payer account", PAYER) // (payer must be first account)
                .expect("second account", ACCOUNT1)
                .expect("third account", ACCOUNT6)
                .expect("fourth account", ACCOUNT3)
                // signed read-only
                .expect("fifth account", ACCOUNT2)
                .expect("sixth account", ACCOUNT7)
                // unsigned read-write
                .expect("seventh account", ACCOUNT8);

        // unsigned read-only
        final var unsignedReadOnlyAccounts = new HashSet<>();

        reader(buffer)
                .expectOneOf("eighth account", actual -> unsignedReadOnlyAccounts.add(account(actual)), PROGRAM1, ACCOUNT4, PROGRAM2, ACCOUNT5)
                .expectOneOf("ninth account", actual -> unsignedReadOnlyAccounts.add(account(actual)), PROGRAM1, ACCOUNT4, PROGRAM2, ACCOUNT5)
                .expectOneOf("tenth account", actual -> unsignedReadOnlyAccounts.add(account(actual)), PROGRAM1, ACCOUNT4, PROGRAM2, ACCOUNT5)
                .expectOneOf("eleventh account", actual -> unsignedReadOnlyAccounts.add(account(actual)), PROGRAM1, ACCOUNT4, PROGRAM2, ACCOUNT5);

        assertThat(unsignedReadOnlyAccounts)
                .containsExactlyInAnyOrder(
                        account(PROGRAM1), account(ACCOUNT4), account(PROGRAM2), account(ACCOUNT5));
    }

    @Test
    void checkSimpleUnsignedMessageBlockhash()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        fromBlockhash(buffer)
                .expect("block hash", BLOCKHASH);
    }

    @Test
    void checkComplexUnsignedMessageBlockhash()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        fromBlockhash(buffer)
                .expect("block hash", BLOCKHASH);
    }

    @Test
    void checkSimpleUnsignedMessageTransactionInstructionCount()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        fromInstructions(buffer)
                .expectShortVecInteger("count instructions", 1);
    }

    @Test
    void checkComplexUnsignedMessageTransactionInstructionCount()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        fromInstructions(buffer)
                .expectShortVecInteger("count instructions", 3);
    }

    @Test
    void checkComplexUnsignedMessageTransactionInstructionCountWhenInstructionsAddedInBulk()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessageWithInstructionsAddedInBulk(buffer);

        fromInstructions(buffer).expectShortVecInteger("count instructions", 3);
    }

    @Test
    void checkPartiallySignedMessageAddFirstSignature()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedLegacyMessage(buffer);

        Solana.forSigning(buffer)
                    .by(account(PAYER), (transaction, signature) -> signature.put(SIGNATURE_PAYER))
                    .build();

        fromSignatures(buffer)
                .expectShortVecInteger("number of signatures", 3)
                .expect("first signature (payer)", SIGNATURE_PAYER)
                .expect("second signature", UNSIGNED)
                .expect("third signature", UNSIGNED);
    }

    @Test
    void checkPartiallySignedMessageAddMidSignature()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedLegacyMessage(buffer);

        Solana.forSigning(buffer)
                .by(account(ACCOUNT1), (transaction, signature) -> signature.put(SIGNATURE1))
                .build();

        fromSignatures(buffer)
                .expectShortVecInteger("number of signatures", 3)
                .expect("first signature (payer)", UNSIGNED)
                .expect("second signature", SIGNATURE1)
                .expect("third signature", UNSIGNED);
    }

    @Test
    void checkPartiallySignedMessageAddEndSignature()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedLegacyMessage(buffer);

        Solana.forSigning(buffer)
                .by(account(ACCOUNT2), (transaction, signature) -> signature.put(SIGNATURE2))
                .build();

        fromSignatures(buffer)
                .expectShortVecInteger("number of signatures", 3)
                .expect("first signature (payer)", UNSIGNED)
                .expect("second signature", UNSIGNED)
                .expect("third signature", SIGNATURE2);
    }

    @Test
    void checkPartiallySignedMessageAddMultipleSignatures()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedLegacyMessage(buffer);

        Solana.forSigning(buffer)
                .by(account(PAYER), (transaction, signature) -> signature.put(SIGNATURE_PAYER))
                .by(account(ACCOUNT2), (transaction, signature) -> signature.put(SIGNATURE2))
                .build();

        fromSignatures(buffer)
                .expectShortVecInteger("number of signatures", 3)
                .expect("first signature (payer)", SIGNATURE_PAYER)
                .expect("second signature", UNSIGNED)
                .expect("third signature", SIGNATURE2);
    }

    @Test
    void checkPartiallySignedMessageAddAllSignatures()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Arrays.fill(buffer.array(), (byte) -77);

        writeSimpleUnsignedLegacyMessage(buffer);

        Solana.forSigning(buffer)
                .by(account(PAYER), (transaction, signature) -> signature.put(SIGNATURE_PAYER))
                .by(account(ACCOUNT2), (transaction, signature) -> signature.put(SIGNATURE2))
                .by(account(ACCOUNT1), (transaction, signature) -> signature.put(SIGNATURE1))
                .build();

        fromSignatures(buffer)
                .expectShortVecInteger("number of signatures", 3)
                .expect("first signature (payer)", SIGNATURE_PAYER)
                .expect("second signature", SIGNATURE1)
                .expect("third signature", SIGNATURE2);
    }

    @Test
    void checkPartiallySignedMessageChangeSignature()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleFullySignedLegacyMessage(buffer);

        Solana.forSigning(buffer)
                .by(account(ACCOUNT1), (transaction, signature) -> signature.put(SIGNATURE5))
                .build();

        fromSignatures(buffer)
                .expectShortVecInteger("number of signatures", 3)
                .expect("first signature (payer)", SIGNATURE_PAYER)
                .expect("second signature", SIGNATURE5)
                .expect("third signature", SIGNATURE2);
    }

    @Test
    void checkSimpleMessageInstructionAccountReferences()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        fromInstructions(buffer)
                .expectShortVecInteger("number of instructions", 1)
                .expect("program index", indexOfAccount(buffer, PROGRAM1))
                .expect("number of accounts", (byte) 4)
                .expect("first account index", indexOfAccount(buffer, ACCOUNT4))
                .expect("second account index", indexOfAccount(buffer, ACCOUNT1))
                .expect("third account index", indexOfAccount(buffer, ACCOUNT2))
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT3));
    }

    @Test
    void checkComplexMessageInstructionAccountReferences()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessage(buffer);

        fromInstruction(buffer, 0)
                .expect("program index", indexOfAccount(buffer, PROGRAM1))
                .expect("number of accounts", (byte) 4)
                .expect("first account index", indexOfAccount(buffer, ACCOUNT4))
                .expect("second account index", indexOfAccount(buffer, ACCOUNT1))
                .expect("third account index", indexOfAccount(buffer, ACCOUNT2))
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT3));

        fromInstruction(buffer, 1)
                .expect("program index", indexOfAccount(buffer, PROGRAM2))
                .expect("number of accounts", (byte) 7)
                .expect("first account index", indexOfAccount(buffer, ACCOUNT5))
                .expect("second account index", indexOfAccount(buffer, ACCOUNT1))
                .expect("third account index", indexOfAccount(buffer, ACCOUNT6))
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT2))
                .expect("fifth account index", indexOfAccount(buffer, ACCOUNT7))
                .expect("sixth account index", indexOfAccount(buffer, ACCOUNT3))
                .expect("seventh account index", indexOfAccount(buffer, ACCOUNT8));

        fromInstruction(buffer, 2)
                .expect("program index", indexOfAccount(buffer, PROGRAM1))
                .expect("number of accounts", (byte) 1)
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT3));
    }

    @Test
    void checkComplexMessageInstructionAccountReferencesWhenInstructionsAddedInBulk()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeComplexUnsignedLegacyMessageWithInstructionsAddedInBulk(buffer);

        fromInstruction(buffer, 0)
                .expect("program index", indexOfAccount(buffer, PROGRAM1))
                .expect("number of accounts", (byte) 4)
                .expect("first account index", indexOfAccount(buffer, ACCOUNT4))
                .expect("second account index", indexOfAccount(buffer, ACCOUNT1))
                .expect("third account index", indexOfAccount(buffer, ACCOUNT2))
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT3));

        fromInstruction(buffer, 1)
                .expect("program index", indexOfAccount(buffer, PROGRAM2))
                .expect("number of accounts", (byte) 7)
                .expect("first account index", indexOfAccount(buffer, ACCOUNT5))
                .expect("second account index", indexOfAccount(buffer, ACCOUNT1))
                .expect("third account index", indexOfAccount(buffer, ACCOUNT6))
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT2))
                .expect("fifth account index", indexOfAccount(buffer, ACCOUNT7))
                .expect("sixth account index", indexOfAccount(buffer, ACCOUNT3))
                .expect("seventh account index", indexOfAccount(buffer, ACCOUNT8));

        fromInstruction(buffer, 2)
                .expect("program index", indexOfAccount(buffer, PROGRAM1))
                .expect("number of accounts", (byte) 1)
                .expect("fourth account index", indexOfAccount(buffer, ACCOUNT3));
    }

    @Test
    void checkThatDataIsBytePerfectWhenInstructionIsZero()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        final byte[] none = new byte[0];
        Solana.builder(buffer)
                .legacy()
                .payer(account(PAYER))
                .recent(blockhash(BLOCKHASH))
                .instructions(tb -> tb
                        .append(ib -> ib
                                .program(account(PROGRAM1))
                                .data(none.length, w -> w.put(none))))
                .seal()
                .unsigned()
                .build();

        fromInstruction(buffer, 0)
                .expect("program", indexOfAccount(buffer, PROGRAM1))
                .expect("number of accounts", (byte) 0)
                .expectShortVecInteger("data length", none.length);
    }


    @Test
    void checkThatDataIsBytePerfectWhenInstructionIsSmall()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        jumpToInstruction(buffer, 0);
        buffer.position(buffer.position() + 6);

        reader(buffer)
                .expectShortVecInteger("data length", DATA1.length)
                .expect("data", DATA1);
    }

    @Test
    void checkThatDataIsBytePerfectWhenInstructionIsMedium()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessageWithBigData(buffer);

        jumpToInstruction(buffer, 0);
        buffer.position(buffer.position() + 2);

        reader(buffer)
                .expectShortVecInteger("data length", DATA3.length)
                .expect("data", DATA3);

    }
}
