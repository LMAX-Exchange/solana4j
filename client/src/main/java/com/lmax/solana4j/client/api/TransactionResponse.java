package com.lmax.solana4j.client.api;

import java.util.List;
import java.util.Map;

/**
 * Represents the response of a transaction on the blockchain.
 */
public interface TransactionResponse
{
    /**
     * Returns the slot in which the transaction was confirmed.
     *
     * @return the slot number where the transaction was confirmed
     */
    long getSlot();

    /**
     * Returns the block time when the transaction was confirmed.
     *
     * @return the block time as a Unix timestamp, or null if not available
     */
    Long getBlockTime();

    /**
     * Returns the version of the transaction.
     * The version represents the transaction version used when encoding the transaction data.
     *
     * @return the version of the transaction as a string
     */
    String getVersion();

    /**
     * Returns the core transaction data and its encoding.
     * The transaction data contains essential components such as instructions, signatures, and account keys
     * involved in the transaction. This data provides the fundamental elements that make up the transaction.
     *
     * @return the {@link TransactionData} object representing the transaction's data
     */
    TransactionData getTransactionData();

    /**
     * Returns the metadata associated with the transaction.
     * The metadata provides detailed information such as fees, balances, log messages, and other execution details
     * generated during the transaction's processing.
     *
     * @return the {@link TransactionMetadata} object representing the transaction's metadata
     */
    TransactionMetadata getMetadata();

    /**
     * Represents the encoded and parsed data associated with the transaction.
     */
    interface TransactionData
    {
        /**
         * Returns the parsed data of the transaction.
         * The parsed data provides structured access to essential elements of the transaction,
         * such as messages, account keys, and signatures.
         *
         * @return the {@link TransactionDataParsed} object representing the parsed transaction data
         */
        TransactionDataParsed getParsedTransactionData();

        /**
         * Returns the encoded transaction data.
         * The first element of the list contains the data and the second the encoding scheme used to encode the data.
         *
         * @return a list containing the encoded account information data and the encoding scheme
         */
        List<String> getEncodedTransactionData();

        /**
         * Represents the parsed data associated with the transaction.
         */
        interface TransactionDataParsed
        {
            /**
             * Returns the message associated with the transaction.
             * The message contains the instructions and related data that define the transaction's actions.
             *
             * @return the {@link Message} object representing the transaction's message
             */
            Message getMessage();

            /**
             * Returns the list of signatures for the transaction.
             * Each signature is a base58-encoded string. These signatures confirm that the transaction has
             * been authorized by the corresponding account holders.
             *
             * @return a list of base58-encoded strings representing the transaction's signatures
             */
            List<String> getSignatures();
        }
    }

    /**
     * Represents metadata associated with a transaction, including errors, fees, balances, log messages, rewards, and more.
     */
    interface TransactionMetadata
    {
        /**
         * Returns any error information associated with the transaction.
         * If the transaction was successful, this method returns null; otherwise, it returns an object representing the error.
         *
         * @return an object representing the error, or null if the transaction was successful
         */
        Object getErr();

        /**
         * Returns the fee paid for processing the transaction.
         *
         * @return the transaction fee in lamports
         */
        long getFee();

        /**
         * Returns a list of inner instructions executed within the transaction.
         * Inner instructions are additional program invocations that occur as part of the transaction.
         *
         * @return a list of {@link InnerInstruction} objects representing the inner instructions
         */
        List<InnerInstruction> getInnerInstructions();

        /**
         * Returns the log messages generated during the transaction.
         * These messages can provide insights into the execution of the transaction, including program-specific logging.
         *
         * @return a list of log messages as strings
         */
        List<String> getLogMessages();

        /**
         * Returns a list of balances before the transaction was processed.
         * Each balance corresponds to an account involved in the transaction, represented in lamports.
         *
         * @return a list of pre-transaction balances in lamports
         */
        List<Long> getPreBalances();

        /**
         * Returns a list of balances after the transaction was processed.
         * Each balance corresponds to an account involved in the transaction, represented in lamports.
         *
         * @return a list of post-transaction balances in lamports
         */
        List<Long> getPostBalances();

