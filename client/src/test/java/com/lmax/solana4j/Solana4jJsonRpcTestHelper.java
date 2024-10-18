package com.lmax.solana4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Solana4jJsonRpcTestHelper
{
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final long SLOT = 1000L;
    public static final long LAMPORTS = 100000L;
    public static final String OWNER = "4Nd1m5FGEh7dEcmvqLkW1fGEi8fE5hWefXaQmK5s7i5i";
    public static final List<String> DATA = List.of("data", "base64");
    public static final String RENT_EPOCH = "245";
}
