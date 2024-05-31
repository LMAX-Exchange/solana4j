package com.lmax.solana4j.api;

import java.nio.ByteBuffer;
import java.util.List;

public interface MessageVisitor<T>
{
    T visit(MessageView message);

    interface MessageView
    {
        int countAccountsSigned();

        int countAccountsSignedReadOnly();

        int countAccountsUnsignedReadOnly();

        AccountsView accounts();

        /**
         * @return buffer with position=0 limit=capacity=size-in-bytes
         */
        ByteBuffer transaction();

        /**
         * @param account used to sign the transaction
         * @return buffer with position=0 limit=capacity=size-in-bytes
         */
        ByteBuffer signature(PublicKey account);

        PublicKey feePayer();

        Blockhash recentBlockHash();

        List<InstructionView> instructions();

        boolean isSigner(PublicKey account);

        boolean isWriter(PublicKey account);

        boolean isWriter(PublicKey account, AddressesFromLookup addressesFromLookup);

        List<PublicKey> signers();
    }

    interface LegacyMessageView extends MessageView
    {
    }

    interface Version0MessageView extends LegacyMessageView
    {
        int version();

        List<AccountLookupTableView> lookupTables();
    }

    interface InstructionView
    {
        int programIndex();

        PublicKey program(List<PublicKey> transactionAccounts);

        List<Integer> accountIndexes();

        List<PublicKey> accounts(List<PublicKey> transactionAccounts);

        /**
         * @return buffer with position=0 limit=capacity=size-in-bytes
         */
        ByteBuffer data();
    }

    interface AccountLookupTableView
    {
        PublicKey lookupAccount();

        List<Integer> readWriteTableIndexes();

        List<Integer> readOnlyTableIndexes();

    }

    interface AccountsView
    {
        List<PublicKey> staticAccounts();

        List<PublicKey> allAccounts(AddressesFromLookup addressesFromLookup);

        List<PublicKey> allAccounts();
    }

}