        /**
         * Returns a list of token balances before the transaction was processed.
         * This provides the pre-transaction spl token balances for accounts that hold tokens.
         *
         * @return a list of {@link TokenBalance} objects representing pre-transaction token balances
         */
        List<TokenBalance> getPreTokenBalances();

        /**
         * Returns a list of token balances after the transaction was processed.
         * This provides the post-transaction spl token balances for accounts that hold tokens.
         *
         * @return a list of {@link TokenBalance} objects representing post-transaction token balances
         */
        List<TokenBalance> getPostTokenBalances();

        /**
         * Returns a list of rewards distributed as part of the transaction.
         * Rewards may be given to validators or other participants in the network.
         *
         * @return a list of {@link Reward} objects representing the rewards distributed
         */
        List<Reward> getRewards();

        /**
         * Returns the number of compute units consumed during the transaction's execution.
         * Compute units represent the computational resources used to process the transaction.
         *
         * @return the number of compute units consumed
         */
        long getComputeUnitsConsumed();

        /**
         * Returns the addresses that were loaded during the transaction.
         * Loaded addresses may include writable or read-only addresses that provide data
         * or permissions necessary for the transaction's execution.
         *
         * @return the {@link LoadedAddresses} object representing loaded addresses
         */
        LoadedAddresses getLoadedAddresses();

        /**
         * Returns the transaction status, including the status type and message.
         *
         * @return a {@link Map.Entry} containing the status type as the key and the status message as the value
         */
        Map.Entry<String, Object> getStatus();

        /**
         * Represents the addresses loaded during a Solana transaction.
         */
        interface LoadedAddresses
        {

            /**
             * Returns a list of read-only addresses loaded during the transaction.
             * These addresses provide data necessary for the transaction's execution but cannot be modified.
             *
             * @return a list of base58-encoded strings representing read-only addresses
             */
            List<String> getReadonly();

            /**
             * Returns a list of writable addresses loaded during the transaction.
             * These addresses can be modified during the transaction's execution.
             *
             * @return a list of base58-encoded strings representing writable addresses
             */
            List<String> getWritable();
        }

        /**
         * Represents a reward given to a validator or account in a transaction, which includes details such as
         * the recipient's public key, the amount of lamports rewarded, the post-reward balance, the reward type,
         * and any commission involved.
         */
        interface Reward
        {

            /**
             * Returns the public key of the account or validator receiving the reward.
             * The public key is a base58-encoded string representing the recipient's account.
             *
             * @return the base58-encoded public key of the reward recipient
             */
            String getPubkey();

            /**
             * Returns the amount of lamports received as a reward.
             *
             * @return the amount of lamports rewarded
             */
            long getLamports();

            /**
             * Returns the post-reward balance of the account.
             * This is the account's balance after the reward has been applied.
             *
             * @return the post-reward balance in lamports
             */
            long getPostBalance();

            /**
             * Returns the type of the reward.
             * The reward type may represent different categories, such as staking rewards or inflation rewards.
             *
             * @return the string representing the type of the reward
             */
            String getRewardType();

            /**
             * Returns the commission percentage taken from the reward.
             * This applies to validator rewards, where a percentage of the reward is taken as commission.
             * If the commission is not applicable, this can return 0.
             *
             * @return the commission percentage, or 0 if no commission is applied
             */
            int getCommission();
        }

        /**
         * Represents the balance of an spl token in a transaction or account, including details about the token such
         * as the account index, the token mint, the owner of the account, the program managing the token,
         * and the user-friendly token amount.
         */
        interface TokenBalance
        {

            /**
             * Returns the index of the account in the transaction or account list.
             * The account index represents the position of the account within the transaction or account list.
             *
             * @return the index of the account holding the token balance
             */
            int getAccountIndex();

            /**
             * Returns the mint of the token.
             * The mint is the base58-encoded public key of the token, representing the token's issuance.
             *
             * @return the base58-encoded string representing the token mint
             */
            String getMint();

