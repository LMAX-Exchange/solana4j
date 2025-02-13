package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.nio.ByteOrder;

/**
 * Program for managing compute budgets on the blockchain.
 */
public final class ComputeBudgetProgram
{
    private static final byte[] COMPUTE_BUDGET_PROGRAM_ID = SolanaEncoding.decodeBase58("ComputeBudget111111111111111111111111111111");

    /**
     * The public key for the compute budget program account.
     */
    public static final PublicKey COMPUTE_BUDGET_PROGRAM_ACCOUNT = Solana.account(COMPUTE_BUDGET_PROGRAM_ID);

    /**
     * The instruction code for setting the compute unit limit.
     */
    public static final int SET_COMPUTE_UNIT_LIMIT_INSTRUCTION = 2;

    /**
     * The instruction code for setting the compute unit price.
     */
    public static final int SET_COMPUTE_UNIT_PRICE_INSTRUCTION = 3;

    private ComputeBudgetProgram()
    {
    }

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

    /**
     * Inner factory class for building compute budget program instructions using a {@link TransactionBuilder}.
     */
    public static final class ComputeBudgetProgramFactory
    {

        private final TransactionBuilder tb;

        private ComputeBudgetProgramFactory(final TransactionBuilder tb)
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
     * @return A {@code TransactionInstruction} of the created instruction
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
     * @return A {@code TransactionInstruction} of the created instruction
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
