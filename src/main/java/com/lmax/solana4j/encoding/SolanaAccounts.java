package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AddressLookupTableIndexes;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionInstruction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class SolanaAccounts implements Accounts
{
    private final List<PublicKey> staticAccounts;
    private final List<AddressLookupTableIndexes> addressLookupTableIndices;
    private final int countSigned;
    private final int countSignedReadOnly;
    private final int countUnsignedReadOnly;

    private SolanaAccounts(
            final List<PublicKey> staticAccounts,
            final List<AddressLookupTableIndexes> addressLookupTableIndices,
            final int countSigned,
            final int countSignedReadOnly,
            final int countUnsignedReadOnly)
    {
        this.staticAccounts = staticAccounts;
        this.addressLookupTableIndices = addressLookupTableIndices;
        this.countSigned = countSigned;
        this.countSignedReadOnly = countSignedReadOnly;
        this.countUnsignedReadOnly = countUnsignedReadOnly;
    }

    @Override
    public List<PublicKey> getFlattenedAccountList()
    {
        return Stream.concat(staticAccounts.stream(), addressLookupTableIndices.stream().flatMap(accountLookupIndex -> accountLookupIndex.getAddresses().stream())).collect(Collectors.toList());
    }

    @Override
    public List<PublicKey> getStaticAccounts()
    {
        return staticAccounts;
    }

    @Override
    public List<AddressLookupTableIndexes> getLookupAccounts()
    {
        return addressLookupTableIndices;
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

    public static Accounts create(final List<TransactionInstruction> instructions, final TransactionInstruction.AccountReference payerReference)
    {
        return create(instructions, List.of(), payerReference);
    }

    public static Accounts create(final List<TransactionInstruction> instructions, final List<AddressLookupTable> addressLookupTables, final TransactionInstruction.AccountReference payerReference)
    {
        final var instructionAccountReferences = getInstructionAccountReferences(instructions, payerReference);

        final Map<PublicKey, SolanaAddressLookupTableIndexes> accountLookupTableIndexes = new HashMap<>();
        final Set<PublicKey> accountsToDereference = new HashSet<>();
        for (final AddressLookupTable addressLookupTable : addressLookupTables)
        {
            final List<PublicKey> accountLookups = addressLookupTable.getAddressLookups();
            for (int i = 0; i < accountLookups.size(); i++)
            {
                final PublicKey account = accountLookups.get(i);
                final Optional<TransactionInstruction.AccountReference> maybeFoundAccountLookup = instructionAccountReferences.stream().filter(x -> x.account().equals(account)).findAny();
                if (maybeFoundAccountLookup.isPresent())
                {
                    // signers have to be static keys
                    if (!maybeFoundAccountLookup.get().isSigner())
                    {
                        accountsToDereference.add(account);
                        final AddressLookupTableIndexes accountLookupTableIndex = accountLookupTableIndexes.computeIfAbsent(
                                addressLookupTable.getLookupTableAddress(),
                                SolanaAddressLookupTableIndexes::new);

                        final TransactionInstruction.AccountReference accountReference = maybeFoundAccountLookup.get();
                        if (accountReference.isWriter())
                        {
                            accountLookupTableIndex.addReadWriteIndex(account, i);
                        }
                        else
                        {
                            accountLookupTableIndex.addReadOnlyIndex(account, i);
                        }
                    }
                }
            }
        }

        final var orderedDistinctAccountReferences = instructionAccountReferences
                .stream()
                .filter(account -> !accountsToDereference.contains(account.account()))
                .collect(Collectors.toMap(
                        TransactionInstruction.AccountReference::account,
                        Function.identity(),
                        SolanaAccounts::prepareHashMap,
                        LinkedHashMap::new));

        int countSigned = 0;
        int countSignedReadOnly = 0;
        int countUnsignedReadOnly = 0;
        for (final var accountReference : orderedDistinctAccountReferences.values())
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

        final List<PublicKey> staticAccounts = orderedDistinctAccountReferences.values()
                .stream()
                .map(TransactionInstruction.AccountReference::account)
                .collect(Collectors.toList());

        return new SolanaAccounts(staticAccounts, new ArrayList<>(accountLookupTableIndexes.values()), countSigned, countSignedReadOnly, countUnsignedReadOnly);
    }

    private static List<TransactionInstruction.AccountReference> getInstructionAccountReferences(
            final List<TransactionInstruction> instructions,
            final TransactionInstruction.AccountReference payerReference)
    {
        final Comparator<TransactionInstruction.AccountReference> comparator = Comparator.comparing(r -> r.isSigner() ? -1 : 1);

        final var cmp = comparator.thenComparing(r -> r.isWriter() ? -1 : 1);
        return Stream.concat(
                Stream.of(payerReference),
                instructions.stream()
                        .flatMap(instruction -> Stream.concat(
                                Stream.of(
                                        new SolanaAccountReference(instruction.program(), false, false, true)),
                                instruction.accountReferences().stream()))
                        .sorted(cmp)).collect(Collectors.toList());
    }

    private static TransactionInstruction.AccountReference prepareHashMap(
            final TransactionInstruction.AccountReference accountReference,
            final TransactionInstruction.AccountReference accountReference2)
    {
        return new SolanaAccountReference(
                accountReference.account(),
                accountReference.isSigner() || accountReference2.isSigner(),
                accountReference.isWriter() || accountReference2.isWriter(),
                accountReference.isExecutable() || accountReference2.isExecutable());
    }
}
