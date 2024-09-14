package com.lmax.solana4j.transactionblobs;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.domain.TokenProgramFactory;

import java.util.List;

public interface TransactionBlobFactory
{
    String solTransfer(
            PublicKey from,
            PublicKey to,
            long amount,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String tokenTransfer(
            TokenProgramFactory tokenProgramFactory,
            PublicKey from,
            PublicKey to,
            PublicKey owner,
            long amount,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String mintTo(
            TokenProgramFactory tokenProgramFactory,
            PublicKey mint,
            PublicKey authority,
            Destination destination,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String createMintAccount(
            TokenProgram tokenProgram,
            PublicKey account,
            int decimals,
            PublicKey mintAuthority,
            PublicKey freezeAuthority,
            long rentExemption,
            int accountSpan,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String createMultiSigAccount(
            TokenProgram tokenProgram,
            PublicKey account,
            List<PublicKey> multiSigSigners,
            int requiredSigners,
            long rentExemption,
            int accountSpan,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables
    );

    String createNonce(
            PublicKey nonce,
            PublicKey authority,
            Blockhash blockhash,
            long rentExemption,
            int accountSpan,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String createTokenAccount(
            TokenProgram tokenProgram,
            long rentExemption,
            int accountSpan,
            PublicKey account,
            PublicKey owner,
            PublicKey mint,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String createAddressLookupTable(
            ProgramDerivedAddress programDerivedAddress,
            PublicKey authority,
            Slot slot,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers);

    String extendAddressLookupTable(
            PublicKey lookupAddress,
            PublicKey authority,
            List<PublicKey> addressesToAdd,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String advanceNonce(
            PublicKey account,
            PublicKey authority,
            Blockhash blockhash,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String createAssociatedTokenAddress(
            TokenProgram tokenProgram,
            PublicKey owner,
            ProgramDerivedAddress associatedTokenAddress,
            Blockhash blockhash,
            PublicKey mint,
            boolean idempotent,
            PublicKey payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);

    String setAuthority(
            TokenProgram tokenProgram,
            PublicKey account,
            PublicKey oldAuthority,
            PublicKey newAuthority,
            com.lmax.solana4j.programs.TokenProgram.AuthorityType authorityType,
            Blockhash blockhash,
            TestKeyPair payer,
            List<TestKeyPair> signers,
            List<AddressLookupTable> addressLookupTables);
}