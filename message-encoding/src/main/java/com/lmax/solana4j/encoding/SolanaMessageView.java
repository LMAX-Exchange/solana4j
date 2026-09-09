package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.MessageVisitor.MessageView;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

abstract class SolanaMessageView implements MessageView
{
    private final int countAccountsSigned;
    private final int countAccountsSignedReadOnly;
    private final int countAccountsUnsignedReadOnly;

    private final MessageVisitor.AccountsView accountsView;
    private final List<ByteBuffer> signatures;
    private final PublicKey feePayer;
    private final Blockhash recentBlockHash;

    private final ByteBuffer transaction;

    SolanaMessageView(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final MessageVisitor.AccountsView accountsView,
            final ByteBuffer transaction,
            final List<ByteBuffer> signatures,
            final PublicKey feePayer,
            final Blockhash recentBlockHash)
    {
        this.countAccountsSigned = countAccountsSigned;
        this.countAccountsSignedReadOnly = countAccountsSignedReadOnly;
        this.countAccountsUnsignedReadOnly = countAccountsUnsignedReadOnly;
        this.accountsView = accountsView;
        this.transaction = transaction;
        this.signatures = signatures;
        this.feePayer = feePayer;
        this.recentBlockHash = recentBlockHash;
    }

    static MessageView fromBuffer(final ByteBuffer buffer)
    {
        // Peek at first byte to detect V1 format (0x81 prefix, signatures at tail)
        final byte firstByte = buffer.get();
        buffer.rewind();

        if (firstByte == (byte) 0x81)
        {
            return readV1Message(buffer);
        }

        // Legacy or V0 format - signatures at the beginning
        final var formatter = new SolanaMessageFormattingCommon(buffer);

        final var signatures = formatter.readSignatures();
        final var transaction = buffer.slice();
        final var first = formatter.readByte();

        boolean v0Format = false;
        boolean legacyFormat = false;

        final int version;
        final int countAccountsSigned;

        if (first == (byte) 0x80)
        {
            v0Format = true;
            version = 0;
            countAccountsSigned = formatter.readByte() & 0xff;
        }
        else if ((first & 0x80) == 0x00)
        {
            legacyFormat = true;
            countAccountsSigned = first & 0xff;
        }
        else
        {
            throw new RuntimeException("unsupported message format");
        }

        final int countAccountsSignedReadOnly = formatter.readByte() & 0xff; //convert ignoring the sign bit
        final int countAccountsUnsignedReadOnly = formatter.readByte() & 0xff;

        final var staticAccounts = formatter.readStaticAccounts();

        final var blockhash = formatter.readBlockHash();

        final var instructions = formatter.readInstructions();

        if (legacyFormat)
        {
            return new SolanaLegacyMessageView(
                    countAccountsSigned,
                    countAccountsSignedReadOnly,
                    countAccountsUnsignedReadOnly,
                    new SolanaLegacyAccountsView(staticAccounts),
                    transaction,
                    signatures,
                    staticAccounts.get(0),
                    blockhash,
                    instructions);
        }
        else if (v0Format)
        {
            final var accountLookups = formatter.readAccountLookups();

            return new SolanaV0MessageView(
                    countAccountsSigned,
                    countAccountsSignedReadOnly,
                    countAccountsUnsignedReadOnly,
                    new SolanaV0AccountsView(staticAccounts, accountLookups),
                    transaction,
                    signatures,
                    staticAccounts.get(0),
                    blockhash,
                    instructions,
                    accountLookups);
        }
        else
        {
            throw new RuntimeException("unsupported message format");
        }
    }

    private static MessageView readV1Message(final ByteBuffer buffer)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        final var formatter = new SolanaMessageFormattingCommon(buffer);

        // Read version prefix
        formatter.readByte(); // 0x81

        // Read header
        final int countAccountsSigned = formatter.readByte() & 0xff;
        final int countAccountsSignedReadOnly = formatter.readByte() & 0xff;
        final int countAccountsUnsignedReadOnly = formatter.readByte() & 0xff;

        // Read config mask (u32 LE)
        final int configMask = buffer.getInt();

        // Read blockhash
        final var blockhash = formatter.readBlockHash();

        // Read num_instructions (fixed u8)
        final int numInstructions = formatter.readByte() & 0xff;

        // Read num_addresses (fixed u8)
        final int numAddresses = formatter.readByte() & 0xff;

        // Read addresses (fixed count, no compact-u16 prefix)
        final var staticAccounts = readFixedAccounts(buffer, numAddresses);

        // Read config values (in bit order)
        long priorityFee = 0;
        int computeUnitLimit = 0;
        int loadedAccountsDataSizeLimit = 0;
        int requestedHeapSize = 0;

