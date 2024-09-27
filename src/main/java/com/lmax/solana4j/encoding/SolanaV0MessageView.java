package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class SolanaV0MessageView extends SolanaMessageView implements MessageVisitor.Version0MessageView
{
    private final int version = 0;
    private final MessageVisitor.V0AccountsView accountsView;
    private final List<MessageVisitor.InstructionView> instructions;
    private final List<MessageVisitor.AccountLookupView> accountLookups;

    SolanaV0MessageView(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final MessageVisitor.V0AccountsView accountsView,
            final ByteBuffer transaction,
            final List<ByteBuffer> signatures,
            final PublicKey feePayer,
            final Blockhash recentBlockHash,
            final List<MessageVisitor.InstructionView> instructions,
            final List<MessageVisitor.AccountLookupView> accountLookups)
    {
        super(countAccountsSigned, countAccountsSignedReadOnly, countAccountsUnsignedReadOnly, accountsView, transaction, signatures, feePayer, recentBlockHash);
        this.accountsView = accountsView;
        this.instructions = instructions;
        this.accountLookups = accountLookups;
    }

    @Override
    public int version()
    {
        return version;
    }

    @Override
    public List<MessageVisitor.AccountLookupView> accountLookups()
    {
        return accountLookups;
    }

    @Override
    public List<PublicKey> allAccounts(final List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException
    {
        return accountsView.accounts(addressLookupTables);
    }

    @Override
    public boolean isWriter(final PublicKey account, final List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException
    {
        final var index = accountsView.accounts(addressLookupTables).indexOf(account);
        if (index == -1)
        {
            return false;
        }
        else if (index < accountsView.staticAccounts().size())
        {
            return isWriterStaticAccount(index);
        }
        else
        {
            return isWriterLookupAccount(account, addressLookupTables);
        }
    }

    @Override
    public List<MessageVisitor.V0InstructionView> instructions()
    {
        return instructions.stream()
                           .map(instructionView -> (MessageVisitor.V0InstructionView) new SolanaV0InstructionView(
                                   instructionView.programIndex(),
                                   instructionView.accountIndexes(),
                                   instructionView.data(),
                                   accountsView)
                           )
                           .collect(Collectors.toList());
    }

    private boolean isWriterStaticAccount(final int index)
    {
        final var signedWriterStaticAccountsCount = countAccountsSigned() - countAccountsSignedReadOnly();
        final var isSignerWriter = index < signedWriterStaticAccountsCount;
        final var isNonSignerWriter = (index >= countAccountsSigned() && (index < (accountsView.staticAccounts()
                                                                                                    .size() - countAccountsUnsignedReadOnly())));

        return isSignerWriter || isNonSignerWriter;
    }

    private boolean isWriterLookupAccount(final PublicKey account, final List<AddressLookupTable> addressLookupTables) throws IllegalArgumentException
    {
        for (final MessageVisitor.AccountLookupView accountLookup : accountLookups)
        {
            final Optional<AddressLookupTable> maybeAddressLookupTable = accountLookup.findAddressLookupTable(addressLookupTables);

            if (maybeAddressLookupTable.isEmpty())
            {
                throw new IllegalArgumentException(
                        "The address lookup tables provided do not contain an address" +
                                "lookup table present in the message.");
            }
            for (final int readWriteIndex : accountLookup.readWriteTableIndexes())
            {
                final PublicKey lookupAccount = maybeAddressLookupTable.get().getAddresses().get(readWriteIndex);
                if (lookupAccount.equals(account))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
