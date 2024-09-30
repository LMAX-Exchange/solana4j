package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;

/**
 * Program for managing compute budgets on the Solana blockchain.
 * <p>
 * This program allows users to set limits on the number of compute units
 * available for transactions and to specify the price per compute unit.
 * </p>
 */
public final class ComputeBudgetProgram
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
     * Private constructor to prevent instantiation.
     */
    private ComputeBudgetProgram()
    {
    }

    /**
     * Factory method for creating a new instance of {@code ComputeBudgetProgramFactory}.
     * <p>
     * This factory helps in creating new instances of {@link ComputeBudgetProgram} for managing
     * compute budget configurations using a provided {@link TransactionBuilder}.
     * </p>
     *
     * @param tb the transaction builder
     * @return a new instance of {@code ComputeBudgetProgramFactory}
     */
    public static ComputeBudgetProgramFactory factory(final TransactionBuilder tb)
    {
        return new ComputeBudgetProgramFactory(tb);
    }

    /**
     * Inner factory class for building compute budget program instructions. using a {@link TransactionBuilder}.
     */
    public static final class ComputeBudgetProgramFactory
    {

        private final TransactionBuilder tb;

        /**
         * Private constructor to initialize the factory with the given transaction builder.
         *
         * @param tb the transaction builder
         */
        private ComputeBudgetProgramFactory(final TransactionBuilder tb)
        {
            this.tb = tb;
        }

        /**
         * Sets the compute unit limit.
         * <p>
         * This method generates a transaction instruction to set the maximum number of compute units that can be used.
         * </p>
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
         * <p>
         * This method generates a transaction instruction to set the price per compute unit, expressed in microLamports.
         * </p>
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
     * <p>
     * This method creates a {@link TransactionInstruction} for setting the compute unit limit in Solana.
     * The compute unit limit controls the maximum compute resource consumption allowed in a transaction.
     * </p>
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
     * <p>
     * This method creates a {@link TransactionInstruction} for setting the price per compute unit in Solana.
     * This price is specified in microLamports (one-millionth of a Lamport, the smallest unit of currency in Solana).
     * </p>
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
