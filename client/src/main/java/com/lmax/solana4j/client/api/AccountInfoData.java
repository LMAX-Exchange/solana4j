package com.lmax.solana4j.client.api;

import java.util.List;
import java.util.Map;

public interface AccountInfoData
{
    List<String> getAccountInfoEncoded();

    AccountInfoParsedData getAccountInfoParsed();

    interface AccountInfoParsedData
    {
        String getProgram();

        int getSpace();

        Map<String, Object> getParsedData();
    }
}
