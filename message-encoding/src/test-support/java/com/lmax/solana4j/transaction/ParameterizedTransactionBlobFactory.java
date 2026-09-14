package com.lmax.solana4j.transaction;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.api.TransactionInstruction;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AssociatedTokenProgram;
import com.lmax.solana4j.programs.BpfLoaderUpgradeableProgram;
import com.lmax.solana4j.programs.StakeProgram;
import com.lmax.solana4j.programs.SystemProgram;
import com.lmax.solana4j.programs.TokenProgramBase;
import com.lmax.solana4j.sign.BouncyCastleSigner;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lmax.solana4j.programs.SystemProgram.SYSTEM_PROGRAM_ACCOUNT;

/**
 * Base {@link TransactionBlobFactory} that encodes transactions in a format agnostic manner.
 * The message format specific parts of the encoding are supplied by subclasses via
 * {@link #messageBufferSize()} and {@link #writeMessage(ByteBuffer, Blockhash, List, PublicKey, List)}.
 * The {@code setComputeUnits} operation differs more substantially between formats and is
 * therefore left to subclasses to implement.
 */
public abstract class ParameterizedTransactionBlobFactory implements TransactionBlobFactory
{
    protected abstract int messageBufferSize();

    protected abstract void writeMessage(
            ByteBuffer buffer,
            Blockhash blockhash,
            List<TransactionInstruction> instructions,
            PublicKey payer,
            List<AddressLookupTable> addressLookupTables);

    private String encode(
            final List<TransactionInstruction> instructions,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(messageBufferSize());

        writeMessage(buffer, blockhash, instructions, payer, addressLookupTables);

        sign(buffer, signers);
        return base64Encode(buffer);
    }

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
        return encode(
                List.of(
                        SystemProgram.transfer(
                                from,
                                to,
                                amount)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        tokenProgram.getTokenProgram().transfer(
                                from,
                                to,
                                owner,
                                amount,
                                signers.stream().map(TestKeyPair::getSolana4jPublicKey).collect(Collectors.toList()))
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        tokenProgram.getTokenProgram().mintTo(
                                mint,
                                authority,
                                destination)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        SystemProgram.createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()),
                        tokenProgram.getTokenProgram().initializeMint(
                                account,
                                (byte) decimals,
                                mintAuthority,
                                Optional.of(freezeAuthority))
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        SystemProgram.createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()),
                        tokenProgram.getTokenProgram().initializeMultisig(
                                account,
                                multiSigSigners,
                                requiredSigners)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        SystemProgram.createAccount(
                                authority,
                                nonce,
                                rentExemption,
                                accountSpan,
                                SYSTEM_PROGRAM_ACCOUNT),
                        SystemProgram.nonceInitialize(nonce, authority)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        SystemProgram.createAccount(
                                payer,
                                account,
                                rentExemption,
                                accountSpan,
                                tokenProgram.getProgram()),
                        tokenProgram.getTokenProgram().initializeAccount(
                                account,
                                mint,
                                owner)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
    }

    @Override
    public String createAddressLookupTable(
            final ProgramDerivedAddress programDerivedAddress,
            final PublicKey authority,
            final Slot recentSlot,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers)
    {
        return encode(
                List.of(
                        AddressLookupTableProgram.createLookupTable(
                                programDerivedAddress,
                                authority,
                                payer,
                                recentSlot)
                ),
                blockhash,
                payer,
                signers,
                null);
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
        return encode(
                List.of(
                        AddressLookupTableProgram.extendLookupTable(
                                lookupAddress,
                                authority,
                                payer,
                                addressesToAdd)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        SystemProgram.nonceAdvance(account, authority)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        AssociatedTokenProgram.createAssociatedTokenAccount(
                                associatedTokenAddress,
                                mint,
                                owner,
                                payer,
                                tokenProgram.getProgram(),
                                idempotent)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        tokenProgram.getTokenProgram().setAuthority(
                                tokenAccount,
                                tokenAccountNewAuthority,
                                tokenAccountOldAuthority,
                                signers.stream().map(TestKeyPair::getSolana4jPublicKey).collect(Collectors.toList()),
                                authorityType)
                ),
                blockhash,
                payer.getSolana4jPublicKey(),
                signers,
                addressLookupTables);
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
        return encode(
                List.of(
                        BpfLoaderUpgradeableProgram.setUpgradeAuthority(
                                program,
                                oldUpgradeAuthority,
                                Optional.of(newUpgradeAuthority))
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
    }

    @Override
    public String createStakeAccount(
            final PublicKey stakeAccount,
            final PublicKey stakeAuthority,
            final PublicKey withdrawAuthority,
            final long lamports,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        return encode(
                List.of(
                        SystemProgram.createAccount(
                                payer,
                                stakeAccount,
                                lamports,
                                StakeProgram.STAKE_ACCOUNT_SPACE,
                                StakeProgram.STAKE_PROGRAM_ACCOUNT),
                        StakeProgram.initialize(
                                stakeAccount,
                                new StakeProgram.Authorized(stakeAuthority, withdrawAuthority),
                                StakeProgram.Lockup.DEFAULT)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
    }

    @Override
    public String delegateStake(
            final PublicKey stakeAccount,
            final PublicKey stakeAuthority,
            final PublicKey voteAccount,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        return encode(
                List.of(
                        StakeProgram.delegate(stakeAccount, stakeAuthority, voteAccount)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
    }

    @Override
    public String deactivateStake(
            final PublicKey stakeAccount,
            final PublicKey stakeAuthority,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        return encode(
                List.of(
                        StakeProgram.deactivate(stakeAccount, stakeAuthority)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
    }

    @Override
    public String withdrawFromStake(
            final PublicKey stakeAccount,
            final PublicKey withdrawAuthority,
            final PublicKey recipient,
            final long lamports,
            final Blockhash blockhash,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        return encode(
                List.of(
                        StakeProgram.withdraw(stakeAccount, withdrawAuthority, recipient, lamports)
                ),
                blockhash,
                payer,
                signers,
                addressLookupTables);
    }

    protected static void sign(final ByteBuffer buffer, final List<TestKeyPair> signers)
    {
        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final TestKeyPair signer : signers)
        {
            signedMessageBuilder.by(signer.getSolana4jPublicKey(), (transaction, signature) -> BouncyCastleSigner.sign(signer.getPrivateKeyBytes(), transaction, signature));
        }
        signedMessageBuilder.build();
    }

    protected static String base64Encode(final ByteBuffer bytes)
    {
        return Base64.getEncoder().encodeToString(SolanaEncoding.copyBuffer(bytes));
    }
}
