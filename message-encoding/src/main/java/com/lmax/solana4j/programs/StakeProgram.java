package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.encoding.SysVar;

import java.nio.ByteOrder;

/**
 * Program for managing stake accounts on the blockchain.
 *
 * <p>
 * This class provides methods to create, delegate, split, merge, and manage stake accounts on the Solana blockchain.
 * </p>
 */
public final class StakeProgram
{
    private static final byte[] STAKE_PROGRAM_ID = SolanaEncoding.decodeBase58("Stake11111111111111111111111111111111111111");
    private static final byte[] STAKE_CONFIG_ID = SolanaEncoding.decodeBase58("StakeConfig11111111111111111111111111111111");

    /**
     * The public key for the stake program account.
     */
    public static final PublicKey STAKE_PROGRAM_ACCOUNT = Solana.account(STAKE_PROGRAM_ID);

    /**
     * The public key for the stake config account.
     */
    public static final PublicKey STAKE_CONFIG_ACCOUNT = Solana.account(STAKE_CONFIG_ID);

    /**
     * The maximum space for a stake account in bytes.
     */
    public static final int STAKE_ACCOUNT_SPACE = 200;

    /**
     * The instruction code for initializing a stake account.
     */
    public static final int INITIALIZE_INSTRUCTION = 0;

    /**
     * The instruction code for authorizing a stake account.
     */
    public static final int AUTHORIZE_INSTRUCTION = 1;

    /**
     * The instruction code for delegating a stake account.
     */
    public static final int DELEGATE_INSTRUCTION = 2;

    /**
     * The instruction code for splitting a stake account.
     */
    public static final int SPLIT_INSTRUCTION = 3;

    /**
     * The instruction code for withdrawing from a stake account.
     */
    public static final int WITHDRAW_INSTRUCTION = 4;

    /**
     * The instruction code for deactivating a stake account.
     */
    public static final int DEACTIVATE_INSTRUCTION = 5;

    /**
     * The instruction code for merging stake accounts.
     */
    public static final int MERGE_INSTRUCTION = 7;

    /**
     * The instruction code for authorizing with seed.
     */
    public static final int AUTHORIZE_WITH_SEED_INSTRUCTION = 8;

    /**
     * Stake authorization type.
     */
    public enum StakeAuthorizationType
    {
        /**
         * Staker authorization.
         */
        STAKER(0),

        /**
         * Withdrawer authorization.
         */
        WITHDRAWER(1);

        private final int value;

        StakeAuthorizationType(final int value)
        {
            this.value = value;
        }

        /**
         * Gets the integer value of the authorization type.
         *
         * @return the integer value
         */
        public int getValue()
        {
            return value;
        }
    }

    /**
     * Represents the authorized accounts for a stake account.
     */
    public static final class Authorized
    {
        private final PublicKey staker;
        private final PublicKey withdrawer;

        /**
         * Creates a new Authorized instance.
         *
         * @param staker     the public key of the staker
         * @param withdrawer the public key of the withdrawer
         */
        public Authorized(final PublicKey staker, final PublicKey withdrawer)
        {
            this.staker = staker;
            this.withdrawer = withdrawer;
        }

        /**
         * Gets the staker public key.
         *
         * @return the staker public key
         */
        public PublicKey getStaker()
        {
            return staker;
        }

        /**
         * Gets the withdrawer public key.
         *
         * @return the withdrawer public key
         */
        public PublicKey getWithdrawer()
        {
            return withdrawer;
        }
    }

    /**
     * Represents the lockup parameters for a stake account.
     */
    public static final class Lockup
    {
        private final long unixTimestamp;
        private final long epoch;
        private final PublicKey custodian;

        /**
         * Default lockup with no restrictions.
         */
        public static final Lockup DEFAULT = new Lockup(0, 0, Solana.account(new byte[32]));

        /**
         * Creates a new Lockup instance.
         *
         * @param unixTimestamp the unix timestamp when lockup expires
         * @param epoch         the epoch when lockup expires
         * @param custodian     the public key of the custodian
         */
        public Lockup(final long unixTimestamp, final long epoch, final PublicKey custodian)
        {
            this.unixTimestamp = unixTimestamp;
            this.epoch = epoch;
            this.custodian = custodian;
        }

        /**
         * Gets the unix timestamp.
         *
         * @return the unix timestamp
         */
        public long getUnixTimestamp()
        {
            return unixTimestamp;
        }

        /**
         * Gets the epoch.
         *
         * @return the epoch
         */
        public long getEpoch()
        {
            return epoch;
        }

