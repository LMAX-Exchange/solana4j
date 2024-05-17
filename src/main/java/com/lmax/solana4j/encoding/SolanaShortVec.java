package com.lmax.solana4j.encoding;

import java.nio.ByteBuffer;

final class SolanaShortVec
{
    static void write(final long value, final ByteBuffer buffer)
    {
        if (value < 0)
        {
            throw new UnsupportedOperationException("negative integer encoding");
        }
        var tmpValue = value;

        while (tmpValue >= 0x80)
        {
            final byte b = (byte) ((tmpValue & 0x7f) | 0x80);
            tmpValue >>= 7;
            buffer.put(b);
        }
        buffer.put((byte) tmpValue);
    }

    static int readInt(final ByteBuffer buffer)
    {
        int result = 0;
        byte b = buffer.get();
        int bytes = 1;
        while ((b & 0x80) == 0x80)
        {
            result = result | ((b & 0x7f) << (7*(bytes-1)));
            b = buffer.get();
            bytes++;
        }
        result = result | (b << (7*(bytes-1)));
        return result;
    }

    static long readLong(final ByteBuffer buffer)
    {
        long result = 0;
        byte b = buffer.get();
        int bytes = 1;
        while ((b & 0x80) == 0x80)
        {
            result = result | (((long) (b & 0x7f)) << (7*(bytes-1)));
            b = buffer.get();
            bytes++;
        }
        result = result | (((long) b) << (7*(bytes-1)));
        return result;
    }
}
