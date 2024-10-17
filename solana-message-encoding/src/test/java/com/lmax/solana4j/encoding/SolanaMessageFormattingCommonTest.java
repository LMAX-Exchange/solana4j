package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AccountLookupEntry;
import com.lmax.solana4j.api.PublicKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static com.lmax.solana4j.Solana.MAX_MESSAGE_SIZE;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT5;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT6;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT7;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.DATA2;
import static com.lmax.solana4j.Solana4jTestHelper.LOOKUP_TABLE_ADDRESS1;
import static com.lmax.solana4j.Solana4jTestHelper.LOOKUP_TABLE_ADDRESS2;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.api.MessageVisitor.AccountLookupView;
import static com.lmax.solana4j.api.MessageVisitor.InstructionView;
import static org.assertj.core.api.Assertions.assertThat;

class SolanaMessageFormattingCommonTest
{
    private ByteBuffer buffer;
    private SolanaMessageFormattingCommon solanaMessageFormattingCommon;

    @BeforeEach
    void setup()
    {
        buffer = ByteBuffer.allocate(MAX_MESSAGE_SIZE);
        solanaMessageFormattingCommon = new SolanaMessageFormattingCommon(buffer);
    }

    @Test
    void writesLong()
    {
        solanaMessageFormattingCommon.writeLong(10000000000L);

        // flip underlying buffer for reading
        buffer.flip();

        long l = solanaMessageFormattingCommon.readLong();
        assertThat(l).isEqualTo(10000000000L);
    }

    @Test
    void writesInt()
    {
        solanaMessageFormattingCommon.writeInt(20);

        // flip underlying buffer for reading
        buffer.flip();

        long l = solanaMessageFormattingCommon.readInt();
        assertThat(l).isEqualTo(20);
    }

    @Test
    void reserveSignatures()
    {
        solanaMessageFormattingCommon.reserveSignatures(10);

        // buffer position has moved 10 multiplied by 64 (length of 1 signature) along + 1
        assertThat(buffer.position()).isEqualTo(10 * 64 + 1);
    }

    @Test
    void readsSignatures()
    {
        // there are 2 signatures
        buffer.put((byte) 2);
        buffer.put(SIGNATURE1);
        buffer.put(SIGNATURE2);

        // flip underlying buffer for reading
        buffer.flip();

        final List<ByteBuffer> signatures = solanaMessageFormattingCommon.readSignatures();

        assertThat(signatures.get(0).array()).isEqualTo(SIGNATURE1);
        assertThat(signatures.get(1).array()).isEqualTo(SIGNATURE2);
    }

    @Test
    void writesAccountLookups()
    {
        // ACCOUNT_LOOKUP_TABLE1 is the lookup table address
        final AccountLookupEntry accountLookup1 = new SolanaAccountLookupEntry(new SolanaAccount(LOOKUP_TABLE_ADDRESS1));
        // ACCOUNT3 is the address referenced at the lookup table index 1
        accountLookup1.addReadOnlyEntry(new SolanaAccount(ACCOUNT3), 1);
        // ACCOUNT4 is the address referenced at the lookup table index 2
        accountLookup1.addReadWriteEntry(new SolanaAccount(ACCOUNT4), 2);
        accountLookup1.addReadWriteEntry(new SolanaAccount(ACCOUNT5), 3);

        // ACCOUNT_LOOKUP_TABLE2 is the lookup table address
        final AccountLookupEntry accountLookup2 = new SolanaAccountLookupEntry(new SolanaAccount(LOOKUP_TABLE_ADDRESS2));
        // ACCOUNT6 is the address referenced at the lookup table index 4
        accountLookup2.addReadOnlyEntry(new SolanaAccount(ACCOUNT6), 4);
        // ACCOUNT7 is the address referenced at the lookup table index 5
        accountLookup2.addReadWriteEntry(new SolanaAccount(ACCOUNT7), 5);

        solanaMessageFormattingCommon.writeAccountLookups(List.of(accountLookup1, accountLookup2));

        // flip underlying buffer for reading
        buffer.flip();

        final List<AccountLookupView> accountLookupViews = solanaMessageFormattingCommon.readAccountLookups();

        // lookup table 1 address
        final AccountLookupView accountLookupView1 = accountLookupViews.get(0);
        assertThat(accountLookupView1.accountLookup()).isEqualTo(new SolanaAccount(LOOKUP_TABLE_ADDRESS1));

        assertThat(accountLookupView1.readOnlyTableIndexes().size()).isEqualTo(1);
        assertThat(accountLookupView1.readOnlyTableIndexes().get(0)).isEqualTo(1);
        assertThat(accountLookupView1.readWriteTableIndexes().size()).isEqualTo(2);
        assertThat(accountLookupView1.readWriteTableIndexes().get(0)).isEqualTo(2);
        assertThat(accountLookupView1.readWriteTableIndexes().get(1)).isEqualTo(3);

        // lookup table 2 address
        final AccountLookupView accountLookupView2 = accountLookupViews.get(1);
        assertThat(accountLookupView2.accountLookup()).isEqualTo(new SolanaAccount(LOOKUP_TABLE_ADDRESS2));

        assertThat(accountLookupView2.readOnlyTableIndexes().size()).isEqualTo(1);
        assertThat(accountLookupView2.readOnlyTableIndexes().get(0)).isEqualTo(4);
        assertThat(accountLookupView2.readWriteTableIndexes().size()).isEqualTo(1);
        assertThat(accountLookupView2.readWriteTableIndexes().get(0)).isEqualTo(5);
    }