            /**
             * Returns the owner of the token account.
             * The owner is the base58-encoded public key of the account's owner.
             *
             * @return the base58-encoded string representing the owner of the token account
             */
            String getOwner();

            /**
             * Returns the program id managing the token.
             * The program id is the base58-encoded public key of the program responsible for managing the token,
             * typically the spl token program.
             *
             * @return the base58-encoded string representing the program id managing the token
             */
            String getProgramId();

            /**
             * Returns the user-friendly token amount.
             * This method provides a user-friendly, scaled version of the token balance using decimals for readability.
             *
             * @return the {@link TokenAmount} representing the token balance in a user-friendly format
             */
            TokenAmount getUiTokenAmount();
        }
    }

    /**
     * Represents a message in a transaction.
     * A message contains all the necessary data for the transaction, including the account keys,
     * instructions to be executed, and metadata such as the recent blockhash and address table lookups.
     */
    interface Message
    {

        /**
         * Returns a list of account keys used in the message.
         * Each account key is a base58-encoded string representing an account involved in the transaction defined by the message.
         * These account keys identify the accounts that participate in the transaction, either as read-only or writable accounts.
         *
         * @return an {@link AccountKeys} object containing the base58-encoded account keys involved in the transaction
         */
        AccountKeys getAccountKeys();

        /**
         * Returns the header of the message.
         * The header contains metadata about the transaction, such as the number of required
         * signatures and readonly accounts.
         *
         * @return the {@link Header} object representing the message header
         */
        Header getHeader();

        /**
         * Returns the list of instructions that are part of the message.
         * Each instruction defines an action to be executed as part of the transaction.
         *
         * @return a list of {@link Instruction} objects representing the message instructions
         */
        List<Instruction> getInstructions();

        /**
         * Returns the recent blockhash for the message.
         * The blockhash ensures the transaction is processed within a specific timeframe
         * and prevents it from being replayed.
         *
         * @return the base58-encoded string representing the recent blockhash
         */
        String getRecentBlockhash();

        /**
         * Represents a collection of account keys used in a transaction.
         * Account keys are public addresses associated with accounts involved in a transaction..
         */
        interface AccountKeys
        {

            /**
             * Returns a list of parsed account keys, each providing detailed information on
             * the account's role in the transaction, including its source, signing status, and write permissions.
             *
             * @return a list of {@link AccountKeyParsed} objects representing the parsed account keys
             */
            List<AccountKeyParsed> getParsedAccountKeys();

            /**
             * Returns a list of base58-encoded account keys involved in the transaction.
             * Each key is a compact string representing the public address of an account referenced in the transaction.
             *
             * @return a list of base58-encoded strings representing the account keys
             */
            List<String> getEncodedAccountKeys();

            /**
             * Represents an account key used in transactions.
             */
            interface AccountKeyParsed
            {

                /**
                 * Enum representing the source of the account key.
                 * The key can originate either from the transaction itself or from a lookup table.
                 */
                enum KeySource
                {
                    /** The key originates from the transaction. */
                    TRANSACTION,

                    /** The key originates from an address lookup table. */
                    LOOKUPTABLE
                }

                /**
                 * Returns the base58-encoded account key.
                 * The key is the public address of the account involved in the transaction.
                 *
                 * @return the base58-encoded string representing the account key
                 */
                String getKey();

                /**
                 * Returns the source of the account key.
                 * This can indicate whether the key was part of the transaction or fetched
                 * from an address lookup table.
                 *
                 * @return the source of the account key, either {@link KeySource#TRANSACTION} or {@link KeySource#LOOKUPTABLE}
                 */
                KeySource getSource();

                /**
                 * Indicates whether the account key is a signer.
                 * If true, the account has signed the transaction.
                 *
                 * @return true if the account is a signer, false otherwise
                 */
                boolean isSigner();

                /**
                 * Indicates whether the account associated with the key is writable.
                 * If true, the account can be modified during the transaction.
                 *
                 * @return true if the account is writable, false otherwise
                 */
                boolean isWritable();
            }
        }

