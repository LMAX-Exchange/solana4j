package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AccountLookupEntry;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lmax.solana4j.api.TransactionInstruction.AccountReference;

final class AccountLookups
{
    private final List<AccountLookupEntry> accountLookupEntries;
    private final Set<PublicKey> accountsInLookupTables;

    AccountLookups(
            final List<AccountLookupEntry> accountLookupEntries,
            final Set<PublicKey> accountsInLookupTables)
    {
        this.accountLookupEntries = accountLookupEntries;
        this.accountsInLookupTables = accountsInLookupTables;
    }

    public static AccountLookups create(
            final List<AccountReference> accountReferences,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Map<PublicKey, AccountLookupEntry> addressLookupTableEntrys = new HashMap<>();
        final Set<PublicKey> addressesFoundInLookupTables = new HashSet<>();

        for (final AccountReference accountReference : accountReferences)
        {
            for (final AddressLookupTable addressLookupTable : addressLookupTables)
            {
                if (addressesFoundInLookupTables.contains(accountReference.account()))
                {
                    break;
                }

                final List<PublicKey> addresses = addressLookupTable.getAddresses();
                for (int i = 0; i < addresses.size(); i++)
                {
                    final PublicKey addressInLookupTable = addresses.get(i);
                    if (addressInLookupTable.equals(accountReference.account()) && !accountReference.isSigner())
                    {

                        addressesFoundInLookupTables.add(addressInLookupTable);

                        final AccountLookupEntry accountLookupTableIndex = addressLookupTableEntrys.computeIfAbsent(
                                addressLookupTable.getLookupTableAddress(),
                                SolanaAccountLookupEntry::new);

                        if (accountReference.isWriter())
                        {
                            accountLookupTableIndex.addReadWriteEntry(accountReference.account(), i);
                        }
                        else
                        {
                            accountLookupTableIndex.addReadOnlyEntry(accountReference.account(), i);
                        }
                        break;
                    }
                }
            }
        }
        return new AccountLookups(new ArrayList<>(addressLookupTableEntrys.values()), addressesFoundInLookupTables);
    }

    public List<AccountLookupEntry> getAccountLookupEntrys()
    {
        return accountLookupEntries;
    }

    public Set<PublicKey> getAccountsInLookupTables()
    {
        return accountsInLookupTables;
    }
}