        /**
         * Gets the custodian public key.
         *
         * @return the custodian public key
         */
        public PublicKey getCustodian()
        {
            return custodian;
        }
    }

    private StakeProgram()
    {
    }

    /**
     * Factory method for creating a new instance of {@code StakeProgramFactory}.
     *
     * @param tb the transaction builder
     * @return a new instance of {@code StakeProgramFactory}
     */
    public static StakeProgramFactory factory(final TransactionBuilder tb)
    {
        return new StakeProgramFactory(tb);
    }

    /**
     * Inner factory class for building stake program instructions using a {@link TransactionBuilder}.
     */
    public static final class StakeProgramFactory
    {
        private final TransactionBuilder tb;

        private StakeProgramFactory(final TransactionBuilder tb)
        {
            this.tb = tb;
        }

        /**
         * Creates and initializes a new stake account.
         *
         * @param payer      the public key of the payer
         * @param stake      the public key of the new stake account
         * @param authorized the authorized accounts
         * @param lockup     the lockup parameters
         * @param lamports   the number of lamports to transfer to the new stake account
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory createAccount(
                final PublicKey payer,
                final PublicKey stake,
                final Authorized authorized,
                final Lockup lockup,
                final long lamports)
        {
            tb.append(SystemProgram.createAccount(payer, stake, lamports, STAKE_ACCOUNT_SPACE, STAKE_PROGRAM_ACCOUNT));
            tb.append(StakeProgram.initialize(stake, authorized, lockup));
            return this;
        }

        /**
         * Initializes a stake account.
         *
         * @param stake      the public key of the stake account
         * @param authorized the authorized accounts
         * @param lockup     the lockup parameters
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory initialize(final PublicKey stake, final Authorized authorized, final Lockup lockup)
        {
            tb.append(StakeProgram.initialize(stake, authorized, lockup));
            return this;
        }

        /**
         * Delegates a stake account to a vote account.
         *
         * @param stake          the public key of the stake account
         * @param stakeAuthority the public key of the stake authority
         * @param voteAccount    the public key of the vote account to delegate to
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory delegate(final PublicKey stake, final PublicKey stakeAuthority, final PublicKey voteAccount)
        {
            tb.append(StakeProgram.delegate(stake, stakeAuthority, voteAccount));
            return this;
        }

        /**
         * Changes the authorization on a stake account.
         *
         * @param stake              the public key of the stake account
         * @param authority          the public key of the current authority
         * @param newAuthority       the public key of the new authority
         * @param stakeAuthorizationType the type of authorization to change
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory authorize(
                final PublicKey stake,
                final PublicKey authority,
                final PublicKey newAuthority,
                final StakeAuthorizationType stakeAuthorizationType)
        {
            tb.append(StakeProgram.authorize(stake, authority, newAuthority, stakeAuthorizationType));
            return this;
        }

        /**
         * Splits a stake account into two accounts.
         *
         * @param stake          the public key of the stake account to split
         * @param stakeAuthority the public key of the stake authority
         * @param lamports       the number of lamports to split to the new account
         * @param newStake       the public key of the new stake account
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory split(
                final PublicKey stake,
                final PublicKey stakeAuthority,
                final long lamports,
                final PublicKey newStake)
        {
            tb.append(StakeProgram.split(stake, stakeAuthority, lamports, newStake));
            return this;
        }

        /**
         * Withdraws from a stake account.
         *
         * @param stake               the public key of the stake account
         * @param withdrawAuthority   the public key of the withdraw authority
         * @param to                  the public key of the account to withdraw to
         * @param lamports            the number of lamports to withdraw
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory withdraw(
                final PublicKey stake,
                final PublicKey withdrawAuthority,
                final PublicKey to,
                final long lamports)
        {
            tb.append(StakeProgram.withdraw(stake, withdrawAuthority, to, lamports));
            return this;
        }

        /**
         * Deactivates a stake account.
         *
         * @param stake          the public key of the stake account
         * @param stakeAuthority the public key of the stake authority
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory deactivate(final PublicKey stake, final PublicKey stakeAuthority)
        {
            tb.append(StakeProgram.deactivate(stake, stakeAuthority));
            return this;
        }

        /**
         * Merges two stake accounts.
         *
         * @param destination    the public key of the destination stake account
         * @param source         the public key of the source stake account
         * @param stakeAuthority the public key of the stake authority
         * @return this {@code StakeProgramFactory} instance
         */
        public StakeProgramFactory merge(
                final PublicKey destination,
                final PublicKey source,
                final PublicKey stakeAuthority)
        {
            tb.append(StakeProgram.merge(destination, source, stakeAuthority));
            return this;
        }
    }

