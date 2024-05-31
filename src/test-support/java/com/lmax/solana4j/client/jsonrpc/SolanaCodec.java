package com.lmax.solana4j.client.jsonrpc;

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
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

final class SolanaCodec
{
    final AtomicLong requestId = new AtomicLong();
    final ObjectMapper mapper;

    SolanaCodec()
    {
        this.mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }

    String encodeRequest(final String method, final Object[] params) throws JsonProcessingException
    {
        final long id = requestId.incrementAndGet();

        if (method.isEmpty())
        {
            throw new IllegalArgumentException("Method is not set");
        }
        final ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put(SolanaRpcClient.JSONRPC, "2.0");
        requestNode.put(SolanaRpcClient.METHOD, method);
        requestNode.set(SolanaRpcClient.PARAMS, arrayParams(params));

        requestNode.put(SolanaRpcClient.ID, id);

        return mapper.writeValueAsString(requestNode);
    }

    <T> RpcWrapperDTO<T> decodeResponse(
            final InputStream inputStream,
            final TypeReference<RpcWrapperDTO<T>> type) throws IOException
    {
        final ObjectReader objectReader = mapper.readerFor(type);
        return objectReader.readValue(inputStream);
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