package com.lmax.solana4j.programs;

import org.junit.jupiter.api.BeforeEach;

public class V0EncodingIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void setMessageEncoding()
    {
        solana.setMessageEncoding("V0");
    }
}
