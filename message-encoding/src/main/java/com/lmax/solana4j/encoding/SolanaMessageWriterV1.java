package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.References;
import com.lmax.solana4j.api.TransactionInstruction;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

final class SolanaMessageWriterV1
{
    static final int CONFIG_PRIORITY_FEE = 0x3;
    static final int CONFIG_COMPUTE_UNIT_LIMIT = 0x4;
    static final int CONFIG_LOADED_ACCOUNTS_DATA_SIZE = 0x8;
    static final int CONFIG_REQUESTED_HEAP_SIZE = 0x10;

    private final SolanaBlockhash recentBlockHash;
    private final List<TransactionInstruction> instructions;
    private final Accounts accounts;
    private final int configMask;
    private final long priorityFee;
    private final int computeUnitLimit;
    private final int loadedAccountsDataSizeLimit;
    private final int requestedHeapSize;

    SolanaMessageWriterV1(
            final SolanaBlockhash recentBlockHash,
            final List<TransactionInstruction> instructions,
            final Accounts accounts,
            final int configMask,
            final long priorityFee,
            final int computeUnitLimit,
            final int loadedAccountsDataSizeLimit,
            final int requestedHeapSize)
    {
        this.recentBlockHash = recentBlockHash;
        this.instructions = instructions;
        this.accounts = accounts;
        this.configMask = configMask;
        this.priorityFee = priorityFee;
        this.computeUnitLimit = computeUnitLimit;
        this.loadedAccountsDataSizeLimit = loadedAccountsDataSizeLimit;
        this.requestedHeapSize = requestedHeapSize;
    }

    void write(final ByteBuffer buffer)
    {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // write V1 version prefix
        buffer.put((byte) 0x81);

        // write header
        buffer.put((byte) accounts.getCountSigned());
        buffer.put((byte) accounts.getCountSignedReadOnly());
        buffer.put((byte) accounts.getCountUnsignedReadOnly());

        // write config mask (u32 LE)
        buffer.putInt(configMask);

        // write recent blockhash
        recentBlockHash.write(buffer);

        // write num_instructions (fixed u8)
        buffer.put((byte) instructions.size());

        // write num_addresses (fixed u8)
        buffer.put((byte) accounts.getStaticAccounts().size());

        // write addresses (all inline, no ALTs)
        for (final var account : accounts.getStaticAccounts())
        {
            account.write(buffer);
        }

        // write config values (in bit order)
        if ((configMask & CONFIG_PRIORITY_FEE) == CONFIG_PRIORITY_FEE)
        {
            buffer.putLong(priorityFee);
        }
        if ((configMask & CONFIG_COMPUTE_UNIT_LIMIT) != 0)
        {
            buffer.putInt(computeUnitLimit);
        }
        if ((configMask & CONFIG_LOADED_ACCOUNTS_DATA_SIZE) != 0)
        {
            buffer.putInt(loadedAccountsDataSizeLimit);
        }
        if ((configMask & CONFIG_REQUESTED_HEAP_SIZE) != 0)
        {
            buffer.putInt(requestedHeapSize);
        }

        final References references = accounts.getFlattenedAccountList()::indexOf;

        // write instruction headers (N x 4 bytes: program_id_index u8, num_accounts u8, data_len u16 LE)
        for (final var instruction : instructions)
        {
            buffer.put((byte) references.indexOfAccount(instruction.program()));
            buffer.put((byte) instruction.accountReferences().size());
            buffer.putShort((short) instruction.datasize());
        }

        // write instruction payloads (per instruction: account indices u8, then data)
        for (final var instruction : instructions)
        {
            for (final var accref : instruction.accountReferences())
            {
                final int indexOfAccount = references.indexOfAccount(accref.account());
                if (indexOfAccount == -1)
                {
                    throw new RuntimeException("Should have found the account.");
                }
                buffer.put((byte) indexOfAccount);
            }
            instruction.data().accept(buffer);
        }

        // reserve signatures at the tail (no length prefix, count comes from header)
        for (int i = 0; i < accounts.getCountSigned(); i++)
        {
            buffer.put(new byte[64]);
        }
    }
}
