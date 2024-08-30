package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressesFromLookup;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.MessageVisitor.MessageView;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

abstract class SolanaMessageView implements MessageView
{
    private final List<ByteBuffer> signatures;

    private final int countAccountsSigned;
    private final int countAccountsSignedReadOnly;
    private final int countAccountsUnsignedReadOnly;

    private final MessageVisitor.AccountsView accounts;
    private final PublicKey feePayer;
    private final Blockhash recentBlockHash;
    private final List<MessageVisitor.InstructionView> instructions;

    private final ByteBuffer transaction;

    SolanaMessageView(
            final int countAccountsSigned,
            final int countAccountsSignedReadOnly,
            final int countAccountsUnsignedReadOnly,
            final MessageVisitor.AccountsView accounts,
            final ByteBuffer transaction,
            final List<ByteBuffer> signatures,
            final PublicKey feePayer,
            final Blockhash recentBlockHash,
            final List<MessageVisitor.InstructionView> instructions)
    {
        this.signatures = signatures;
        this.countAccountsSigned = countAccountsSigned;
        this.countAccountsSignedReadOnly = countAccountsSignedReadOnly;
        this.countAccountsUnsignedReadOnly = countAccountsUnsignedReadOnly;
        this.accounts = accounts;
        this.feePayer = feePayer;
        this.recentBlockHash = recentBlockHash;
        this.instructions = instructions;
        this.transaction = transaction;
    }

    static MessageView fromBuffer(final ByteBuffer buffer)
    {
        final var formatter = new SolanaMessageFormattingCommon(buffer);

        final var signatures = formatter.readSignatures();
        final var transaction = buffer.slice();
        final var first = formatter.readByte();

        boolean v0Format = false;
        boolean legacyFormat = false;

        final int version;
        final int countAccountsSigned;

        if (first == (byte) 0x80)
        {
            v0Format = true;
            version = 0;
            countAccountsSigned = formatter.readByte() & 0xff;
        }
        else if ((first & 0x80) == 0x00)
        {
            legacyFormat = true;
            countAccountsSigned = first & 0xff;
        }
        else
        {
            throw new RuntimeException("unsupported message format");
        }

        final int countAccountsSignedReadOnly = formatter.readByte() & 0xff; //convert ignoring the sign bit
        final int countAccountsUnsignedReadOnly = formatter.readByte() & 0xff;

        final var staticAccounts = formatter.readStaticAccounts();

        // write recent-block-header or NONCE
        final var blockhash = formatter.readBlockHash();

        // write transaction instructions
        final var instructions = formatter.readInstructions();

        if (legacyFormat)
        {
            return new SolanaLegacyMessageView(
                    countAccountsSigned,
                    countAccountsSignedReadOnly,
                    countAccountsUnsignedReadOnly,
                    new SolanaAccountsView(staticAccounts, 0, 0),
                    transaction,
                    signatures,
                    staticAccounts.get(0),
                    blockhash,
                    instructions);
        }
        else if (v0Format)
        {
            final var accountLookupTables = formatter.readLookupAccounts();

            final Integer readWriteCount = accountLookupTables.stream().map(accountLookupTableView -> accountLookupTableView.readWriteTableIndexes().size()).reduce(0, Integer::sum);
            final Integer readOnlyCount = accountLookupTables.stream().map(accountLookupTableView -> accountLookupTableView.readOnlyTableIndexes().size()).reduce(0, Integer::sum);

            return new SolanaV0MessageView(
                    countAccountsSigned,
                    countAccountsSignedReadOnly,
                    countAccountsUnsignedReadOnly,
                    new SolanaAccountsView(staticAccounts, readWriteCount, readOnlyCount),
                    transaction,
                    signatures,
                    staticAccounts.get(0),
                    blockhash,
                    instructions,
                    accountLookupTables);
        }
        else
        {
            throw new RuntimeException("unsupported message format");
        }

    }

    @Override
    public int countAccountsSigned()
    {
        return countAccountsSigned;
    }

    @Override
    public int countAccountsSignedReadOnly()
    {
        return countAccountsSignedReadOnly;
    }

    @Override
    public int countAccountsUnsignedReadOnly()
    {
        return countAccountsUnsignedReadOnly;
    }

    @Override
    public MessageVisitor.AccountsView accounts()
    {
        return accounts;
    }

    @Override
    public ByteBuffer transaction()
    {
        return transaction.duplicate();
    }

    @Override
    public ByteBuffer signature(final PublicKey account)
    {
        int index = 0;
        for (final var signature : signatures)
        {
            if (accounts.staticAccounts().get(index).equals(account))
            {
                return signature;
            }
            index++;
        }
        throw new NoSuchElementException();
    }

    @Override
    public PublicKey feePayer()
    {
        return feePayer;
    }

    @Override
    public Blockhash recentBlockHash()
    {
        return recentBlockHash;
    }

    @Override
    public List<MessageVisitor.InstructionView> instructions()
    {
        return instructions;
    }

    @Override
    public boolean isSigner(final PublicKey account)
    {
        final var index = accounts.staticAccounts().indexOf(account);

        if (index == -1)
        {
            return false;
        }

        return index < countAccountsSigned;
    }

    @Override
    public boolean isWriter(final PublicKey account)
    {
        return isWriter(account, new SolanaAddressesFromLookup(Collections.emptyList(), Collections.emptyList()));
    }

    @Override
    public boolean isWriter(final PublicKey account, final AddressesFromLookup addressesFromLookup)
    {
        final var index = accounts.allAccounts(addressesFromLookup).indexOf(account);
        if (index == -1)
        {
            return false;
        }

        final var isSignerWriter = index < countAccountsSigned - countAccountsSignedReadOnly;
        final boolean isNonSigner = index >= countAccountsSigned;
        final boolean isNonSignerReadonly = index >= accounts.allAccounts(addressesFromLookup).size() - countAccountsUnsignedReadOnly;
        final var isNonSignerWriter = isNonSigner && !isNonSignerReadonly;

        return isSignerWriter || isNonSignerWriter;
    }

    @Override
    public List<PublicKey> signers()
    {
        return accounts.staticAccounts().subList(0, countAccountsSigned);
    }

}
