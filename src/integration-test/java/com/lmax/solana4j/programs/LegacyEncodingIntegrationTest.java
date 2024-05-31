package com.lmax.solana4j.programs;

import org.junit.jupiter.api.BeforeEach;

public class LegacyEncodingIntegrationTest extends IntegrationTestBase
{
    @BeforeEach
    void setMessageEncoding()
    {
        solana.setMessageEncoding("Legacy");
    }
}
