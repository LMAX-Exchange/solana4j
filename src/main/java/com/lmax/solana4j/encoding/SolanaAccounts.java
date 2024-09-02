package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AddressLookupTableEntrys;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionInstruction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SolanaAccounts implements Accounts
{
    private final List<PublicKey> staticAccounts;
    private final List<AddressLookupTableEntrys> lookupAccounts;
    private final int countSigned;
    private final int countSignedReadOnly;
    private final int countUnsignedReadOnly;

    private SolanaAccounts(
            final List<PublicKey> staticAccounts,
            final List<AddressLookupTableEntrys> lookupAccounts,
            final int countSigned,
            final int countSignedReadOnly,
            final int countUnsignedReadOnly)
    {
        this.staticAccounts = staticAccounts;
        this.lookupAccounts = lookupAccounts;
        this.countSigned = countSigned;
        this.countSignedReadOnly = countSignedReadOnly;
        this.countUnsignedReadOnly = countUnsignedReadOnly;
    }

    @Override
    public List<PublicKey> getFlattenedAccountList()
    {
        return Stream.concat(staticAccounts.stream(), lookupAccounts.stream().flatMap(lookupTable -> lookupTable.getAddresses().stream())).collect(Collectors.toList());
    }

    @Override
    public List<PublicKey> getStaticAccounts()
    {
        return staticAccounts;
    }

    @Override
    public List<AddressLookupTableEntrys> getLookupAccounts()
    {
        return lookupAccounts;
    }

    @Override
    public int getCountSigned()
    {
        return countSigned;
    }

    @Override
    public int getCountSignedReadOnly()
    {
        return countSignedReadOnly;
    }

    @Override
    public int getCountUnsignedReadOnly()
    {
        return countUnsignedReadOnly;
    }

    static Accounts create(final List<TransactionInstruction> instructions, final PublicKey payer)
    {
        return create(instructions, payer, List.of());
    }

    static Accounts create(
            final List<TransactionInstruction> instructions,
            final PublicKey payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final var payerReference = new SolanaAccountReference(payer, true, true, false);
        final var allAccountReferences = mergeAccountReferences(instructions, payerReference);

        final var lookupAccounts = LookupAccounts.create(allAccountReferences, addressLookupTables);

        final var staticAccountReferences = calculateStaticAccountReferences(allAccountReferences, lookupAccounts.getAccountsInLookupTables());

        int countSigned = 0;
        int countSignedReadOnly = 0;
        int countUnsignedReadOnly = 0;
        for (final var accountReference : staticAccountReferences)
        {
            if (accountReference.isSigner())
            {
                countSigned += 1;
                if (!accountReference.isWriter())
                {
                    countSignedReadOnly += 1;
                }
            }
            else if (!accountReference.isWriter())
            {
                countUnsignedReadOnly += 1;
            }
        }

        countUnsignedReadOnly += lookupAccounts.countUnsignedReadOnly();

        return new SolanaAccounts(
                staticAccountReferences.stream().map(TransactionInstruction.AccountReference::account).collect(Collectors.toList()),
                lookupAccounts.getLookupTableEntrys(),
                countSigned,
                countSignedReadOnly,
                countUnsignedReadOnly
        );
    }

    private static List<TransactionInstruction.AccountReference> calculateStaticAccountReferences(
            final List<TransactionInstruction.AccountReference> allAccountReferences,
            final Set<PublicKey> accountsInLookup)
    {
        final LinkedHashMap<PublicKey, TransactionInstruction.AccountReference> staticAccountReferences = new LinkedHashMap<>();
        for (final var accountReference : allAccountReferences)
        {
            if (!accountsInLookup.contains(accountReference.account()))
            {
                if (staticAccountReferences.containsKey(accountReference.account()))
                {
                    staticAccountReferences.merge(accountReference.account(), accountReference, SolanaAccounts::merge);
                }
                else
                {
                    staticAccountReferences.put(accountReference.account(), accountReference);
                }
            }
        }

        return new ArrayList<>(staticAccountReferences.values());
    }

    private static List<TransactionInstruction.AccountReference> mergeAccountReferences(
            final List<TransactionInstruction> instructions,
            final TransactionInstruction.AccountReference payerReference)
    {
        final var cmp = Comparator
                .<TransactionInstruction.AccountReference, Integer>comparing(r1 -> r1.isSigner() ? -1 : 1)
                .thenComparing(r -> r.isWriter() ? -1 : 1);

        return Stream.concat(
                Stream.of(payerReference),
                instructions.stream()
                        .flatMap(instruction -> Stream.concat(
                                Stream.of(new SolanaAccountReference(instruction.program(), false, false, true)),
                                instruction.accountReferences().stream())
                        )
                        .sorted(cmp)).collect(Collectors.toList());
    }

    private static TransactionInstruction.AccountReference merge(
            final TransactionInstruction.AccountReference accountReference,
            final TransactionInstruction.AccountReference accountReference2)
    {
        return new SolanaAccountReference(
                accountReference.account(),
                accountReference.isSigner() || accountReference2.isSigner(),
                accountReference.isWriter() || accountReference2.isWriter(),
                accountReference.isExecutable() || accountReference2.isExecutable()
        );
    }
}
