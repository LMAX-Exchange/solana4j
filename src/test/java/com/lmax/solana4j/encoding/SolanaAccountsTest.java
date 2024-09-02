package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.Accounts;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AddressLookupTableEntrys;
import com.lmax.solana4j.api.PublicKey;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT4;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT5;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LOOKUP_TABLE1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT_LOOKUP_TABLE2;
import static com.lmax.solana4j.Solana4jTestHelper.DATA1;
import static com.lmax.solana4j.Solana4jTestHelper.PAYER;
import static com.lmax.solana4j.Solana4jTestHelper.PROGRAM1;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class SolanaAccountsTest
{
    public static final SolanaAccount RO_U_PROGRAM1_ACCOUNT = new SolanaAccount(PROGRAM1);
    public static final SolanaAccount RW_S_PAYER_ACCOUNT = new SolanaAccount(PAYER);

    @Test
    void payerAndProgramAccountsAlwaysStaticAccounts()
    {
        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                // no account references at all
                List.of(),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT));
    }

    @Test
    void unsignedReadOnlyAccountReferenceSortedAfterSignersAndWritersInStaticAccountList()
    {
        final SolanaAccount roUAccount = new SolanaAccount(ACCOUNT1);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roUAccount, false, false, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount));
    }

    @Test
    void unsignedReadOnlyAccountReferenceSortedInOrderTheyAreAppearWithinATransaction()
    {
        final SolanaAccount roUAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roUAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roUAccount2, false, false, false),
                        new SolanaAccountReference(roUAccount1, false, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount2, roUAccount1));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 3);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }

    @Test
    void unsignedReadOnlyAccountReferenceSortedInOrderTheyAreAppearAcrossMultipleTransactions()
    {
        final SolanaAccount roUAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roUAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roUAccount2, false, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roUAccount1, false, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount2, roUAccount1));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 3);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }


    @Test
    void unsignedReadWriteAccountReferenceSortedAfterSignersButBeforeReadOnlyAccountsInStaticAccountList()
    {
        final SolanaAccount rwUAccount = new SolanaAccount(ACCOUNT1);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(rwUAccount, false, true, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwUAccount, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }

    @Test
    void unsignedReadWriteAccountReferenceSortedInOrderTheyAreAppearWithinATransaction()
    {
        final SolanaAccount rwSAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount rwSAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(rwSAccount2, false, true, false),
                        new SolanaAccountReference(rwSAccount1, false, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwSAccount2, rwSAccount1, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }


    @Test
    void unsignedReadWriteAccountReferenceSortedInOrderTheyAreAppearAcrossMultipleTransactions()
    {
        final SolanaAccount rwUAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount rwUAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(rwUAccount2, false, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(rwUAccount1, false, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwUAccount2, rwUAccount1, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }

    @Test
    void signedReadOnlyAccountReferenceSortedAfterReadWriteSignersButBeforeNonSigners()
    {
        final SolanaAccount roSAccount = new SolanaAccount(ACCOUNT1);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roSAccount, true, false, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, roSAccount, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 1);
        countSignedEquals(accounts.getCountSigned(), 2);
    }

    @Test
    void signedReadOnlyAccountReferenceSortedInOrderTheyAreAppearWithinATransaction()
    {
        final SolanaAccount roSAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roSAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roSAccount2, true, false, false),
                        new SolanaAccountReference(roSAccount1, true, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, roSAccount2, roSAccount1, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 2);
        countSignedEquals(accounts.getCountSigned(), 3);
    }

    @Test
    void signedReadOnlyAccountReferenceSortedInOrderTheyAreAppearAcrossMultipleTransactions()
    {
        final SolanaAccount roSAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roSAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roSAccount2, true, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roSAccount1, true, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, roSAccount2, roSAccount1, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 2);
        countSignedEquals(accounts.getCountSigned(), 3);
    }

    @Test
    void signedReadWriteAccountReferenceSortedAfterPayerButBeforeEverythingElse()
    {
        final SolanaAccount rwSAccount = new SolanaAccount(ACCOUNT1);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(rwSAccount, true, true, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwSAccount, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 2);
    }

    @Test
    void signedReadWriteAccountReferenceSortedInOrderTheyAreAppearWithinATransaction()
    {
        final SolanaAccount rwSAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount rwSAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(rwSAccount2, true, true, false),
                        new SolanaAccountReference(rwSAccount1, true, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwSAccount2, rwSAccount1, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 3);
    }

    @Test
    void signedReadWriteAccountReferenceSortedInOrderTheyAreAppearAcrossMultipleTransactions()
    {
        final SolanaAccount rwSAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount rwSAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(rwSAccount2, true, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(rwSAccount1, true, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwSAccount2, rwSAccount1, RO_U_PROGRAM1_ACCOUNT));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 3);
    }

    @Test
    void accountReferencesSortedBySignersThenWritersThenOrderTheyAppear()
    {
        final SolanaAccount rwSAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount rwUAccount = new SolanaAccount(ACCOUNT2);
        final SolanaAccount roSAccount = new SolanaAccount(ACCOUNT3);
        final SolanaAccount roUAccount = new SolanaAccount(ACCOUNT4);
        final SolanaAccount rwSAccount2 = new SolanaAccount(ACCOUNT5);

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roUAccount, false, false, false),
                        new SolanaAccountReference(rwSAccount2, true, true, false),
                        new SolanaAccountReference(roSAccount, true, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(rwUAccount, false, true, false),
                        new SolanaAccountReference(rwSAccount1, true, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(
                RW_S_PAYER_ACCOUNT,
                rwSAccount2,
                rwSAccount1,
                roSAccount,
                rwUAccount,
                RO_U_PROGRAM1_ACCOUNT,
                roUAccount)
        );
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 2);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 1);
        countSignedEquals(accounts.getCountSigned(), 4);
    }

    @Test
    void staticAccountPresentManyTimesIsDeduplicated()
    {
        final SolanaAccount account = new SolanaAccount(ACCOUNT1);

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(account, true, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(account, false, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT
        );

        // ACCOUNT deduplicated
        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, account, RO_U_PROGRAM1_ACCOUNT));

        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        // ACCOUNT isSigner: true and isWriter: true
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 2);
    }

    @Test
    void evenIfSignerAccountInLookupTableWillAlwaysBeStaticAccount()
    {
        final SolanaAccount lookupTableAddress = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable = new SolanaAddressLookupTable(lookupTableAddress, List.of(RW_S_PAYER_ACCOUNT));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                // no account references at all
                List.of(),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress, List.of());
    }

    @Test
    void nonSignerInLookupTableNoLongerInStaticAccounts()
    {
        final SolanaAccount lookupTableAddress = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable = new SolanaAddressLookupTable(lookupTableAddress, List.of(RO_U_PROGRAM1_ACCOUNT));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                // no account references at all
                List.of(),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress, List.of(RO_U_PROGRAM1_ACCOUNT));
    }

    @Test
    void unsignedReadOnlyAccountReferenceInLookupTable()
    {
        final SolanaAccount roUAccount = new SolanaAccount(ACCOUNT1);

        final SolanaAccount lookupTableAddress = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable = new SolanaAddressLookupTable(lookupTableAddress, List.of(roUAccount));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roUAccount, false, false, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress, List.of(roUAccount));
        lookupAccountsReadOnlyIndexEqual(accounts.getLookupAccounts(), lookupTableAddress, roUAccount, 0);
        flattenedAccountListEquals(accounts.getFlattenedAccountList(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 2);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }

    @Test
    void unsignedReadWriteAccountReferenceInLookupTable()
    {
        final SolanaAccount roUAccount = new SolanaAccount(ACCOUNT1);

        final SolanaAccount lookupTableAddress = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable = new SolanaAddressLookupTable(lookupTableAddress, List.of(roUAccount));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roUAccount, false, true, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress, List.of(roUAccount));
        lookupAccountsReadWriteIndexEqual(accounts.getLookupAccounts(), lookupTableAddress, roUAccount, 0);
        flattenedAccountListEquals(accounts.getFlattenedAccountList(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount));
        assertCountUnsignedReadOnly(accounts.getCountUnsignedReadOnly(), 1);
        assertCountSignedReadOnly(accounts.getCountSignedReadOnly(), 0);
        countSignedEquals(accounts.getCountSigned(), 1);
    }

    @Test
    void findFirstOccurrenceOfAccountInLookupTableAndStopLooking()
    {
        final SolanaAccount roUAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roUAccount2 = new SolanaAccount(ACCOUNT2);
        final SolanaAccount roUAccount3 = new SolanaAccount(ACCOUNT2);

        final SolanaAccount lookupTableAddress1 = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable1 = new SolanaAddressLookupTable(lookupTableAddress1, List.of(roUAccount2, roUAccount3, roUAccount1));

        final SolanaAccount lookupTableAddress2 = new SolanaAccount(ACCOUNT_LOOKUP_TABLE2);
        final AddressLookupTable addressLookupTable2 = new SolanaAddressLookupTable(lookupTableAddress2, List.of(roUAccount1));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(new SolanaAccountReference(roUAccount1, false, false, false)),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable1, addressLookupTable2)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress1, List.of(roUAccount1));
        lookupAccountsReadOnlyIndexEqual(accounts.getLookupAccounts(), lookupTableAddress1, roUAccount1, 2);
        flattenedAccountListEquals(accounts.getFlattenedAccountList(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount1));
    }

    @Test
    void lookupAccountsSortedInOrderOfLookupTablesProvided()
    {
        final SolanaAccount roUAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roUAccount2 = new SolanaAccount(ACCOUNT2);

        final SolanaAccount lookupTableAddress1 = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable1 = new SolanaAddressLookupTable(lookupTableAddress1, List.of(roUAccount2));

        final SolanaAccount lookupTableAddress2 = new SolanaAccount(ACCOUNT_LOOKUP_TABLE2);
        final AddressLookupTable addressLookupTable2 = new SolanaAddressLookupTable(lookupTableAddress2, List.of(roUAccount1));

        final SolanaTransactionInstruction solanaTransactionInstruction = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roUAccount1, false, false, false),
                        new SolanaAccountReference(roUAccount2, false, false, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable1, addressLookupTable2)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress1, List.of(roUAccount2));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress2, List.of(roUAccount1));
        lookupAccountsReadOnlyIndexEqual(accounts.getLookupAccounts(), lookupTableAddress1, roUAccount2, 0);
        lookupAccountsReadOnlyIndexEqual(accounts.getLookupAccounts(), lookupTableAddress2, roUAccount1, 0);
        flattenedAccountListEquals(accounts.getFlattenedAccountList(), List.of(RW_S_PAYER_ACCOUNT, RO_U_PROGRAM1_ACCOUNT, roUAccount2, roUAccount1));
    }

    @Test
    void findManyAccountsInManyLookupTables()
    {
        final SolanaAccount roUAccount1 = new SolanaAccount(ACCOUNT1);
        final SolanaAccount roUAccount2 = new SolanaAccount(ACCOUNT2);
        final SolanaAccount rwSAccount3 = new SolanaAccount(ACCOUNT3);
        final SolanaAccount rwUAccount4 = new SolanaAccount(ACCOUNT4);

        final SolanaAccount lookupTableAddress1 = new SolanaAccount(ACCOUNT_LOOKUP_TABLE1);
        final AddressLookupTable addressLookupTable1 = new SolanaAddressLookupTable(lookupTableAddress1, List.of(rwSAccount3, roUAccount2));

        final SolanaAccount lookupTableAddress2 = new SolanaAccount(ACCOUNT_LOOKUP_TABLE2);
        final AddressLookupTable addressLookupTable2 = new SolanaAddressLookupTable(lookupTableAddress2, List.of(roUAccount1));

        final SolanaTransactionInstruction solanaTransactionInstruction1 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roUAccount1, false, true, false),
                        new SolanaAccountReference(rwSAccount3, true, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final SolanaTransactionInstruction solanaTransactionInstruction2 = new SolanaTransactionInstruction(
                List.of(
                        new SolanaAccountReference(roUAccount2, false, false, false),
                        new SolanaAccountReference(rwUAccount4, false, true, false)
                ),
                RO_U_PROGRAM1_ACCOUNT,
                10,
                w -> w.put(DATA1));

        final Accounts accounts = SolanaAccounts.create(
                List.of(solanaTransactionInstruction1, solanaTransactionInstruction2),
                RW_S_PAYER_ACCOUNT,
                List.of(addressLookupTable1, addressLookupTable2)
        );

        staticAccountsEqual(accounts.getStaticAccounts(), List.of(RW_S_PAYER_ACCOUNT, rwSAccount3, rwUAccount4, RO_U_PROGRAM1_ACCOUNT));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress1, List.of(roUAccount2));
        lookupAccountsEqual(accounts.getLookupAccounts(), lookupTableAddress2, List.of(roUAccount1));
        lookupAccountsReadOnlyIndexEqual(accounts.getLookupAccounts(), lookupTableAddress1, roUAccount2, 1);
        lookupAccountsReadWriteIndexEqual(accounts.getLookupAccounts(), lookupTableAddress2, roUAccount1, 0);
        flattenedAccountListEquals(accounts.getFlattenedAccountList(), List.of(
                RW_S_PAYER_ACCOUNT,
                rwSAccount3,
                rwUAccount4,
                RO_U_PROGRAM1_ACCOUNT,
                roUAccount2,
                roUAccount1)
        );
    }

    private void staticAccountsEqual(final List<PublicKey> staticAccounts, final List<PublicKey> expectedAccounts)
    {
        assertThat(staticAccounts).usingRecursiveComparison().isEqualTo(expectedAccounts);
    }

    private void lookupAccountsEqual(
            final List<AddressLookupTableEntrys> lookupAccounts,
            final PublicKey lookupTableAddress,
            final List<PublicKey> expectedAccounts)
    {
        final Optional<AddressLookupTableEntrys> maybeAddressLookups = lookupAccounts
                .stream()
                .filter(x -> x.getLookupTableAddress().equals(lookupTableAddress))
                .findFirst();

        if (maybeAddressLookups.isPresent())
        {
            if (expectedAccounts.isEmpty())
            {
                fail("Found address lookups but we were not expecting to.");
            }
            else
            {
                final AddressLookupTableEntrys addressLookups = maybeAddressLookups.get();
                assertThat(addressLookups.getAddresses()).usingRecursiveComparison().isEqualTo(expectedAccounts);
            }
        }
        else
        {
            if (!expectedAccounts.isEmpty())
            {
                fail("We expected to find lookup accounts but we did not find any at all.");
            }
        }
    }

    private void lookupAccountsReadOnlyIndexEqual(
            final List<AddressLookupTableEntrys> lookupAccounts,
            final SolanaAccount lookupTableAddress,
            final SolanaAccount lookupAccount,
            final int readOnlyIndex)
    {
        final AddressLookupTableEntrys addressLookups = lookupAccounts
                .stream()
                .filter(x -> x.getLookupTableAddress().equals(lookupTableAddress))
                .findFirst()
                .orElseThrow();

        for (final AddressLookupTableEntrys.LookupTableEntry entry : addressLookups.getReadOnlyAddressEntrys())
        {
            if (entry.getAddress().equals(lookupAccount))
            {
                assertThat(entry.getIndex()).isEqualTo(readOnlyIndex);
                return;
            }
        }
        fail();
    }

    private void lookupAccountsReadWriteIndexEqual(
            final List<AddressLookupTableEntrys> lookupAccounts,
            final SolanaAccount lookupTableAddress,
            final SolanaAccount lookupAccount,
            final int readWriteIndex)
    {
        final AddressLookupTableEntrys addressLookups = lookupAccounts
                .stream()
                .filter(x -> x.getLookupTableAddress().equals(lookupTableAddress))
                .findFirst()
                .orElseThrow();

        for (final AddressLookupTableEntrys.LookupTableEntry entry : addressLookups.getReadWriteAddressEntrys())
        {
            if (entry.getAddress().equals(lookupAccount))
            {
                assertThat(entry.getIndex()).isEqualTo(readWriteIndex);
                return;
            }
        }
        fail();
    }

    private void flattenedAccountListEquals(final List<PublicKey> flattenedAccounts, final List<PublicKey> expectedAccounts)
    {
        assertThat(flattenedAccounts).usingRecursiveComparison().isEqualTo(expectedAccounts);
    }

    private void assertCountUnsignedReadOnly(final int countUnsignedReadOnly, final int expectedCount)
    {
        assertThat(countUnsignedReadOnly).isEqualTo(expectedCount);
    }

    private void assertCountSignedReadOnly(final int countSignedReadOnly, final int expectedCount)
    {
        assertThat(countSignedReadOnly).isEqualTo(expectedCount);
    }

    private void countSignedEquals(final int countSigned, final int expectedCount)
    {
        assertThat(countSigned).isEqualTo(expectedCount);
    }
}