        if ((configMask & SolanaMessageWriterV1.CONFIG_PRIORITY_FEE) == SolanaMessageWriterV1.CONFIG_PRIORITY_FEE)
        {
            priorityFee = buffer.getLong();
        }
        if ((configMask & SolanaMessageWriterV1.CONFIG_COMPUTE_UNIT_LIMIT) != 0)
        {
            computeUnitLimit = buffer.getInt();
        }
        if ((configMask & SolanaMessageWriterV1.CONFIG_LOADED_ACCOUNTS_DATA_SIZE) != 0)
        {
            loadedAccountsDataSizeLimit = buffer.getInt();
        }
        if ((configMask & SolanaMessageWriterV1.CONFIG_REQUESTED_HEAP_SIZE) != 0)
        {
            requestedHeapSize = buffer.getInt();
        }

        // Read instruction headers (N x 4 bytes: program_id_index u8, num_accounts u8, data_len u16 LE)
        final int[] programIndices = new int[numInstructions];
        final int[] numAccountsPerInstruction = new int[numInstructions];
        final int[] dataLens = new int[numInstructions];
        for (int i = 0; i < numInstructions; i++)
        {
            programIndices[i] = formatter.readByte() & 0xff;
            numAccountsPerInstruction[i] = formatter.readByte() & 0xff;
            dataLens[i] = buffer.getShort() & 0xffff;
        }

        // Read instruction payloads (per instruction: account indices u8, then data)
        final List<MessageVisitor.InstructionView> instructions = new ArrayList<>(numInstructions);
        for (int i = 0; i < numInstructions; i++)
        {
            final var accountIndexes = new ArrayList<Integer>();
            for (int j = 0; j < numAccountsPerInstruction[i]; j++)
            {
                accountIndexes.add(formatter.readByte() & 0xff);
            }
            final var ro = buffer.asReadOnlyBuffer();
            ro.limit(ro.position() + dataLens[i]);
            final var data = ro.slice();
            buffer.position(buffer.position() + dataLens[i]);

            instructions.add(new SolanaInstructionView(programIndices[i], accountIndexes, data));
        }

        // Create transaction slice (message body from 0 to current position, excluding signatures)
        final int messageBodyEnd = buffer.position();
        final var transactionDup = buffer.duplicate();
        transactionDup.position(0);
        transactionDup.limit(messageBodyEnd);
        final var transaction = transactionDup.slice();

        // Read signatures from tail (count from header, no length prefix)
        final List<ByteBuffer> signatures = new ArrayList<>(countAccountsSigned);
        for (int i = 0; i < countAccountsSigned; i++)
        {
            final byte[] bytes = new byte[64];
            buffer.get(bytes);
            signatures.add(ByteBuffer.wrap(bytes));
        }

        return new SolanaV1MessageView(
                countAccountsSigned,
                countAccountsSignedReadOnly,
                countAccountsUnsignedReadOnly,
                new SolanaLegacyAccountsView(staticAccounts),
                transaction,
                signatures,
                staticAccounts.get(0),
                blockhash,
                instructions,
                configMask,
                priorityFee,
                computeUnitLimit,
                loadedAccountsDataSizeLimit,
                requestedHeapSize);
    }

    private static List<PublicKey> readFixedAccounts(final ByteBuffer buffer, final int count)
    {
        final List<PublicKey> publicKeys = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
        {
            final byte[] bytes = new byte[32];
            buffer.get(bytes);
            publicKeys.add(new SolanaAccount(bytes));
        }
        return publicKeys;
    }

    @Override
    public int countAccountsSigned()
    {
        return countAccountsSigned;
    }

    @Override
    public int countAccountsSignedReadOnly()
    {
        return countAccountsSignedReadOnly;
    }

    @Override
    public int countAccountsUnsignedReadOnly()
    {
        return countAccountsUnsignedReadOnly;
    }

    @Override
    public ByteBuffer transaction()
    {
        return transaction.duplicate();
    }

    @Override
    public ByteBuffer signature(final PublicKey account)
    {
        int index = 0;
        for (final var signature : signatures)
        {
            if (accountsView.staticAccounts().get(index).equals(account))
            {
                return signature;
            }
            index++;
        }
        throw new NoSuchElementException();
    }

    @Override
    public PublicKey feePayer()
    {
        return feePayer;
    }

    @Override
    public Blockhash recentBlockHash()
    {
        return recentBlockHash;
    }

    @Override
    public boolean isSigner(final PublicKey account)
    {
        final var index = accountsView.staticAccounts().indexOf(account);

        if (index == -1)
        {
            return false;
        }

        return index < countAccountsSigned;
    }

    @Override
    public List<PublicKey> signers()
    {
        return accountsView.staticAccounts().subList(0, countAccountsSigned);
    }

    @Override
    public List<PublicKey> staticAccounts()
    {
        return accountsView.staticAccounts();
    }

}