    /**
     * Initializes a stake account.
     *
     * @param stake      the public key of the stake account
     * @param authorized the authorized accounts
     * @param lockup     the lockup parameters
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction initialize(final PublicKey stake, final Authorized authorized, final Lockup lockup)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(stake, false, true)
                .account(SysVar.RENT, false, false)
                .data(116, bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(INITIALIZE_INSTRUCTION);
                    authorized.staker.write(bb);
                    authorized.withdrawer.write(bb);
                    bb.putLong(lockup.unixTimestamp)
                            .putLong(lockup.epoch);
                    lockup.custodian.write(bb);
                })
        );
    }

    /**
     * Delegates a stake account to a vote account.
     *
     * @param stake          the public key of the stake account
     * @param stakeAuthority the public key of the stake authority
     * @param voteAccount    the public key of the vote account to delegate to
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction delegate(final PublicKey stake, final PublicKey stakeAuthority, final PublicKey voteAccount)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(stake, false, true)
                .account(voteAccount, false, false)
                .account(SysVar.CLOCK, false, false)
                .account(SysVar.STAKE_HISTORY, false, false)
                .account(STAKE_CONFIG_ACCOUNT, false, false)
                .account(stakeAuthority, true, false)
                .data(4, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(DELEGATE_INSTRUCTION))
        );
    }

    /**
     * Changes the authorization on a stake account.
     *
     * @param stake              the public key of the stake account
     * @param authority          the public key of the current authority
     * @param newAuthority       the public key of the new authority
     * @param stakeAuthorizationType the type of authorization to change
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction authorize(
            final PublicKey stake,
            final PublicKey authority,
            final PublicKey newAuthority,
            final StakeAuthorizationType stakeAuthorizationType)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(stake, false, true)
                .account(SysVar.CLOCK, false, false)
                .account(authority, true, false)
                .data(40, bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                            .putInt(AUTHORIZE_INSTRUCTION);
                    newAuthority.write(bb);
                    bb.putInt(stakeAuthorizationType.getValue());
                })
        );
    }

    /**
     * Splits a stake account into two accounts.
     *
     * @param stake          the public key of the stake account to split
     * @param stakeAuthority the public key of the stake authority
     * @param lamports       the number of lamports to split to the new account
     * @param newStake       the public key of the new stake account
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction split(
            final PublicKey stake,
            final PublicKey stakeAuthority,
            final long lamports,
            final PublicKey newStake)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(stake, false, true)
                .account(newStake, false, true)
                .account(stakeAuthority, true, false)
                .data(12, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(SPLIT_INSTRUCTION)
                        .putLong(lamports))
        );
    }

    /**
     * Withdraws from a stake account.
     *
     * @param stake               the public key of the stake account
     * @param withdrawAuthority   the public key of the withdraw authority
     * @param to                  the public key of the account to withdraw to
     * @param lamports            the number of lamports to withdraw
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction withdraw(
            final PublicKey stake,
            final PublicKey withdrawAuthority,
            final PublicKey to,
            final long lamports)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(stake, false, true)
                .account(to, false, true)
                .account(SysVar.CLOCK, false, false)
                .account(SysVar.STAKE_HISTORY, false, false)
                .account(withdrawAuthority, true, false)
                .data(12, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(WITHDRAW_INSTRUCTION)
                        .putLong(lamports))
        );
    }

    /**
     * Deactivates a stake account.
     *
     * @param stake          the public key of the stake account
     * @param stakeAuthority the public key of the stake authority
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction deactivate(final PublicKey stake, final PublicKey stakeAuthority)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(stake, false, true)
                .account(SysVar.CLOCK, false, false)
                .account(stakeAuthority, true, false)
                .data(4, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(DEACTIVATE_INSTRUCTION))
        );
    }

    /**
     * Merges two stake accounts.
     *
     * @param destination    the public key of the destination stake account
     * @param source         the public key of the source stake account
     * @param stakeAuthority the public key of the stake authority
     * @return A {@code TransactionInstruction} of the created instruction
     */
    public static TransactionInstruction merge(
            final PublicKey destination,
            final PublicKey source,
            final PublicKey stakeAuthority)
    {
        return Solana.instruction(ib -> ib
                .program(STAKE_PROGRAM_ACCOUNT)
                .account(destination, false, true)
                .account(source, false, true)
                .account(SysVar.CLOCK, false, false)
                .account(SysVar.STAKE_HISTORY, false, false)
                .account(stakeAuthority, true, false)
                .data(4, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(MERGE_INSTRUCTION))
        );
    }
}
