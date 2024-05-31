package com.lmax.solana4j.client.api;

import java.util.Map;

public interface AccountInfoParsedData
{
    long getLamports();

    String getOwner();

    Map<String, Object> getData();

    String getType();

    Map<String, Object> getInfo();

    Integer getDecimals();

    boolean isExecutable();

    long getRentEpoch();
}
