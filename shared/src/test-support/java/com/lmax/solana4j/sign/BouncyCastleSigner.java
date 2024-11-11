package com.lmax.solana4j.sign;

import org.bouncycastle.math.ec.rfc8032.Ed25519;

import java.nio.ByteBuffer;

public class BouncyCastleSigner
{
    public static void sign(final byte[] secretKey, final ByteBuffer transaction, final ByteBuffer signature)
    {
        Ed25519.sign(secretKey, 0,
                transaction.array(), transaction.arrayOffset() + transaction.position(), transaction.limit() - transaction.position(),
                signature.array(), signature.arrayOffset() + signature.position());
    }
}
