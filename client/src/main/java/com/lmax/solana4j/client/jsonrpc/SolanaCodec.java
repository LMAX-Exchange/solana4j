package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

final class SolanaCodec
{
    static final String JSONRPC = "jsonrpc";
    static final String ID = "id";
    static final String METHOD = "method";
    static final String PARAMS = "params";

    final AtomicLong requestId = new AtomicLong();
    final ObjectMapper mapper;

    SolanaCodec(final boolean failOnUnknownProperties)
    {
        this.mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties)
                .build();
    }

    String encodeRequest(final String method, final Object[] params) throws JsonProcessingException
    {
        final long id = requestId.incrementAndGet();

        final ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put(JSONRPC, "2.0");
        requestNode.put(METHOD, method);
        requestNode.set(PARAMS, arrayParams(params));

        requestNode.put(ID, id);

        return mapper.writeValueAsString(requestNode);
    }

    <T> RpcWrapperDTO<T> decodeResponse(
            final byte[] bytes,
            final TypeReference<RpcWrapperDTO<T>> type) throws IOException
    {
        final ObjectReader objectReader = mapper.readerFor(type);
        return objectReader.readValue(bytes);
    }

    private ArrayNode arrayParams(final Object[] values)
    {
        final ArrayNode newArrayParams = mapper.createArrayNode();
        for (final Object value : values)
        {
            newArrayParams.add(mapper.valueToTree(value));
        }
        return newArrayParams;
    }
}