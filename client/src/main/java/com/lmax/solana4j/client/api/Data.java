package com.lmax.solana4j.client.api;

import java.util.List;

public interface Data
{
    String getProgramId();

    List<String> getData();
}
