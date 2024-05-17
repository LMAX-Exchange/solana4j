package com.lmax.solana4j.api;

import java.util.List;

public interface Accounts
{
    List<PublicKey> getFlattenedAccountList();

    List<PublicKey> getStaticAccounts();

    List<AddressLookupTableIndexes> getLookupAccounts();

    int getCountSigned();

    int getCountSignedReadOnly();

    int getCountUnsignedReadOnly();
}
