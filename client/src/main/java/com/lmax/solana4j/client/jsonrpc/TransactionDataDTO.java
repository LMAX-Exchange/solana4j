package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lmax.solana4j.client.api.Message;
import com.lmax.solana4j.client.api.TransactionData;

import java.io.IOException;
import java.util.List;

@JsonDeserialize(using = TransactionDataDTO.TransactionDataDeserializer.class)
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
