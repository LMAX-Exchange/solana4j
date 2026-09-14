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

public class LegacyTransactionBlobFactory extends ParameterizedTransactionBlobFactory
{
    @Override
    protected int messageBufferSize()
    {
        return Solana.MAX_MESSAGE_SIZE;
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
                .legacy()
                .recent(blockhash)
                .prebuiltInstructions(instructions)
                .payer(payer)
                .seal()
                .unsigned()
                .build();
    }

    @Override
    public String setComputeUnits(
            final int computeUnitLimit,
            final long computeUnitPrice,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .legacy()
                .recent(blockhash)
                .prebuiltInstructions(List.of(
                        ComputeBudgetProgram.setComputeUnitLimit(computeUnitLimit),
                        ComputeBudgetProgram.setComputeUnitPrice(computeUnitPrice)
                ))
                .payer(payer)
                .seal()
                .unsigned()
                .build();

        sign(buffer, signers);
        return base64Encode(buffer);
    }
}
