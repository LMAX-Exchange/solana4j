package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AddressLookupTableEntrys;
import com.lmax.solana4j.api.PublicKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lmax.solana4j.api.TransactionInstruction.AccountReference;

public class LookupAccounts
{
    private final List<AddressLookupTableEntrys> lookupTableEntries;
    private final Set<PublicKey> accountsInLookupTables;

    public LookupAccounts(
            final List<AddressLookupTableEntrys> lookupTableEntrys,
            final Set<PublicKey> accountsInLookupTables)
    {
        this.lookupTableEntries = lookupTableEntrys;
        this.accountsInLookupTables = accountsInLookupTables;
    }

    public static LookupAccounts create(
            final List<AccountReference> accountReferences,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Map<PublicKey, AddressLookupTableEntrys> accountLookupTableEntrys = new HashMap<>();
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

                        final AddressLookupTableEntrys accountLookupTableIndex = accountLookupTableEntrys.computeIfAbsent(
                                addressLookupTable.getLookupTableAddress(),
                                SolanaAddressLookupTableEntrys::new);

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
        return new LookupAccounts(new ArrayList<>(accountLookupTableEntrys.values()), addressesFoundInLookupTables);
    }

    public List<AddressLookupTableEntrys> getLookupTableEntries()
    {
        return lookupTableEntries;
    }

    public Set<PublicKey> getAccountsInLookupTables()
    {
        return accountsInLookupTables;
    }

    public int countUnsignedReadOnly()
    {
        return lookupTableEntries.stream().map(x -> x.getReadOnlyAddressEntrys().size()).mapToInt(Integer::intValue).sum();
    }
}
