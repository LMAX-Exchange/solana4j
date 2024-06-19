package com.lmax.solana4j.transaction;

import com.lmax.solana4j.ByteBufferPrimitiveArray;
import com.lmax.solana4j.Solana;
import com.lmax.solana4j.TokenProgram;
import com.lmax.solana4j.TokenProgramFactory;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.domain.BouncyCastleSigner;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.SystemProgram;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class V0TransactionFactory implements TransactionFactory
{

    @Override
    public String solTransfer(
            final PublicKey from,
            final PublicKey to,
            final long amount,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> SystemProgram.factory(builder).transfer(from, to, amount))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String tokenTransfer(
            final TokenProgramFactory tokenProgramFactory,
            final PublicKey from,
            final PublicKey to,
            final PublicKey owner,
            final long amount,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> tokenProgramFactory.factory(builder)
                        .createTransferInstruction(
                                from,
                                to,
                                owner,
                                amount,
                                signers.stream().map(TestKeyPair::getSolana4jPublicKey).collect(Collectors.toList())
                        ))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }

        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String mintTo(
            final TokenProgramFactory tokenProgramFactory,
            final PublicKey mint,
            final PublicKey authority,
            final Destination destination,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> tokenProgramFactory.factory(builder)
                        .createMintToInstruction(
                                mint,
                                authority,
                                destination
                        ))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }

        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String createAccount(
            final PublicKey account,
            final PublicKey tokenProgramAccount,
            final long rentExemption,
            final int accountSpan,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedTransactionBuilder -> SystemProgram.factory(versionedTransactionBuilder)
                        .createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgramAccount))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String createMintAccount(
            final TokenProgram tokenProgram,
            final PublicKey account,
            final int decimals,
            final PublicKey mintAuthority,
            final PublicKey freezeAuthority,
            final long rentExemption,
            final int accountSpan,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedTransactionBuilder -> SystemProgram.factory(versionedTransactionBuilder)
                        .createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()))
                .instructions(builder -> tokenProgram.getFactory().factory(builder)
                        .createInitializeMintAccountInstruction(
                                account,
                                (byte) decimals,
                                mintAuthority,
                                Optional.of(freezeAuthority)))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String createNonce(
            final PublicKey nonce,
            final PublicKey authority,
            final Blockhash blockhash,
            final long rentExemption,
            final int accountSpan,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(legacyTransactionBuilder -> SystemProgram.factory(legacyTransactionBuilder)
                        .createAccount(
                                payer,
                                nonce,
                                rentExemption,
                                accountSpan))
                .instructions(legacyTransactionBuilder -> SystemProgram.factory(legacyTransactionBuilder).nonceInitialize(nonce, authority))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String initializeTokenAccount(
            final TokenProgram tokenProgram,
            final long rentExemption,
            final int accountSpan,
            final PublicKey account,
            final PublicKey owner,
            final PublicKey mint,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(legacyTransactionBuilder -> SystemProgram.factory(legacyTransactionBuilder)
                        .createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()))
                .instructions(legacyTransactionBuilder -> tokenProgram.getFactory().factory(legacyTransactionBuilder)
                        .createInitializeTokenAccountInstruction(
                                account,
                                mint,
                                owner))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String initializeMultiSig(
            final TokenProgramFactory tokenProgramFactory,
            final PublicKey multisig,
            final int requiredSignatures,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        return null;
    }

    @Override
    public String createAddressLookupTable(
            final ProgramDerivedAddress programDerivedAddress,
            final PublicKey authority,
            final Slot slot,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> AddressLookupTableProgram.factory(builder)
                        .createAddressLookupTableInstruction(
                                programDerivedAddress,
                                authority,
                                payer,
                                slot)
                )
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base58encode(buffer);
    }

    @Override
    public String extendAddressLookupTable(
            final PublicKey lookupAddress,
            final PublicKey authority,
            final List<PublicKey> addressesToAdd,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(builder -> AddressLookupTableProgram.factory(builder)
                        .extendAddressLookupTable(
                                lookupAddress,
                                authority,
                                payer,
                                addressesToAdd)
                )
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }

        signedMessageBuilder.build();
        return base58encode(buffer);
    }

    @Override
    public String advanceNonce(
            final PublicKey account,
            final PublicKey authority,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(tb -> SystemProgram.factory(tb)
                        .nonceAdvance(account, authority))
                .payer(payer)
                .lookups(addressLookupTables)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }

        signedMessageBuilder.build();
        return base58encode(buffer);
    }

    private static String base58encode(final ByteBuffer bytes)
    {
        return Base58.encode(ByteBufferPrimitiveArray.copy(bytes));
    }
}
