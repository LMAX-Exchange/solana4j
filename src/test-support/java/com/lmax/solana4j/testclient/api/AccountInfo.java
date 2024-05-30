package com.lmax.solana4j.testclient.api;

import java.util.List;

public interface AccountInfo
{
    long getLamports();

    String getOwner();

    List<String> getData();

    boolean isExecutable();

    long getRentEpoch();
}
