package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lmax.solana4j.client.api.AccountInfoData;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@JsonDeserialize(using = AccountInfoDataDTO.AccountInfoDataDeserializer.class)
class AccountInfoDataDTO implements AccountInfoData
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

    public static class AccountInfoDataDeserializer extends JsonDeserializer<AccountInfoData>
    {
        @Override
        public AccountInfoData deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException
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
