package com.lmax.solana4j.transaction;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.programs.ComputeBudgetProgram;

import java.nio.ByteBuffer;
import java.util.List;

public class V1TransactionBlobFactory extends ParameterizedTransactionBlobFactory
{
    private static final int DEFAULT_COMPUTE_UNIT_LIMIT = 1_400_000;
    private static final int DEFAULT_LOADED_ACCOUNTS_DATA_SIZE_LIMIT = 64 * 1024 * 1024;

    @Override
    protected int messageBufferSize()
    {
        return Solana.MAX_V1_MESSAGE_SIZE;
    }

    @Override
    protected void writeMessage(
            final ByteBuffer buffer,
            final Blockhash blockhash,
            final List<TransactionInstruction> instructions,
            final PublicKey payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        Solana.builder(buffer)
                .v1()
                .recent(blockhash)
                .prebuiltInstructions(instructions)
                .payer(payer)
                .computeUnitLimit(DEFAULT_COMPUTE_UNIT_LIMIT)
                .loadedAccountsDataSizeLimit(DEFAULT_LOADED_ACCOUNTS_DATA_SIZE_LIMIT)
                .seal()
                .unsigned()
                .build();
    }

    @Override
    public String setComputeUnits(final int computeUnitLimit, final long computeUnitPrice, final Blockhash blockhash, final PublicKey payer, final List<TestKeyPair> signers)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_V1_MESSAGE_SIZE);

        final var builder = Solana.builder(buffer)
                .v1()
                .recent(blockhash)
                .prebuiltInstructions(List.of(
                        ComputeBudgetProgram.setComputeUnitLimit(computeUnitLimit),
                        ComputeBudgetProgram.setComputeUnitPrice(computeUnitPrice)
                ))
                .payer(payer)
                .computeUnitLimit(computeUnitLimit)
                .loadedAccountsDataSizeLimit(DEFAULT_LOADED_ACCOUNTS_DATA_SIZE_LIMIT);

        if (computeUnitPrice > 0)
        {
            builder.priorityFee(((computeUnitPrice * computeUnitLimit) + 999_999L) / 1_000_000L);
        }

        builder.seal()
                .unsigned()
                .build();

        sign(buffer, signers);
        return base64Encode(buffer);
    }
}
