package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

final class SolanaV1MessageView extends SolanaMessageView implements MessageVisitor.Version1MessageView
{
    private final int version = 1;
    private final MessageVisitor.LegacyAccountsView accountsView;
    private final List<MessageVisitor.InstructionView> instructions;
    private final int configMask;
    private final long priorityFee;
    private final int computeUnitLimit;
    private final int loadedAccountsDataSizeLimit;
    private final int requestedHeapSize;

    SolanaV1MessageView(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final MessageVisitor.LegacyAccountsView accountsView,
            final ByteBuffer transaction,
            final List<ByteBuffer> signatures,
            final PublicKey feePayer,
            final Blockhash recentBlockHash,
            final List<MessageVisitor.InstructionView> instructions,
            final int configMask,
            final long priorityFee,
            final int computeUnitLimit,
            final int loadedAccountsDataSizeLimit,
            final int requestedHeapSize)
    {
        super(countAccountsSigned, countAccountsSignedReadOnly, countAccountsUnsignedReadOnly, accountsView, transaction, signatures, feePayer, recentBlockHash);
        this.accountsView = accountsView;
        this.instructions = instructions;
        this.configMask = configMask;
        this.priorityFee = priorityFee;
        this.computeUnitLimit = computeUnitLimit;
        this.loadedAccountsDataSizeLimit = loadedAccountsDataSizeLimit;
        this.requestedHeapSize = requestedHeapSize;
    }

    @Override
    public int version()
    {
        return version;
    }

    @Override
    public int configMask()
    {
        return configMask;
    }

    @Override
    public boolean hasPriorityFee()
    {
        return (configMask & SolanaMessageWriterV1.CONFIG_PRIORITY_FEE) == SolanaMessageWriterV1.CONFIG_PRIORITY_FEE;
    }

    @Override
    public long priorityFee()
    {
        if (!hasPriorityFee())
        {
            throw new IllegalStateException("No priority fee set in config");
        }
        return priorityFee;
    }

    @Override
    public boolean hasComputeUnitLimit()
    {
        return (configMask & SolanaMessageWriterV1.CONFIG_COMPUTE_UNIT_LIMIT) != 0;
    }

    @Override
    public int computeUnitLimit()
    {
        if (!hasComputeUnitLimit())
        {
            throw new IllegalStateException("No compute unit limit set in config");
        }
        return computeUnitLimit;
    }

    @Override
    public boolean hasLoadedAccountsDataSizeLimit()
    {
        return (configMask & SolanaMessageWriterV1.CONFIG_LOADED_ACCOUNTS_DATA_SIZE) != 0;
    }

    @Override
    public int loadedAccountsDataSizeLimit()
    {
        if (!hasLoadedAccountsDataSizeLimit())
        {
            throw new IllegalStateException("No loaded accounts data size limit set in config");
        }
        return loadedAccountsDataSizeLimit;
    }

    @Override
    public boolean hasRequestedHeapSize()
    {
        return (configMask & SolanaMessageWriterV1.CONFIG_REQUESTED_HEAP_SIZE) != 0;
    }

    @Override
    public int requestedHeapSize()
    {
        if (!hasRequestedHeapSize())
        {
            throw new IllegalStateException("No requested heap size set in config");
        }
        return requestedHeapSize;
    }

    @Override
    public List<MessageVisitor.LegacyInstructionView> instructions()
    {
        return instructions.stream()
                .map(instructionView ->
                        new SolanaLegacyInstructionView(
                                instructionView.programIndex(),
                                instructionView.accountIndexes(),
                                instructionView.data(),
                                accountsView)
                ).collect(Collectors.toList());
    }

    @Override
    public boolean isWriter(final PublicKey account)
    {
        final var index = accountsView.staticAccounts().indexOf(account);
        if (index == -1)
        {
            return false;
        }

        final var isSignerWriter = index < countAccountsSigned() - countAccountsSignedReadOnly();
        final boolean isNonSigner = index >= countAccountsSigned();
        final boolean isNonSignerReadonly = index >= (accountsView.staticAccounts().size() - countAccountsUnsignedReadOnly());
        final var isNonSignerWriter = isNonSigner && !isNonSignerReadonly;

        return isSignerWriter || isNonSignerWriter;
    }
}
