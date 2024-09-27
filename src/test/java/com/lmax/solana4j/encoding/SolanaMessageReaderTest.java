package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Message;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedLegacyMessage;
import static com.lmax.solana4j.Solana4jTestHelper.writeSimpleFullySignedV0Message;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SolanaMessageReaderTest
{
    @Test
    void shouldReadV0Message()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        final Message messageWritten = writeSimpleFullySignedV0Message(buffer);

        final Message messageRead = Solana.read(buffer);

        assertThat(messageWritten).usingRecursiveComparison().isEqualTo(messageRead);
    }

    @Test
    void shouldReadLegacyMessage()
    {
        final var buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        final Message messageWritten = writeSimpleFullySignedLegacyMessage(buffer);

        final Message messageRead = Solana.read(buffer);

        assertThat(messageWritten).usingRecursiveComparison().isEqualTo(messageRead);
    }
}