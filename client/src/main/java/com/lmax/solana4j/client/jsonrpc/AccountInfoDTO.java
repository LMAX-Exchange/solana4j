package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.SolanaRpcResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

final class AccountInfoDTO implements SolanaRpcResponse<AccountInfo>
{
    private final ContextDTO context;
    private final AccountInfoValueDTO value;

    @JsonCreator
    AccountInfoDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") AccountInfoValueDTO value)
    {
        this.context = context;
        this.value = value;
    }

    @Override
    public Context getContext()
    {
        return context;
    }

    @Override
    public AccountInfo getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "AccountInfoDTO{" +
               "context=" + context +
               ", value=" + value +
               '}';
    }

    static final class AccountInfoValueDTO implements AccountInfo
    {
        private final long lamports;
        private final String owner;
        private final AccountInfoData data;
        private final boolean executable;
        private final String rentEpoch;
        private final int space;

        @JsonCreator
        AccountInfoValueDTO(
                final @JsonProperty("lamports") long lamports,
                final @JsonProperty("owner") String owner,
                final @JsonProperty("data") AccountInfoDataDTO data,
                final @JsonProperty("executable") boolean executable,
                final @JsonProperty("rentEpoch") String rentEpoch,
                final @JsonProperty("space") int space)
        {
            this.lamports = lamports;
            this.owner = owner;
            this.data = data;
            this.executable = executable;
            this.rentEpoch = rentEpoch;
            this.space = space;
        }

        @Override
        public long getLamports()
        {
            return lamports;
        }

        @Override
        public String getOwner()
        {
            return owner;
        }

        @Override
        public AccountInfoData getData()
        {
            return data;
        }

        @Override
        public boolean isExecutable()
        {
            return executable;
        }

        @Override
        public String getRentEpoch()
        {
            return rentEpoch;
        }

        @Override
        public long getSpace()
        {
            return space;
        }

        @Override
        public String toString()
        {
            return "AccountInfoValueDTO{" +
                    "lamports=" + lamports +
                    ", owner='" + owner + '\'' +
                    ", data=" + data +
                    ", executable=" + executable +
                    ", rentEpoch='" + rentEpoch + '\'' +
                    ", space=" + space +
                    '}';
        }
    }

    @JsonDeserialize(using = AccountInfoDataDTO.AccountInfoDataDeserializer.class)
    static final class AccountInfoDataDTO implements AccountInfo.AccountInfoData
    {
        private final List<String> accountInfoEncoded;
        private final AccountInfoParsedData accountInfoParsed;

        AccountInfoDataDTO(final List<String> accountInfoEncoded, final AccountInfoParsedDataDTO accountInfoParsed)
        {
            this.accountInfoEncoded = accountInfoEncoded;
            this.accountInfoParsed = accountInfoParsed;
        }

        @Override
        public List<String> getAccountInfoEncoded()
        {
            return accountInfoEncoded;
        }

        @Override
        public AccountInfoParsedData getAccountInfoParsed()
        {
            return accountInfoParsed;
        }

        public static class AccountInfoDataDeserializer extends JsonDeserializer<AccountInfo.AccountInfoData>
        {
            @Override
            public AccountInfo.AccountInfoData deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException
            {
                final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
                final JsonNode node = mapper.readTree(parser);

                if (node.isArray())
                {
                    final List<String> accountInfoEncoded = mapper.convertValue(node, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    return new AccountInfoDataDTO(accountInfoEncoded, null);
                }
                else if (node.isObject())
                {
                    final AccountInfoParsedDataDTO accountInfoParsedData = mapper.convertValue(node, AccountInfoParsedDataDTO.class);
                    return new AccountInfoDataDTO(null, accountInfoParsedData);
                }
                throw new IOException("Unable to deserialize Transaction Data.");
            }
        }

        static class AccountInfoParsedDataDTO implements AccountInfoParsedData
        {
            private final String program;
            private final int space;
            // encoding attempts to use program-specific state parsers - we do not know what these are upfront
            private final Map<String, Object> parsedData;

            AccountInfoParsedDataDTO(
                    final @JsonProperty("program") String program,
                    final @JsonProperty("space") int space,
                    final @JsonProperty("parsed") Map<String, Object> parsedData)
            {
                this.program = program;
                this.space = space;
                this.parsedData = parsedData;
            }

            @Override
            public String getProgram()
            {
                return program;
            }

            @Override
            public int getSpace()
            {
                return space;
            }

            @Override
            public Map<String, Object> getParsedData()
            {
                return parsedData;
            }
        }
    }
}
