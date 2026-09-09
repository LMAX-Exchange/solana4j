package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.parameterisation.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

import java.nio.ByteBuffer;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class GetTransactionIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.createKeyPair("from");
        solana.createKeyPair("to");
        solana.airdropSol("payer", "0.1");
        solana.airdropSol("from", "0.1");
    }

    @ParameterizedMessageEncodingTest
    void shouldReturnCorrectTransactionVersionAndMetadata(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.transferSol("from: from", "to: to", "amountSol: 0.01", "payer: payer", "rememberTransactionAs: tx");

        final TransactionResponse response = solana.getTransactionResponse("tx");

        assertNotNull(response);
        assertTrue(response.getSlot() > 0L);
        assertNotNull(response.getBlockTime());
        assertNotNull(response.getTransactionIndex());

        final String expectedVersion;
        if ("Legacy".equals(messageEncoding))
        {
            expectedVersion = "legacy";
        }
        else if ("V0".equals(messageEncoding))
        {
            expectedVersion = "0";
        }
        else
        {
            expectedVersion = "1";
        }
        assertEquals(expectedVersion, response.getVersion());

        final var metadata = response.getMetadata();
        assertNull(metadata.getErr());
        assertTrue(metadata.getFee() > 0L);
        assertTrue(metadata.getComputeUnitsConsumed() > 0L);
        assertFalse(metadata.getPreBalances().isEmpty());
        assertEquals(metadata.getPreBalances().size(), metadata.getPostBalances().size());

        final var transactionData = response.getTransactionData();
        assertNotNull(transactionData);
        assertFalse(transactionData.getEncodedTransactionData().isEmpty());

        final String base64Data = transactionData.getEncodedTransactionData().get(0);
        final byte[] rawTransaction = Base64.getDecoder().decode(base64Data);
        final ByteBuffer buffer = ByteBuffer.wrap(rawTransaction);

        final var message = Solana.read(buffer);
        final var view = message.accept(v -> v);

        assertEquals(2, view.countAccountsSigned());
        assertFalse(view.staticAccounts().isEmpty());
        assertNotNull(view.feePayer());
        assertNotNull(view.recentBlockHash());

        if ("V1".equals(messageEncoding))
        {
            final var v1View = (MessageVisitor.Version1MessageView) view;
            assertEquals(1, v1View.version());
            assertTrue(v1View.hasComputeUnitLimit());
            assertTrue(v1View.computeUnitLimit() > 0);
            assertTrue(v1View.hasLoadedAccountsDataSizeLimit());
            assertTrue(v1View.loadedAccountsDataSizeLimit() > 0);
        }
        else if ("V0".equals(messageEncoding))
        {
            final var v0View = (MessageVisitor.Version0MessageView) view;
            assertEquals(0, v0View.version());
        }
        else
        {
            final var legacyView = (MessageVisitor.LegacyMessageView) view;
            assertEquals(1, legacyView.instructions().size());
        }
    }
}
