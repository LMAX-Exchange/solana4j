package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilderBase;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;

public class ComputeBudgetProgram
{
    private static final byte[] COMPUTE_BUDGET_PROGRAM_ID = Base58.decode("ComputeBudget111111111111111111111111111111");
    public static final PublicKey COMPUTE_BUDGET_PROGRAM_ACCOUNT = Solana.account(COMPUTE_BUDGET_PROGRAM_ID);

    private static final int SET_COMPUTE_UNIT_LIMIT_INSTRUCTION = 2;
    private static final int SET_COMPUTE_UNIT_PRICE_INSTRUCTION = 3;

    private final TransactionBuilderBase tb;

    public static ComputeBudgetProgram factory(final TransactionBuilderBase tb)
    {
        return new ComputeBudgetProgram(tb);
    }

    ComputeBudgetProgram(final TransactionBuilderBase tb)
    {
        this.tb = tb;
    }

    public ComputeBudgetProgram setComputeUnitLimit(final int computeUnits)
    {
        tb.append(ib -> ib
                .program(COMPUTE_BUDGET_PROGRAM_ACCOUNT)
                .data(5, bb -> bb
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) SET_COMPUTE_UNIT_LIMIT_INSTRUCTION)
                        .putInt(computeUnits))
        );
        return this;
    }

    public ComputeBudgetProgram setComputeUnitPrice(final long microLamports)
    {
        tb.append(ib -> ib
                .program(COMPUTE_BUDGET_PROGRAM_ACCOUNT)
                .data(9, bb -> bb
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) SET_COMPUTE_UNIT_PRICE_INSTRUCTION)
                        .putLong(microLamports))
        );
        return this;
    }
}
