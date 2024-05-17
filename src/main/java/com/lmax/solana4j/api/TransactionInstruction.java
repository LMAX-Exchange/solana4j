package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

public interface TransactionInstruction
{
    List<AccountReference> accountReferences();

    PublicKey program();

    int datasize();

    Consumer<ByteBuffer> data();

    interface AccountReference
    {
        PublicKey account();

        boolean isSigner();

        boolean isWriter();

        boolean isExecutable();
    }
}
