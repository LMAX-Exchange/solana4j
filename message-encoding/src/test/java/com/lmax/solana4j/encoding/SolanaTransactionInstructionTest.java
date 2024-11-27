package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionInstruction;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SolanaTransactionInstructionTest
{

    @Test
    void shouldBuildTransactionInstruction()
    {
        final TransactionInstruction instruction = Solana.instruction(ib ->
                ib
                        .account(new SolanaAccount(ACCOUNT2), false, false)
                        .account(new SolanaAccount(ACCOUNT1), true, true)
                        .program(new SolanaAccount(PROGRAM1))
                        .data(10, w -> w.put(DATA1)));

        assertTransactionInstruction(
                instruction,
                List.of(new SolanaAccount(ACCOUNT2), new SolanaAccount(ACCOUNT1)),
                new SolanaAccount(PROGRAM1),
                DATA1
        );
    }

    @Test
    void shouldReadAccountReferencesInTheOrderTheyAreWritten()
    {
        final TransactionInstruction instruction = Solana.instruction(ib ->
                ib
                        .account(new SolanaAccount(ACCOUNT4), false, false)
                        .account(new SolanaAccount(ACCOUNT3), false, true)
                        .account(new SolanaAccount(ACCOUNT2), true, false)
                        .account(new SolanaAccount(ACCOUNT1), true, true)
                        .program(new SolanaAccount(PROGRAM1))
                        .data(10, w -> w.put(DATA1))
        );

        // it was tempting to me to reorder these internal account references in terms of signer-writer-readonly
        // sorted-ness in other areas, however the order they are stated really does matter
        assertTransactionInstruction(
                instruction,
                List.of(new SolanaAccount(ACCOUNT4), new SolanaAccount(ACCOUNT3), new SolanaAccount(ACCOUNT2), new SolanaAccount(ACCOUNT1)),
                new SolanaAccount(PROGRAM1),
                DATA1
        );
    }

    private void assertTransactionInstruction(
            final TransactionInstruction instruction,
            final List<PublicKey> expectedAccountReferences,
            final PublicKey expectedProgramId,
            final byte[] expectedData)
    {
        assertThat(instruction.datasize()).isEqualTo(expectedData.length);
        assertThat(instruction.accountReferences()
                .stream()
                .map(TransactionInstruction.AccountReference::account)
                .collect(Collectors.toList()))
                .usingRecursiveComparison()
                .isEqualTo(expectedAccountReferences);

        assertThat(instruction.program()).isEqualTo(expectedProgramId);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(expectedData.length);
        instruction.data().accept(byteBuffer);
        assertThat(byteBuffer.array()).isEqualTo(expectedData);
    }
}