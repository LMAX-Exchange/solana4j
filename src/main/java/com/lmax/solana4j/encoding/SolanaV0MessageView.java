package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;

final class SolanaV0MessageView extends SolanaMessageView implements MessageVisitor.Version0MessageView
{
    private final int version = 0;
    private final MessageVisitor.V0AccountsView accountsView;
    private final List<MessageVisitor.InstructionView> instructions;
    private final List<MessageVisitor.AddressLookupView> addressLookups;

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
            final List<MessageVisitor.AddressLookupView> addressLookups)
    {
        super(countAccountsSigned, countAccountsSignedReadOnly, countAccountsUnsignedReadOnly, accountsView, transaction, signatures, feePayer, recentBlockHash);
        this.accountsView = accountsView;
        this.instructions = instructions;
        this.addressLookups = addressLookups;
    }

    @Override
    public int version()
    {
        return version;
    }

    @Override
    public List<MessageVisitor.AddressLookupView> addressLookups()
    {
        return addressLookups;
    }

    @Override
    public List<PublicKey> allAccounts(final List<AddressLookupTable> addressLookupTables)
    {
        return accountsView.accounts(addressLookupTables);
    }

    @Override
    public boolean isWriter(final PublicKey account, final List<AddressLookupTable> addressLookupTables)
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
                .toList();
    }

    private boolean isWriterStaticAccount(final int index)
    {
        final var unsignedReadOnlyLookupAccountsCount = addressLookups
                .stream()
                .map(x -> x.readOnlyTableIndexes().size())
                .mapToInt(Integer::intValue)
                .sum();

        final var unsignedReadOnlyStaticAccountsCount = countAccountsUnsignedReadOnly() - unsignedReadOnlyLookupAccountsCount;
        final var signedWriterStaticAccountsCount = countAccountsSigned() - countAccountsSignedReadOnly();
        final var isSignerWriter = index < signedWriterStaticAccountsCount;
        final var isNonSignerWriter = (index > (countAccountsSigned() - 1) && (index < (accountsView.staticAccounts().size() - unsignedReadOnlyStaticAccountsCount)));

        return isSignerWriter || isNonSignerWriter;
    }

    private boolean isWriterLookupAccount(final PublicKey account, final List<AddressLookupTable> addressLookupTables)
    {
        for (final MessageVisitor.AddressLookupView addressLookup : addressLookups)
        {
            final AddressLookupTable addressLookupTable = findAddressLookupTable(addressLookup, addressLookupTables);

            for (final int readWriteIndex : addressLookup.readWriteTableIndexes())
            {
                final PublicKey lookupAccount = addressLookupTable.getAddresses().get(readWriteIndex);
                if (lookupAccount.equals(account))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private AddressLookupTable findAddressLookupTable(
            final MessageVisitor.AddressLookupView addressLookup,
            final List<AddressLookupTable> addressLookupTables)
    {
        return addressLookupTables
                .stream()
                .filter(addressLookupTable -> addressLookupTable.getLookupTableAddress().equals(addressLookup.lookupAccount()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The address lookup tables provided do not contain an address" +
                        "lookup table present in the message."));
    }
}
