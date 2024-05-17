package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.PublicKey;

interface References
{
    int indexOfAccount(PublicKey account);
}
