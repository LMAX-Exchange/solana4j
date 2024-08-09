package com.lmax.solana4j.transactionblobs;

import com.lmax.solana4j.ByteBufferPrimitiveArray;
import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.domain.TokenProgramFactory;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AssociatedTokenProgram;
import com.lmax.solana4j.util.BouncyCastleSigner;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lmax.solana4j.programs.SystemProgram.SYSTEM_PROGRAM_ACCOUNT;
import static com.lmax.solana4j.programs.SystemProgram.factory;

public class LegacyTransactionBlobFactory implements TransactionBlobFactory
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
                .legacy()
                .recent(blockhash)
                .instructions(builder -> factory(builder)
                        .transfer(
                                from,
                                to,
                                amount
                        ))
                .payer(payer)
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
                .legacy()
                .recent(blockhash)
                .instructions(builder -> tokenProgramFactory.factory(builder)
                        .transfer(
                                from,
                                to,
                                owner,
                                amount,
                                signers.stream().map(TestKeyPair::getSolana4jPublicKey).collect(Collectors.toList())
                        ))
                .payer(payer)
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
                .legacy()
                .recent(blockhash)
                .instructions(builder -> tokenProgramFactory.factory(builder)
                        .mintTo(
                                mint,
                                authority,
                                List.of(destination)
                        ))
                .payer(payer)
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
                .instructions(versionedTransactionBuilder -> factory(versionedTransactionBuilder)
                        .createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()))
                .instructions(builder -> tokenProgram.getFactory().factory(builder)
                        .initializeMint(
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
                .legacy()
                .recent(blockhash)
                .instructions(legacyTransactionBuilder -> factory(legacyTransactionBuilder)
                        .createAccount(
                                authority,
                                nonce,
                                rentExemption,
                                accountSpan,
                                SYSTEM_PROGRAM_ACCOUNT))
                .instructions(legacyTransactionBuilder -> factory(legacyTransactionBuilder).nonceInitialize(nonce, authority))
                .payer(payer)
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
                .legacy()
                .recent(blockhash)
                .instructions(legacyTransactionBuilder -> factory(legacyTransactionBuilder)
                        .createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()))
                .instructions(legacyTransactionBuilder -> tokenProgram.getFactory().factory(legacyTransactionBuilder)
                        .initializeAccount(
                                account,
                                mint,
                                owner))
                .payer(payer)
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
        throw new UnsupportedOperationException("This hasn't been implemented yet!!");
    }

    @Override
    public String createAddressLookupTable(
            final ProgramDerivedAddress programDerivedAddress,
            final PublicKey authority,
            final Slot recentSlot,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .legacy()
                .recent(blockhash)
                .instructions(builder -> AddressLookupTableProgram.factory(builder)
                        .createLookupTable(
                                programDerivedAddress,
                                authority,
                                payer,
                                recentSlot)
                )
                .payer(payer)
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
                .legacy()
                .recent(blockhash)
                .instructions(builder -> AddressLookupTableProgram.factory(builder)
                        .extendLookupTable(
                                lookupAddress,
                                authority,
                                payer,
                                addressesToAdd)
                )
                .payer(payer)
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
                .legacy()
                .recent(blockhash)
                .instructions(tb -> factory(tb)
                        .nonceAdvance(account, authority))
                .payer(payer)
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
    public String createAssociatedTokenAddress(
            final PublicKey owner,
            final ProgramDerivedAddress associatedTokenAddress,
            final Blockhash blockhash,
            final PublicKey mint,
            final boolean idempotent,
            final PublicKey tokenProgramId,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .legacy()
                .recent(blockhash)
                .instructions(tb -> AssociatedTokenProgram.factory(tb)
                        .createAssociatedToken(
                                associatedTokenAddress,
                                mint,
                                owner,
                                payer,
                                tokenProgramId,
                                idempotent))
                .payer(payer)
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
