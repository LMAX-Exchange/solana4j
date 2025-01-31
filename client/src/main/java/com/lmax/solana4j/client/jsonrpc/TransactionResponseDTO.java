package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lmax.solana4j.client.api.TokenAmount;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;

final class TransactionResponseDTO implements TransactionResponse
{
    private final MetaDTO metaImpl;
    private final long slot;
    private final TransactionDataDTO transaction;
    private final Long blockTime;
    private final String version;

    @JsonCreator
    TransactionResponseDTO(
            final @JsonProperty("meta") MetaDTO metaImpl,
            final @JsonProperty("slot") long slot,
            final @JsonProperty("transaction") TransactionDataDTO transaction,
            final @JsonProperty("blockTime") Long blockTime,
            final @JsonProperty("version") String version)
    {
        this.metaImpl = metaImpl;
        this.slot = slot;
        this.transaction = transaction;
        this.blockTime = blockTime;
        this.version = version;
    }

    @Override
    @JsonProperty("meta")
    public TransactionMetadata getMetadata()
    {
        return metaImpl;
    }

    @Override
    public long getSlot()
    {
        return slot;
    }

    @Override
    public TransactionData getTransactionData()
    {
        return transaction;
    }

    @Override
    public Long getBlockTime()
    {
        return blockTime;
    }

