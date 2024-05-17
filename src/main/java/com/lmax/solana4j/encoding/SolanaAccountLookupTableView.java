package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;

final class SolanaAccountLookupTableView implements MessageVisitor.AccountLookupTableView
{
    private final PublicKey lookupAccount;
    private final List<Integer> readWriteTableIndexes;
    private final List<Integer> readOnlyTableIndexes;

    public SolanaAccountLookupTableView(final PublicKey lookupAccount, final List<Integer> readWriteTableIndexes, final List<Integer> readOnlyTableIndexes)
    {
        this.lookupAccount = lookupAccount;
        this.readWriteTableIndexes = readWriteTableIndexes;
        this.readOnlyTableIndexes = readOnlyTableIndexes;
    }


    @Override
    public PublicKey lookupAccount()
    {
        return lookupAccount;
    }

    @Override
    public List<Integer> readWriteTableIndexes()
    {
        return readWriteTableIndexes;
    }

    @Override
    public List<Integer> readOnlyTableIndexes()
    {
        return readOnlyTableIndexes;
    }
}
