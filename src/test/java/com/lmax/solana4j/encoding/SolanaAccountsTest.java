package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.AddressLookupTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LOOKUP_TABLE1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LOOKUP_TABLE2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LOOKUP_TABLE3;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.DATA2;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SolanaAccountsTest
{
    @Test
    void withNoLookupAccountsAllAccountsAreStatic()
    {
        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(new SolanaAccount(ACCOUNT1), true, true, false)),
                new SolanaAccount(PROGRAM1),
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                // no account table lookups
                List.of(),
                new SolanaAccountReference(new SolanaAccount(PAYER), true, true, false)
        );

        assertThat(accounts.getStaticAccounts().size()).isEqualTo(3);
        assertThat(accounts.getStaticAccounts())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new SolanaAccount(ACCOUNT1), new SolanaAccount(PROGRAM1), new SolanaAccount(PAYER)));
        assertThat(accounts.getLookupAccounts().size()).isEqualTo(0);
    }

    @Test
    void ifAccountSignerAlwaysStaticAccountEvenIfInLookupTable()
    {
        final AddressLookupTable addressLookupTable = new SolanaAddressLookupTable(
                new SolanaAccount(ACCOUNT_LOOKUP_TABLE1),
                List.of(new SolanaAccount(ACCOUNT1))
        );

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(new SolanaAccount(ACCOUNT1), true, true, false)),
                new SolanaAccount(PROGRAM1),
                10,
                w -> w.put(DATA1));


        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                List.of(addressLookupTable),
                new SolanaAccountReference(new SolanaAccount(PAYER), true, true, false)
        );

        assertThat(accounts.getStaticAccounts().size()).isEqualTo(3);
        assertThat(accounts.getStaticAccounts())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new SolanaAccount(ACCOUNT1), new SolanaAccount(PROGRAM1), new SolanaAccount(PAYER)));
        assertThat(accounts.getLookupAccounts().size()).isEqualTo(0);
    }

    @Test
    void ifAccountNotSignerAndInLookupTableThenNoLongerStaticAccountForReadWriteAccount()
    {
        final AddressLookupTable addressLookupTable1 = new SolanaAddressLookupTable(new SolanaAccount(ACCOUNT_LOOKUP_TABLE1), List.of(new SolanaAccount(ACCOUNT1)));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(new SolanaAccount(ACCOUNT1), false, true, false)
                ),
                new SolanaAccount(PROGRAM1),
                10,
                w -> w.put(DATA1));


        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                List.of(addressLookupTable1),
                new SolanaAccountReference(new SolanaAccount(PAYER), true, true, false)
        );

        assertThat(accounts.getStaticAccounts().size()).isEqualTo(2);
        assertThat(accounts.getStaticAccounts()).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(List.of(new SolanaAccount(PROGRAM1), new SolanaAccount(PAYER)));
        assertThat(accounts.getLookupAccounts().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getLookupTableAddress())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(new SolanaAccount(ACCOUNT_LOOKUP_TABLE1));
        assertThat(accounts.getLookupAccounts().get(0).getAddresses().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getAddresses().get(0)).isEqualTo(new SolanaAccount(ACCOUNT1));
        // ACCOUNT1 isWriter: true
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().size()).isEqualTo(0);
        assertThat(accounts.getLookupAccounts().get(0).getReadWriteAddressIndexes().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getReadWriteAddressIndexes().get(0).getAddress()).isEqualTo(new SolanaAccount(ACCOUNT1));
        assertThat(accounts.getLookupAccounts().get(0).getReadWriteAddressIndexes().get(0).getAddressIndex()).isEqualTo(0);
    }

    @Test
    void ifAccountNotSignerAndInLookupTableThenNoLongerStaticAccountForReadOnlyAccount()
    {
        final AddressLookupTable addressLookupTable1 = new SolanaAddressLookupTable(new SolanaAccount(ACCOUNT_LOOKUP_TABLE1), List.of(new SolanaAccount(ACCOUNT1)));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(new SolanaAccount(ACCOUNT1), false, false, false)
                ),
                new SolanaAccount(PROGRAM1),
                10,
                w -> w.put(DATA1));


        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                List.of(addressLookupTable1),
                new SolanaAccountReference(new SolanaAccount(PAYER), true, true, false)
        );

        assertThat(accounts.getStaticAccounts().size()).isEqualTo(2);
        assertThat(accounts.getStaticAccounts())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new SolanaAccount(PROGRAM1), new SolanaAccount(PAYER)));
        assertThat(accounts.getLookupAccounts().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getLookupTableAddress())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(new SolanaAccount(ACCOUNT_LOOKUP_TABLE1));
        assertThat(accounts.getLookupAccounts().get(0).getAddresses().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getAddresses().get(0)).isEqualTo(new SolanaAccount(ACCOUNT1));
        // ACCOUNT1 isWriter: false
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().get(0).getAddress()).isEqualTo(new SolanaAccount(ACCOUNT1));
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().get(0).getAddressIndex()).isEqualTo(0);
        assertThat(accounts.getLookupAccounts().get(0).getReadWriteAddressIndexes().size()).isEqualTo(0);
    }

    @Test
    void accountsAreDeduplicatedIfTheyAppearInManyTransactions()
    {
        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(new SolanaAccount(ACCOUNT1), true, true, false)),
                new SolanaAccount(PROGRAM1),
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(new SolanaAccount(ACCOUNT1), true, true, false)),
                new SolanaAccount(PROGRAM2),
                15,
                w -> w.put(DATA2));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                // no account table lookups
                List.of(),
                new SolanaAccountReference(new SolanaAccount(PAYER), true, true, false)
        );

        assertThat(accounts.getStaticAccounts().size()).isEqualTo(4);
        assertThat(accounts.getStaticAccounts())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(new SolanaAccount(ACCOUNT1), new SolanaAccount(PROGRAM2), new SolanaAccount(PROGRAM1), new SolanaAccount(PAYER)));
        assertThat(accounts.getLookupAccounts().size()).isEqualTo(0);
    }

    @Test
    void ifAccountExistsInManyLookupTablesSelectTheIndexOfTheFirstLookupTableItsFoundIn()
    {
        final AddressLookupTable addressLookupTable1 = new SolanaAddressLookupTable(
                new SolanaAccount(ACCOUNT_LOOKUP_TABLE1),
                List.of(new SolanaAccount(ACCOUNT2), new SolanaAccount(ACCOUNT3))
        );
        final AddressLookupTable addressLookupTable2 = new SolanaAddressLookupTable(
                new SolanaAccount(ACCOUNT_LOOKUP_TABLE2),
                List.of(new SolanaAccount(ACCOUNT4), new SolanaAccount(ACCOUNT1))
        );
        final AddressLookupTable addressLookupTable3 = new SolanaAddressLookupTable(
                new SolanaAccount(ACCOUNT_LOOKUP_TABLE3),
                List.of(new SolanaAccount(ACCOUNT1))
        );

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(new SolanaAccount(ACCOUNT1), false, false, false)),
                new SolanaAccount(PROGRAM1),
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1),
                List.of(addressLookupTable1, addressLookupTable2, addressLookupTable3),
                new SolanaAccountReference(new SolanaAccount(PAYER), true, true, false)
        );

        assertThat(accounts.getStaticAccounts().size()).isEqualTo(2);
        assertThat(accounts.getStaticAccounts()).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(List.of(new SolanaAccount(PROGRAM1), new SolanaAccount(PAYER)));
        assertThat(accounts.getLookupAccounts().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getLookupTableAddress())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(new SolanaAccount(ACCOUNT_LOOKUP_TABLE2));
        assertThat(accounts.getLookupAccounts().get(0).getAddresses().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getAddresses().get(0)).isEqualTo(new SolanaAccount(ACCOUNT1));
        // ACCOUNT1 isWriter: false
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().size()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().get(0).getAddress()).isEqualTo(new SolanaAccount(ACCOUNT1));
        assertThat(accounts.getLookupAccounts().get(0).getReadOnlyAddressIndexes().get(0).getAddressIndex()).isEqualTo(1);
        assertThat(accounts.getLookupAccounts().get(0).getReadWriteAddressIndexes().size()).isEqualTo(0);
    }
}