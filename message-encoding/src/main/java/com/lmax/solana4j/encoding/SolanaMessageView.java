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

    protected final MessageVisitor.AccountsView accountsView;
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
        final byte firstByte = buffer.get(buffer.position());

        if (firstByte == (byte) 0x81)
        {
            return readV1Message(buffer);
        }

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

        if (buffer.remaining() > SolanaEncoding.MAX_V1_MESSAGE_SIZE)
        {
            throw new IllegalStateException("message is malformed; V1 message length " + buffer.remaining() +
                    " exceeds the maximum of " + SolanaEncoding.MAX_V1_MESSAGE_SIZE + " bytes");
        }

        final int messageStart = buffer.position();
        final var formatter = new SolanaMessageFormattingCommon(buffer);

        formatter.readByte();

        final int countAccountsSigned = formatter.readByte() & 0xff;
        final int countAccountsSignedReadOnly = formatter.readByte() & 0xff;
        final int countAccountsUnsignedReadOnly = formatter.readByte() & 0xff;

        final int configMask = buffer.getInt();
        validateConfigMask(configMask);

        final var blockhash = formatter.readBlockHash();

        final int numInstructions = formatter.readByte() & 0xff;

        final int numAddresses = formatter.readByte() & 0xff;

        validateV1Counts(
                countAccountsSigned,
                countAccountsSignedReadOnly,
                countAccountsUnsignedReadOnly,
                numInstructions,
                numAddresses);

        final var staticAccounts = readFixedAccounts(buffer, numAddresses);

        if (staticAccounts.isEmpty())
        {
            throw new IllegalStateException("message is malformed; V1 message must declare at least one account address");
        }

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

        final int[] programIndices = new int[numInstructions];
        final int[] numAccountsPerInstruction = new int[numInstructions];
        final int[] dataLens = new int[numInstructions];
        for (int i = 0; i < numInstructions; i++)
        {
            programIndices[i] = formatter.readByte() & 0xff;
            numAccountsPerInstruction[i] = formatter.readByte() & 0xff;
            dataLens[i] = buffer.getShort() & 0xffff;

            if (programIndices[i] >= numAddresses)
            {
                throw new IllegalStateException("message is malformed; instruction " + i + " program index " +
                        programIndices[i] + " is outside the " + numAddresses + " declared account addresses");
            }
        }

        final List<MessageVisitor.InstructionView> instructions = new ArrayList<>(numInstructions);
        for (int i = 0; i < numInstructions; i++)
        {
            final var accountIndexes = new ArrayList<Integer>();
            for (int j = 0; j < numAccountsPerInstruction[i]; j++)
            {
                final int accountIndex = formatter.readByte() & 0xff;
                if (accountIndex >= numAddresses)
                {
                    throw new IllegalStateException("message is malformed; instruction " + i + " account index " +
                            accountIndex + " is outside the " + numAddresses + " declared account addresses");
                }
                accountIndexes.add(accountIndex);
            }
            final var ro = buffer.asReadOnlyBuffer();
            ro.limit(ro.position() + dataLens[i]);
            final var data = ro.slice();
            buffer.position(buffer.position() + dataLens[i]);

            instructions.add(new SolanaInstructionView(programIndices[i], accountIndexes, data));
        }

        final int messageBodyEnd = buffer.position();
        final var transactionDup = buffer.duplicate();
        transactionDup.position(messageStart);
        transactionDup.limit(messageBodyEnd);
        final var transaction = transactionDup.slice();

        final List<ByteBuffer> signatures = new ArrayList<>(countAccountsSigned);
        for (int i = 0; i < countAccountsSigned; i++)
        {
            final byte[] bytes = new byte[64];
            buffer.get(bytes);
            signatures.add(ByteBuffer.wrap(bytes));
        }

        if (buffer.remaining() != 0)
        {
            throw new IllegalStateException("message is malformed; " + buffer.remaining() +
                    " unexpected trailing bytes after the signatures");
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

    private static void validateConfigMask(final int configMask)
    {
        final int allowedBits =
                SolanaMessageWriterV1.CONFIG_PRIORITY_FEE
                        | SolanaMessageWriterV1.CONFIG_COMPUTE_UNIT_LIMIT
                        | SolanaMessageWriterV1.CONFIG_LOADED_ACCOUNTS_DATA_SIZE
                        | SolanaMessageWriterV1.CONFIG_REQUESTED_HEAP_SIZE;

        if ((configMask & ~allowedBits) != 0)
        {
            throw new IllegalStateException("message is malformed; config mask 0x" + Integer.toHexString(configMask) +
                    " sets bits outside the defined V1 config fields (allowed mask 0x" + Integer.toHexString(allowedBits) + ")");
        }

        final int priorityFeeLow = configMask & SolanaMessageWriterV1.CONFIG_PRIORITY_FEE;
        if (priorityFeeLow != 0 && priorityFeeLow != SolanaMessageWriterV1.CONFIG_PRIORITY_FEE)
        {
            throw new IllegalStateException("message is malformed; config mask 0x" + Integer.toHexString(configMask) +
                    " must set both priority fee bits (0x" + Integer.toHexString(SolanaMessageWriterV1.CONFIG_PRIORITY_FEE) + ") together");
        }
    }

    private static void validateV1Counts(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final int numInstructions,
            final int numAddresses)
    {
        if (numInstructions > SolanaMessageWriterV1.MAX_INSTRUCTIONS)
        {
            throw new IllegalStateException("message is malformed; V1 messages support at most " +
                    SolanaMessageWriterV1.MAX_INSTRUCTIONS + " instructions but header declares " + numInstructions);
        }
        if (numAddresses > SolanaMessageWriterV1.MAX_ADDRESSES)
        {
            throw new IllegalStateException("message is malformed; V1 messages support at most " +
                    SolanaMessageWriterV1.MAX_ADDRESSES + " account addresses but header declares " + numAddresses);
        }
        if (countAccountsSigned > SolanaMessageWriterV1.MAX_SIGNERS)
        {
            throw new IllegalStateException("message is malformed; V1 messages support at most " +
                    SolanaMessageWriterV1.MAX_SIGNERS + " signers but header declares " + countAccountsSigned);
        }
        if (countAccountsSigned < 1)
        {
            throw new IllegalStateException("message is malformed; header declares " + countAccountsSigned +
                    " signers but at least one signer (the fee payer) is required");
        }
        if (countAccountsSignedReadOnly >= countAccountsSigned)
        {
            throw new IllegalStateException("message is malformed; header declares " + countAccountsSignedReadOnly +
                    " read-only signers but at least one signer (the fee payer) must be writable");
        }
        if (countAccountsSigned + countAccountsUnsignedReadOnly > numAddresses)
        {
            throw new IllegalStateException("message is malformed; header signer/read-only counts (" +
                    countAccountsSigned + " signed, " + countAccountsUnsignedReadOnly +
                    " unsigned read-only) exceed the " + numAddresses + " declared account addresses");
        }
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

    protected final boolean isWriterStaticAccount(final int index)
    {
        final var isSignerWriter = index < countAccountsSigned - countAccountsSignedReadOnly;
        final boolean isNonSigner = index >= countAccountsSigned;
        final boolean isNonSignerReadonly = index >= (accountsView.staticAccounts().size() - countAccountsUnsignedReadOnly);
        final var isNonSignerWriter = isNonSigner && !isNonSignerReadonly;

        return isSignerWriter || isNonSignerWriter;
    }

}
