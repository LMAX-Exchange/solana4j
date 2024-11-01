package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lmax.solana4j.client.api.AccountKeys;

import java.io.IOException;
import java.util.List;

@JsonDeserialize(using = AccountKeysDTO.AccountKeyDeserializer.class)
public class AccountKeysDTO implements AccountKeys
{
    private final List<String> accountKeysEncoded;
    private final List<AccountKeyParsed> accountKeysParsed;

    public AccountKeysDTO(final List<String> accountKeysEncoded, final List<AccountKeyParsed> accountKeysParsed)
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

    public static class AccountKeyDeserializer extends JsonDeserializer<AccountKeysDTO>
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