    @Override
    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return "TransactionResponseDTO{" +
                "metaImpl=" + metaImpl +
                ", slot=" + slot +
                ", transaction=" + transaction +
                ", blockTime=" + blockTime +
                ", version='" + version + '\'' +
                '}';
    }

    static final class MetaDTO implements TransactionMetadata
    {
        public final Object err;
        public final long fee;
        public final List<InnerInstructionDTO> innerInstructions;
        public final List<String> logMessages;
        public final List<Long> postBalances;
        public final List<TokenBalanceDTO> postTokenBalances;
        public final List<Long> preBalances;
        public final List<TokenBalanceDTO> preTokenBalances;
        public final List<RewardDTO> rewards;
        public final long computeUnitsConsumed;
        private final LoadedAddressesDTO loadedAddresses;
        private final Map.Entry<String, Object> status;

        @JsonCreator
        MetaDTO(
                final @JsonProperty("err") Object err,
                final @JsonProperty("fee") long fee,
                final @JsonProperty("innerInstructions") List<InnerInstructionDTO> innerInstructions,
                final @JsonProperty("logMessages") List<String> logMessages,
                final @JsonProperty("postBalances") List<Long> postBalances,
                final @JsonProperty("postTokenBalances") List<TokenBalanceDTO> postTokenBalances,
                final @JsonProperty("preBalances") List<Long> preBalances,
                final @JsonProperty("preTokenBalances") List<TokenBalanceDTO> preTokenBalances,
                final @JsonProperty("rewards") List<RewardDTO> rewards,
                final @JsonProperty("computeUnitsConsumed") long computeUnitsConsumed,
                final @JsonProperty("loadedAddresses") LoadedAddressesDTO loadedAddresses,
                final @JsonProperty("status") Map.Entry<String, Object> status)
        {
            this.err = err;
            this.fee = fee;
            this.innerInstructions = innerInstructions;
            this.logMessages = logMessages;
            this.postBalances = postBalances;
            this.postTokenBalances = postTokenBalances;
            this.preBalances = preBalances;
            this.preTokenBalances = preTokenBalances;
            this.rewards = rewards;
            this.computeUnitsConsumed = computeUnitsConsumed;
            this.loadedAddresses = loadedAddresses;
            this.status = status;
        }

        @Override
        public Object getErr()
        {
            return err;
        }

        @Override
        public long getFee()
        {
            return fee;
        }

        @Override
        public List<InnerInstruction> getInnerInstructions()
        {
            return innerInstructions != null ?
                   innerInstructions.stream().map(x -> (InnerInstruction) x).collect(Collectors.toList()) :
                   Collections.emptyList();
        }

        @Override
        public List<String> getLogMessages()
        {
            return logMessages;
        }

        @Override
        public List<Long> getPostBalances()
        {
            return postBalances;
        }

        @Override
        public List<TokenBalance> getPostTokenBalances()
        {
            return new ArrayList<>(postTokenBalances);
        }

        @Override
        public List<Long> getPreBalances()
        {
            return preBalances;
        }

        @Override
        public List<TokenBalance> getPreTokenBalances()
        {
            return preTokenBalances.stream().map(x -> (TokenBalance) x).collect(Collectors.toList());
        }

        @Override
        public List<Reward> getRewards()
        {
            return rewards.stream().map(x -> (Reward) x).collect(Collectors.toList());
        }

        @Override
        public long getComputeUnitsConsumed()
        {
            return computeUnitsConsumed;
        }

        @Override
        public LoadedAddresses getLoadedAddresses()
        {
            return loadedAddresses;
        }

        @Override
        public Map.Entry<String, Object> getStatus()
        {
            return status;
        }


        @Override
        public String toString()
        {
            return "MetaDTO{" +
                    "err=" + err +
                    ", fee=" + fee +
                    ", innerInstructions=" + innerInstructions +
                    ", logMessages=" + logMessages +
                    ", postBalances=" + postBalances +
                    ", postTokenBalances=" + postTokenBalances +
                    ", preBalances=" + preBalances +
                    ", preTokenBalances=" + preTokenBalances +
                    ", rewards=" + rewards +
                    ", computeUnitsConsumed=" + computeUnitsConsumed +
                    ", loadedAddresses=" + loadedAddresses +
                    ", status=" + status +
                    '}';
        }

        static final class InnerInstructionDTO implements InnerInstruction
        {
            private final long index;
            private final List<MessageDTO.InstructionDTO> instructions;

            @JsonCreator
            InnerInstructionDTO(
                    final @JsonProperty("index") long index,
                    final @JsonProperty("instructions") List<MessageDTO.InstructionDTO> instructions)
            {
                this.index = index;
                this.instructions = unmodifiableList(instructions);
            }

            @Override
            public long getIndex()
            {
                return index;
            }

            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public List<Instruction> getInstructions()
            {
                return (List) instructions;
            }

            @Override
            public String toString()
            {
                return "InnerInstruction{" +
                       "index=" + index +
                       ", instructions=" + instructions +
                       '}';
            }
        }
    }

    static final class RewardDTO implements TransactionMetadata.Reward
    {
        private final String pubkey;
        private final long lamports;
        private final long postBalance;
        private final String rewardType;
        private final int commission;

        @JsonCreator
        RewardDTO(
                final @JsonProperty("pubkey") String pubkey,
                final @JsonProperty("lamports") long lamports,
                final @JsonProperty("postBalance") long postBalance,
                final @JsonProperty("rewardType") String rewardType,
                final @JsonProperty("commission") int commission)
        {
            this.pubkey = pubkey;
            this.lamports = lamports;
            this.postBalance = postBalance;
            this.rewardType = rewardType;
            this.commission = commission;
        }

        @Override
        public String getPubkey()
        {
            return pubkey;
        }

        @Override
        public long getLamports()
        {
            return lamports;
        }

        @Override
        public long getPostBalance()
        {
            return postBalance;
        }

        @Override
        public String getRewardType()
        {
            return rewardType;
        }

        @Override
        public int getCommission()
        {
            return commission;
        }

        @Override
        public String toString()
        {
            return "Reward{" +
                   "pubkey='" + pubkey + '\'' +
                   ", lamports=" + lamports +
                   ", postBalance=" + postBalance +
                   ", rewardType='" + rewardType + '\'' +
                   ", commission=" + commission +
                   '}';
        }
    }

    static final class TokenBalanceDTO implements TransactionMetadata.TokenBalance
    {
        private final int accountIndex;
        private final String mint;
        private final String owner;
        private final String programId;
        private final TokenAmountDTO.TokenAmountValueDTO uiTokenAmount;

        @JsonCreator
        TokenBalanceDTO(
                final @JsonProperty("accountIndex") int accountIndex,
                final @JsonProperty("mint") String mint,
                final @JsonProperty("owner") String owner,
                final @JsonProperty("programId") String programId,
                final @JsonProperty("uiTokenAmount") TokenAmountDTO.TokenAmountValueDTO uiTokenAmount)
        {
            this.accountIndex = accountIndex;
            this.mint = mint;
            this.owner = owner;
            this.programId = programId;
            this.uiTokenAmount = uiTokenAmount;
        }

        @Override
        public int getAccountIndex()
        {
            return accountIndex;
        }

        @Override
        public String getMint()
        {
            return mint;
        }

        @Override
        public String getOwner()
        {
            return owner;
        }

        @Override
        public String getProgramId()
        {
            return programId;
        }

        @Override
        public TokenAmount getUiTokenAmount()
        {
            return uiTokenAmount;
        }

        @Override
        public String toString()
        {
            return "TokenBalance{" +
                    "accountIndex=" + accountIndex +
                    ", mint='" + mint + '\'' +
                    ", owner='" + owner + '\'' +
                    ", programId='" + programId + '\'' +
                    ", uiTokenAmount=" + uiTokenAmount +
                    '}';
        }
    }

    @JsonDeserialize(using = TransactionDataDTO.TransactionDataDeserializer.class)
    static
    class TransactionDataDTO implements TransactionData
    {
        private final List<String> transactionDataEncoded;
        private final TransactionDataParsed transactionDataParsed;

        TransactionDataDTO(
                final List<String> transactionDataEncoded,
                final TransactionDataParsed transactionDataParsed)
        {
            this.transactionDataEncoded = transactionDataEncoded;
            this.transactionDataParsed = transactionDataParsed;
        }

        @Override
        public List<String> getEncodedTransactionData()
        {
            return transactionDataEncoded;
        }

        @Override
        public TransactionDataParsed getParsedTransactionData()
        {
            return transactionDataParsed;
        }

        private static final class TransactionDataParsedDTO implements TransactionDataParsed
        {
            private final MessageDTO message;
            private final List<String> signatures;

            @JsonCreator
            TransactionDataParsedDTO(
                    final @JsonProperty("message") MessageDTO message,
                    final @JsonProperty("signatures") List<String> signatures)
            {
                this.message = message;
                this.signatures = signatures;
            }

            @Override
            public Message getMessage()
            {
                return message;
            }

            @Override
            public List<String> getSignatures()
            {
                return signatures;
            }

            @Override
            public String toString()
            {
                return "TransactionData{" +
                        "message=" + message +
                        ", signatures=" + signatures +
                        '}';
            }
        }

        static class TransactionDataDeserializer extends JsonDeserializer<TransactionDataDTO>
        {
            @Override
            public TransactionDataDTO deserialize(final JsonParser parser, final DeserializationContext context) throws IOException
            {
                final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                final JsonNode node = mapper.readTree(parser);

                if (node.isArray())
                {
                    final List<String> transactionDataEncoded = mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    return new TransactionDataDTO(transactionDataEncoded, null);
                }
                else if (node.isObject())
                {
                    final TransactionDataParsed transactionDataParsed = mapper.convertValue(node, TransactionDataParsedDTO.class);
                    return new TransactionDataDTO(null, transactionDataParsed);
                }
                throw new IOException("Unable to deserialize Transaction Data.");
            }
        }
    }

    static final class MessageDTO implements Message
    {
        private final AccountKeysDTO accountKeys;
        private final HeaderDTO headerImpl;
        private final List<InstructionDTO> instructions;
        private final String recentBlockhash;

        @JsonCreator
        MessageDTO(
                final @JsonProperty("accountKeys") AccountKeysDTO accountKeys,
                final @JsonProperty("header") HeaderDTO headerImpl,
                final @JsonProperty("instructions") List<InstructionDTO> instructions,
                final @JsonProperty("recentBlockhash") String recentBlockhash)
        {
            this.accountKeys = accountKeys;
            this.headerImpl = headerImpl;
            this.instructions = instructions;
            this.recentBlockhash = recentBlockhash;
        }

        @Override
        public AccountKeys getAccountKeys()
        {
            return accountKeys;
        }

        @Override
        public Header getHeader()
        {
            return headerImpl;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public List<Instruction> getInstructions()
        {
            return (List) instructions;
        }

        @Override
        public String getRecentBlockhash()
        {
            return recentBlockhash;
        }

        @Override
        public String toString()
        {
            return "MessageDTO{" +
                    "accountKeys=" + accountKeys +
                    ", headerImpl=" + headerImpl +
                    ", instructions=" + instructions +
                    ", recentBlockhash='" + recentBlockhash + '\'' +
                    '}';
        }

        @JsonDeserialize(using = AccountKeysDTO.AccountKeyDeserializer.class)
        static final class AccountKeysDTO implements AccountKeys
        {
            private final List<String> accountKeysEncoded;
            private final List<AccountKeyParsed> accountKeysParsed;

            AccountKeysDTO(final List<String> accountKeysEncoded, final List<AccountKeyParsed> accountKeysParsed)
            {
                this.accountKeysEncoded = accountKeysEncoded;
                this.accountKeysParsed = accountKeysParsed;
            }

            @Override
            public List<AccountKeyParsed> getParsedAccountKeys()
            {
                return accountKeysParsed;
            }

            @Override
            public List<String> getEncodedAccountKeys()
            {
                return accountKeysEncoded;
            }

            static final class AccountKeyParsedDTO implements AccountKeyParsed
            {
                private final String key;
                private final KeySource source;
                private final boolean signer;
                private final boolean writable;

                @JsonCreator
                AccountKeyParsedDTO(
                        final @JsonProperty("pubkey") String key,
                        final @JsonProperty("source") KeySource source,
                        final @JsonProperty("signer") boolean signer,
                        final @JsonProperty("writable") boolean writable)
                {
                    this.key = key;
                    this.source = source;
                    this.signer = signer;
                    this.writable = writable;
                }

                @Override
                public String getKey()
                {
                    return key;
                }

                @Override
                public KeySource getSource()
                {
                    return source;
                }

                @Override
                public boolean isSigner()
                {
                    return signer;
                }

                @Override
                public boolean isWritable()
                {
                    return writable;
                }

                @Override
                public String toString()
                {
                    return "AccountKeyDTO{" +
                            "key='" + key + '\'' +
                            ", source=" + source +
                            ", signer=" + signer +
                            ", writable=" + writable +
                            '}';
                }
            }

            static class AccountKeyDeserializer extends JsonDeserializer<AccountKeysDTO>
            {
                @Override
                public AccountKeysDTO deserialize(final JsonParser parser, final DeserializationContext context) throws IOException
                {
                    final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                    final JsonNode node = mapper.readTree(parser);

                    if (node.isArray())
                    {
                        if (!node.isEmpty() && node.get(0).isTextual())
                        {
                            final List<String> accountKeysDecoded = mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                            return new AccountKeysDTO(accountKeysDecoded, null);
                        }
                        else
                        {
                            final List<AccountKeyParsed> accountKeysParsed = mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, AccountKeyParsedDTO.class));
                            return new AccountKeysDTO(null, accountKeysParsed);
                        }
                    }

                    throw new IOException("Unable to deserialize Account Key.");
                }
            }
        }

        static final class HeaderDTO implements Header
        {
            private final int numReadonlySignedAccounts;
            private final int numReadonlyUnsignedAccounts;
            private final int numRequiredSignatures;

            @JsonCreator
            HeaderDTO(
                    final @JsonProperty("numReadonlySignedAccounts") int numReadonlySignedAccounts,
                    final @JsonProperty("numReadonlyUnsignedAccounts") int numReadonlyUnsignedAccounts,
                    final @JsonProperty("numRequiredSignatures") int numRequiredSignatures)
            {
                this.numReadonlySignedAccounts = numReadonlySignedAccounts;
                this.numReadonlyUnsignedAccounts = numReadonlyUnsignedAccounts;
                this.numRequiredSignatures = numRequiredSignatures;
            }

            @Override
            public int getNumReadonlySignedAccounts()
            {
                return numReadonlySignedAccounts;
            }

            @Override
            public int getNumReadonlyUnsignedAccounts()
            {
                return numReadonlyUnsignedAccounts;
            }

            @Override
            public int getNumRequiredSignatures()
            {
                return numRequiredSignatures;
            }

            @Override
            public String toString()
            {
                return "Header{" +
                       "numReadonlySignedAccounts=" + numReadonlySignedAccounts +
                       ", numReadonlyUnsignedAccounts=" + numReadonlyUnsignedAccounts +
                       ", numRequiredSignatures=" + numRequiredSignatures +
                       '}';
            }
        }

        static final class InstructionDTO implements Instruction
        {
            private final InstructionAccountsDTO accounts;
            private final Map<String, Object> instructionParsed;
            private final String data;
            private final String program;
            private final String programId;
            private final Integer programIdIndex;
            private final Integer stackHeight;

            @JsonCreator
            InstructionDTO(
                    final @JsonProperty("accounts") InstructionAccountsDTO accounts,
                    final @JsonProperty("parsed") Map<String, Object> instructionParsed,
                    final @JsonProperty("data") String data,
                    final @JsonProperty("program") String program,
                    final @JsonProperty("programId") String programId,
                    final @JsonProperty("programIdIndex") Integer programIdIndex,
                    final @JsonProperty("stackHeight") Integer stackHeight)
            {
                this.accounts = accounts;
                this.instructionParsed = instructionParsed;
                this.data = data;
                this.program = program;
                this.programId = programId;
                this.programIdIndex = programIdIndex;
                this.stackHeight = stackHeight;
            }

            @Override
            public InstructionAccounts getAccounts()
            {
                return accounts;
            }

            @Override
            public String getData()
            {
                return data;
            }

            @Override
            public Integer getProgramIdIndex()
            {
                return programIdIndex;
            }

            @Override
            public String getProgram()
            {
                return program;
            }

            @Override
            public String getProgramId()
            {
                return programId;
            }

            @Override
            public Map<String, Object> getInstructionParsed()
            {
                return instructionParsed;
            }

            @Override
            public Integer getStackHeight()
            {
                return stackHeight;
            }

            @Override
            public String toString()
            {
                return "InstructionDTO{" +
                        "accounts=" + accounts +
                        ", instructionParsed=" + instructionParsed +
                        ", data='" + data + '\'' +
                        ", program='" + program + '\'' +
                        ", programId='" + programId + '\'' +
                        ", programIdIndex=" + programIdIndex +
                        ", stackHeight=" + stackHeight +
                        '}';
            }
        }


        @JsonDeserialize(using = InstructionAccountsDTO.InstructionAccountsDeserializer.class)
        static final class InstructionAccountsDTO implements Instruction.InstructionAccounts
        {
            private final List<Integer> indexes;
            private final List<String> addresses;

            InstructionAccountsDTO(final List<Integer> indexes, final List<String> addresses)
            {
                this.indexes = indexes;
                this.addresses = addresses;
            }

            @Override
            public List<Integer> getIndexes()
            {
                return indexes;
            }

            @Override
            public List<String> getAddresses()
            {
                return addresses;
            }

            static class InstructionAccountsDeserializer extends JsonDeserializer<InstructionAccountsDTO>
            {
                @Override
                public InstructionAccountsDTO deserialize(final JsonParser parser, final DeserializationContext context) throws IOException
                {
                    final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                    final JsonNode node = mapper.readTree(parser);

                    if (node.isArray())
                    {
                        if (!node.isEmpty() && node.get(0).isTextual())
                        {
                            final List<String> addresses = mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                            return new InstructionAccountsDTO(null, addresses);
                        }
                        else
                        {
                            final List<Integer> indexes = mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
                            return new InstructionAccountsDTO(indexes, null);
                        }
                    }

                    throw new IOException("Unable to deserialize instruction accounts.");
                }
            }
        }

    }

    static final class LoadedAddressesDTO implements TransactionMetadata.LoadedAddresses
    {
        private final List<String> readonly;
        private final List<String> writable;

        @JsonCreator
        LoadedAddressesDTO(final @JsonProperty("readonly") List<String> readonly, final @JsonProperty("writable") List<String> writable)
        {
            this.readonly = readonly;
            this.writable = writable;
        }

        @Override
        public List<String> getReadonly()
        {
            return readonly;
        }

        @Override
        public List<String> getWritable()
        {
            return writable;
        }
    }
}