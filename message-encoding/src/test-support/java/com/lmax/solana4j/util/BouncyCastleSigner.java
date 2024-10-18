package com.lmax.solana4j.util;

import com.lmax.solana4j.api.ByteBufferSigner;
import org.bouncycastle.math.ec.rfc8032.Ed25519;

import java.nio.ByteBuffer;

public class BouncyCastleSigner implements ByteBufferSigner
{
    private final byte[] secretKey;

    public BouncyCastleSigner(final byte[] secretKey)
    {
        this.secretKey = secretKey;
    }

    @Override
    public void sign(final ByteBuffer transaction, final ByteBuffer signature)
    {
        Ed25519.sign(secretKey, 0,
                transaction.array(), transaction.arrayOffset() + transaction.position(), transaction.limit() - transaction.position(),
                signature.array(), signature.arrayOffset() + signature.position());
    }
}
