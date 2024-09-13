package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;

/**
 * Program for managing compute budgets on the Solana blockchain.
 */
public class ComputeBudgetProgram
{
    private static final byte[] COMPUTE_BUDGET_PROGRAM_ID = Base58.decode("ComputeBudget111111111111111111111111111111");
    /**
     * The public key for the compute budget program account.
     * <p>
     * This constant defines the public key associated with the Solana account for the compute budget program.
     * It is set to the value returned by {@link Solana#account(byte[])} using the {@link #COMPUTE_BUDGET_PROGRAM_ID}.
     * </p>
     */
    public static final PublicKey COMPUTE_BUDGET_PROGRAM_ACCOUNT = Solana.account(COMPUTE_BUDGET_PROGRAM_ID);
    /**
     * The instruction code for setting the compute unit limit.
     * <p>
     * This constant defines the instruction code used to set the compute unit limit in the compute budget program in Solana.
     * </p>
     */
    public static final int SET_COMPUTE_UNIT_LIMIT_INSTRUCTION = 2;
    /**
     * The instruction code for setting the compute unit price.
     * <p>
     * This constant defines the instruction code used to set the compute unit price in the compute budget program in Solana.
     * </p>
     */
    public static final int SET_COMPUTE_UNIT_PRICE_INSTRUCTION = 3;

    /**
     * Factory method for creating a new instance of {@code ComputeBudgetProgramFactory}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code ComputeBudgetProgramFactory}
     */
    public static ComputeBudgetProgramFactory factory(final TransactionBuilder tb)
    {
        return new ComputeBudgetProgramFactory(tb);
    }

    public static class ComputeBudgetProgramFactory
    {

        private final TransactionBuilder tb;

        ComputeBudgetProgramFactory(final TransactionBuilder tb)
        {
            this.tb = tb;
        }

        /**
         * Sets the compute unit limit.
         *
         * @param computeUnits the number of compute units to set as the limit
         * @return this {@code ComputeBudgetProgramFactory} instance
         */
        public ComputeBudgetProgramFactory setComputeUnitLimit(final int computeUnits)
        {
            tb.append(ComputeBudgetProgram.setComputeUnitLimit(computeUnits));
            return this;
        }

        /**
         * Sets the compute unit price.
         *
         * @param microLamports the price in microLamports per compute unit
         * @return this {@code ComputeBudgetProgramFactory} instance
         */
        public ComputeBudgetProgramFactory setComputeUnitPrice(final long microLamports)
        {
            tb.append(ComputeBudgetProgram.setComputeUnitPrice(microLamports));
            return this;
        }
    }

    /**
     * Sets the compute unit limit.
     *
     * @param computeUnits the number of compute units to set as the limit
     * @return this {@code com.lmax.solana4j.api.TransactionInstruction} instance
     */
    public static TransactionInstruction setComputeUnitLimit(final int computeUnits)
    {
        return Solana.instruction(ib -> ib
                .program(COMPUTE_BUDGET_PROGRAM_ACCOUNT)
                .data(5, bb -> bb
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) SET_COMPUTE_UNIT_LIMIT_INSTRUCTION)
                        .putInt(computeUnits))
        );
    }

    /**
     * Sets the compute unit price.
     *
     * @param microLamports the price in microLamports per compute unit
     * @return this {@code TransactionInstruction} instance
     */
    public static TransactionInstruction setComputeUnitPrice(final long microLamports)
    {
        return Solana.instruction(ib -> ib
                .program(COMPUTE_BUDGET_PROGRAM_ACCOUNT)
                .data(9, bb -> bb
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) SET_COMPUTE_UNIT_PRICE_INSTRUCTION)
                        .putLong(microLamports))
        );
    }
}