    @Test
    void writesBlockhash()
    {
        solanaMessageFormattingCommon.writeBlockHash(new SolanaBlockhash(BLOCKHASH));

        // flip underlying buffer for reading
        buffer.flip();

        final SolanaBlockhash solanaBlockhash = (SolanaBlockhash) solanaMessageFormattingCommon.readBlockHash();
        assertThat(solanaBlockhash).isEqualTo(new SolanaBlockhash(BLOCKHASH)).usingRecursiveComparison();
    }

    @Test
    void writesByte()
    {
        solanaMessageFormattingCommon.writeByte((byte) 10);

        // flip underlying buffer for reading
        buffer.flip();

        final byte readByte = solanaMessageFormattingCommon.readByte();
        assertThat(readByte).isEqualTo((byte) 10);
    }

    @Test
    void writesAccounts()
    {
        solanaMessageFormattingCommon.writeStaticAccounts(List.of(
                new SolanaAccount(ACCOUNT1),
                new SolanaAccount(ACCOUNT2),
                new SolanaAccount(ACCOUNT3))
        );

        // flip underlying buffer for reading
        buffer.flip();

        final List<PublicKey> accounts = solanaMessageFormattingCommon.readStaticAccounts();
        assertThat(accounts).isEqualTo(List.of(
                new SolanaAccount(ACCOUNT1),
                new SolanaAccount(ACCOUNT2),
                new SolanaAccount(ACCOUNT3))
        );
    }

    @Test
    void writesTransactionInstructions()
    {
        final List<PublicKey> accounts = List.of(
                new SolanaAccount(ACCOUNT1),
                new SolanaAccount(PROGRAM1),
                new SolanaAccount(ACCOUNT2),
                new SolanaAccount(ACCOUNT3),
                new SolanaAccount(ACCOUNT4),
                new SolanaAccount(PROGRAM2)
        );

        solanaMessageFormattingCommon.writeInstructions(
                List.of(new SolanaTransactionInstruction(
                        List.of(
                                new SolanaAccountReference(new SolanaAccount(ACCOUNT1), true, true, false),
                                new SolanaAccountReference(new SolanaAccount(ACCOUNT2), true, true, false)
                        ),
                        new SolanaAccount(PROGRAM1),
                        10,
                        w -> w.put(DATA1)),
                        new SolanaTransactionInstruction(
                                List.of(
                                        new SolanaAccountReference(new SolanaAccount(ACCOUNT3), true, true, false),
                                        new SolanaAccountReference(new SolanaAccount(ACCOUNT4), true, true, false)
                                ),
                                new SolanaAccount(PROGRAM2),
                                15,
                                w -> w.put(DATA2))
                ),
                accounts::indexOf
        );

        // flip underlying buffer for reading
        buffer.flip();

        final List<InstructionView> instructions = solanaMessageFormattingCommon.readInstructions();

        final InstructionView instruction1 = instructions.get(0);
        assertThat(instruction1.programIndex()).isEqualTo(1);
        assertThat(instruction1.data()).isEqualTo(ByteBuffer.wrap(DATA1));
        assertThat(instruction1.accountIndexes()).isEqualTo(List.of(0, 2));

        final InstructionView instruction2 = instructions.get(1);

        assertThat(instruction2.programIndex()).isEqualTo(5);
        assertThat(instruction2.data()).isEqualTo(ByteBuffer.wrap(DATA2));
        assertThat(instruction2.accountIndexes()).isEqualTo(List.of(3, 4));
    }
}