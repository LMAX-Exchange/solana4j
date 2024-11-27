package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Message;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.getByteBufferSignerFor;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedV0Message;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleUnsignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleUnsignedV0Message;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SolanaSignedMessageBuilderTest
{
    @Test
    void shouldAppendSignaturesWithSignedMessageBuilderLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedLegacyMessage(buffer);

        final SolanaSignedMessageBuilder signedMessageBuilder = new SolanaSignedMessageBuilder(buffer);
        signedMessageBuilder.by(Solana.account(PAYER), getByteBufferSignerFor(PAYER));
        signedMessageBuilder.by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1));
        signedMessageBuilder.by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2));

        final Message signedMessageActual = signedMessageBuilder.build();

        final Message signedMessageExpected = writeSimpleFullySignedLegacyMessage(buffer);

        assertThat(signedMessageActual).usingRecursiveComparison().isEqualTo(signedMessageExpected);
    }

    @Test
    void shouldAppendSignaturesWithSignedMessageBuilderV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        writeSimpleUnsignedV0Message(buffer);

        final SolanaSignedMessageBuilder signedMessageBuilder = new SolanaSignedMessageBuilder(buffer);
        signedMessageBuilder.by(Solana.account(PAYER), getByteBufferSignerFor(PAYER));
        signedMessageBuilder.by(Solana.account(ACCOUNT1), getByteBufferSignerFor(ACCOUNT1));
        signedMessageBuilder.by(Solana.account(ACCOUNT2), getByteBufferSignerFor(ACCOUNT2));

        final Message signedMessageActual = signedMessageBuilder.build();

        final Message signedMessageExpected = writeSimpleFullySignedV0Message(buffer);

        assertThat(signedMessageActual).usingRecursiveComparison().isEqualTo(signedMessageExpected);
    }
}