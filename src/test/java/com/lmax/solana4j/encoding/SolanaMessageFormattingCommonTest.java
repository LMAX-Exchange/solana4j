package com.lmax.solana4j.encoding;

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
import static com.lmax.solana4j.Solana4jTestHelper.LOOKUP_TABLE_ADDRESS1;
import static com.lmax.solana4j.Solana4jTestHelper.LOOKUP_TABLE_ADDRESS2;
import static com.lmax.solana4j.Solana4jTestHelper.BLOCKHASH;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.DATA2;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM2;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE1;
import static com.lmax.solana4j.Solana4jTestHelper.SIGNATURE2;
import static com.lmax.solana4j.api.MessageVisitor.AccountLookupTableView;
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
    void writesLookupAccounts()
    {
        // ACCOUNT_LOOKUP_TABLE1 is the lookup table address
        final SolanaAddressLookupTableEntrys lookupTable1 = new SolanaAddressLookupTableEntrys(new SolanaAccount(LOOKUP_TABLE_ADDRESS1));
        // ACCOUNT3 is the address referenced at the lookup table index 1
        lookupTable1.addReadOnlyEntry(new SolanaAccount(ACCOUNT3), 1);
        // ACCOUNT4 is the address referenced at the lookup table index 2
        lookupTable1.addReadWriteEntry(new SolanaAccount(ACCOUNT4), 2);
        lookupTable1.addReadWriteEntry(new SolanaAccount(ACCOUNT5), 3);

        // ACCOUNT_LOOKUP_TABLE1 is the lookup table address
        final SolanaAddressLookupTableEntrys lookupTable2 = new SolanaAddressLookupTableEntrys(new SolanaAccount(LOOKUP_TABLE_ADDRESS2));
        // ACCOUNT5 is the address referenced at the lookup table index 1
        lookupTable2.addReadOnlyEntry(new SolanaAccount(ACCOUNT6), 4);
        // ACCOUNT6 is the address referenced at the lookup table index 2
        lookupTable2.addReadWriteEntry(new SolanaAccount(ACCOUNT7), 5);

        solanaMessageFormattingCommon.writeLookupAccounts(List.of(lookupTable1, lookupTable2));

        // flip underlying buffer for reading
        buffer.flip();

        final List<AccountLookupTableView> accountLookupTableViews = solanaMessageFormattingCommon.readLookupAccounts();

        // lookup table 1 address
        final AccountLookupTableView accountLookupTable1 = accountLookupTableViews.get(0);
        assertThat(accountLookupTable1.lookupAccount()).isEqualTo(new SolanaAccount(LOOKUP_TABLE_ADDRESS1));

        assertThat(accountLookupTable1.readOnlyTableIndexes().size()).isEqualTo(1);
        assertThat(accountLookupTable1.readOnlyTableIndexes().get(0)).isEqualTo(1);
        assertThat(accountLookupTable1.readWriteTableIndexes().size()).isEqualTo(2);
        assertThat(accountLookupTable1.readWriteTableIndexes().get(0)).isEqualTo(2);
        assertThat(accountLookupTable1.readWriteTableIndexes().get(1)).isEqualTo(3);

        // lookup table 2 address
        final AccountLookupTableView accountLookupTable2 = accountLookupTableViews.get(1);
        assertThat(accountLookupTable2.lookupAccount()).isEqualTo(new SolanaAccount(LOOKUP_TABLE_ADDRESS2));

        assertThat(accountLookupTable2.readOnlyTableIndexes().size()).isEqualTo(1);
        assertThat(accountLookupTable2.readOnlyTableIndexes().get(0)).isEqualTo(4);
        assertThat(accountLookupTable2.readWriteTableIndexes().size()).isEqualTo(1);
        assertThat(accountLookupTable2.readWriteTableIndexes().get(0)).isEqualTo(5);
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