        /**
         * Represents the header of a transaction on the blockchain.
         * The header contains metadata about the transaction, including information about
         * the number of required signatures, readonly accounts, and whether the accounts are signed.
         */
        interface Header
        {

            /**
             * Returns the number of readonly signed accounts in the transaction.
             * Readonly accounts cannot be modified during the transaction but must sign it.
             *
             * @return the number of readonly signed accounts
             */
            int getNumReadonlySignedAccounts();

            /**
             * Returns the number of readonly unsigned accounts in the transaction.
             * Readonly accounts cannot be modified during the transaction and do not need to sign it.
             *
             * @return the number of readonly unsigned accounts
             */
            int getNumReadonlyUnsignedAccounts();

            /**
             * Returns the number of required signatures for the transaction.
             * This represents the total number of accounts that must sign the transaction for it to be valid.
             *
             * @return the number of required signatures
             */
            int getNumRequiredSignatures();
        }
    }

    /**
     * Represents an inner instruction in a Solana transaction.
     * Inner instructions are instructions that are executed within a broader transaction and can involve program invocations.
     */
    interface InnerInstruction
    {

        /**
         * Returns the index of the inner instruction within the transaction.
         * The index indicates the position of this inner instruction in the list of instructions in the transaction.
         *
         * @return the index of the inner instruction
         */
        long getIndex();

        /**
         * Returns the list of instructions that are part of this inner instruction.
         * Each instruction represents an action or a program invocation that is part of the inner transaction.
         *
         * @return a list of {@link Instruction} objects representing the inner instructions
         */
        List<Instruction> getInstructions();
    }

    /**
     * Represents an instruction in a transaction.
     * Instructions define actions to be executed on the blockchain, specifying accounts involved,
     * the data for the instruction, and the program responsible for executing the instruction.
     *
     */
    interface Instruction
    {
        /**
         * Retrieves information about the accounts involved in this instruction.
         * Each account is referenced by its index within the transaction's account list
         * and is associated with its corresponding address.
         *
         * @return an {@link InstructionAccounts} instance containing the index and address of the affected account.
         */
        InstructionAccounts getAccounts();


        /**
         * Returns the base58-encoded data associated with the instruction.
         * This data contains the arguments or parameters needed for the execution of the instruction.
         *
         * @return the base58-encoded string representing the instruction data
         */
        String getData();

        /**
         * Returns the index of the program id that is responsible for executing the instruction.
         * The index refers to the program in the transaction's account list that defines the instruction's logic.
         *
         * @return the integer index of the program id
         */
        Integer getProgramIdIndex();

        /**
         * Returns the name or identifier of the program responsible for executing the instruction.
         * This program is associated with the program id and specifies the logic that the instruction will follow.
         *
         * @return a string representing the program's name or identifier
         */
        String getProgram();

        /**
         * Returns the program id associated with the instruction.
         * The program id is a unique base58-encoded string that identifies the Solana program
         * responsible for processing the instruction's actions on the blockchain.
         *
         * @return the base58-encoded string representing the program id
         */
        String getProgramId();


        /**
         * Returns a map representation of the parsed instruction data.
         *
         * @return a {@link Map} where keys are field names and values are the corresponding data
         */
        Map<String, Object> getInstructionParsed();

        /**
         * Returns the stack height at which this instruction operates, if applicable.
         *
         * @return the stack height as an {@link Integer}, or null if not specified
         */
        Integer getStackHeight();

        /**
         * Represents an account involved in an instruction within a transaction.
         */
        interface InstructionAccounts
        {
            /**
             * Retrieves the index of the account within the transaction's account list.
             *
             * @return the zero-based index of the account in the transaction's account list.
             */
            List<Integer> getIndexes();

            /**
             * Retrieves the address of the account involved in the instruction.
             * This is a base58-encoded string representing the address.
             *
             * @return the base58-encoded address as a string.
             */
            List<String> getAddresses();
        }

    }
}