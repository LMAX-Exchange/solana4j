package com.lmax.solana4j.transaction;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AssociatedTokenProgram;
import com.lmax.solana4j.programs.BpfLoaderUpgradeableProgram;
import com.lmax.solana4j.programs.ComputeBudgetProgram;
import com.lmax.solana4j.programs.TokenProgramBase;
import com.lmax.solana4j.sign.BouncyCastleSigner;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lmax.solana4j.programs.SystemProgram.SYSTEM_PROGRAM_ACCOUNT;
import static com.lmax.solana4j.programs.SystemProgram.factory;

// uses instructions() to build transaction
public class V0TransactionBlobFactory implements TransactionBlobFactory
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
                .instructions(versionedInstructionBuilder -> factory(versionedInstructionBuilder).transfer(from, to, amount))
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

        return base64encode(buffer);
    }

    @Override
    public String tokenTransfer(
            final TokenProgram tokenProgram,
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
                .instructions(versionedInstructionBuilder -> tokenProgram.getFactory().factory(versionedInstructionBuilder)
                        .transfer(
                                from,
                                to,
                                owner,
                                amount,
                                signers.stream().map(TestKeyPair::getSolana4jPublicKey).collect(Collectors.toList())))
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

        return base64encode(buffer);
    }

    @Override
    public String mintTo(
            final TokenProgram tokenProgram,
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
                .instructions(versionedInstructionBuilder -> tokenProgram.getFactory().factory(versionedInstructionBuilder)
                        .mintTo(
                                mint,
                                authority,
                                List.of(destination)))
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

        return base64encode(buffer);
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
                .instructions(versionedInstructionBuilder -> tokenProgram.getFactory().factory(versionedInstructionBuilder)
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

        return base64encode(buffer);
    }

    @Override
    public String createMultiSigAccount(
            final TokenProgram tokenProgram,
            final PublicKey account,
            final List<PublicKey> multiSigSigners,
            final int requiredSigners,
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
                .instructions(versionedInstructionBuilder -> tokenProgram.getFactory().factory(versionedInstructionBuilder)
                        .initializeMultisig(
                                account,
                                multiSigSigners,
                                requiredSigners))
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

        return base64encode(buffer);
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
                .instructions(versionedTransactionBuilder -> factory(versionedTransactionBuilder)
                        .createAccount(
                                payer,
                                nonce,
                                rentExemption,
                                accountSpan,
                                SYSTEM_PROGRAM_ACCOUNT))
                .instructions(vesionedTransactionBuilder -> factory(vesionedTransactionBuilder).nonceInitialize(nonce, authority))
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

        return base64encode(buffer);
    }

    @Override
    public String createTokenAccount(
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
                .instructions(versionedTransactionBuilder -> factory(versionedTransactionBuilder)
                        .createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()))
                .instructions(versionedTransactionBuilder -> tokenProgram.getFactory().factory(versionedTransactionBuilder)
                        .initializeAccount(
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

        return base64encode(buffer);
    }

    @Override
    public String createAddressLookupTable(
            final ProgramDerivedAddress programDerivedAddress,
            final PublicKey authority,
            final Slot slot,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedInstructionBuilder -> AddressLookupTableProgram.factory(versionedInstructionBuilder)
                        .createLookupTable(
                                programDerivedAddress,
                                authority,
                                payer,
                                slot)
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

        return base64encode(buffer);
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
                .instructions(versionedInstructionBuilder -> AddressLookupTableProgram.factory(versionedInstructionBuilder)
                        .extendLookupTable(
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
        return base64encode(buffer);
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
                .instructions(tb -> factory(tb).nonceAdvance(account, authority))
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
        return base64encode(buffer);
    }

    @Override
    public String createAssociatedTokenAccount(
            final TokenProgram tokenProgram,
            final PublicKey owner,
            final ProgramDerivedAddress associatedTokenAddress,
            final Blockhash blockhash,
            final PublicKey mint,
            final boolean idempotent,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedTransactionBuilder -> AssociatedTokenProgram.factory(versionedTransactionBuilder)
                        .createAssociatedTokenAccount(
                                associatedTokenAddress, mint, owner, payer,
                                tokenProgram.getProgram(),
                                idempotent))
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
        return base64encode(buffer);
    }

    @Override
    public String setTokenAccountAuthority(
            final TokenProgram tokenProgram,
            final PublicKey tokenAccount,
            final PublicKey tokenAccountOldAuthority,
            final PublicKey tokenAccountNewAuthority,
            final TokenProgramBase.AuthorityType authorityType,
            final Blockhash blockhash,
            final TestKeyPair payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedTransactionBuilder -> tokenProgram.getFactory().factory(versionedTransactionBuilder)
                        .setAuthority(
                                tokenAccount,
                                tokenAccountNewAuthority,
                                tokenAccountOldAuthority,
                                signers.stream().map(TestKeyPair::getSolana4jPublicKey).collect(Collectors.toList()),
                                authorityType))
                .lookups(addressLookupTables)
                .payer(payer.getSolana4jPublicKey())
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), new BouncyCastleSigner(signer.getPrivateKeyBytes()));
        }
        signedMessageBuilder.build();

        return base64encode(buffer);
    }

    @Override
    public String setComputeUnits(
            final int computeUnitLimit,
            final long computeUnitPrice,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedTransactionBuilder -> ComputeBudgetProgram.factory(versionedTransactionBuilder)
                        .setComputeUnitLimit(computeUnitLimit)
                        .setComputeUnitPrice(computeUnitPrice))
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

        return base64encode(buffer);
    }

    @Override
    public String setBpfUpgradeableProgramUpgradeAuthority(
            final PublicKey program,
            final PublicKey oldUpgradeAuthority,
            final PublicKey newUpgradeAuthority,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);

        Solana.builder(buffer)
                .v0()
                .recent(blockhash)
                .instructions(versionedTransactionBuilder -> BpfLoaderUpgradeableProgram.factory(versionedTransactionBuilder)
                        .setUpgradeAuthority(
                                program,
                                oldUpgradeAuthority,
                                Optional.of(newUpgradeAuthority))
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

        return base64encode(buffer);
    }

    private static String base64encode(final ByteBuffer bytes)
    {
        return Base64.getEncoder().encodeToString(ByteBufferPrimitiveArray.copy(bytes));
    }
}
