package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.programs.TokenProgramBase;
import com.lmax.solana4j.transactionblobs.LegacyTransactionBlobFactory;
import com.lmax.solana4j.transactionblobs.TransactionBlobFactory;
import com.lmax.solana4j.transactionblobs.V0TransactionBlobFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SolanaDriver
{
    private final SolanaApi solanaApi;
    private TransactionBlobFactory transactionBlobFactory;

    public SolanaDriver(final SolanaApi solanaApi)
    {
        this.solanaApi = solanaApi;
    }

    public String requestAirdrop(final TestPublicKey address, final long amountLamports)
    {
        return solanaApi.requestAirdrop(address.getPublicKeyBase58(), amountLamports);
    }

    public TransactionResponse getTransactionResponse(final String transactionSignature)
    {
        return solanaApi.getTransactionResponse(transactionSignature);
    }

    public long getSlot()
    {
        return solanaApi.getSlot();
    }

    public long getBlockHeight()
    {
        return solanaApi.getBlockHeight();
    }

    public AccountInfo getAccountInfo(final TestPublicKey address)
    {
        return solanaApi.getAccountInfo(address.getPublicKeyBase58());
    }

    public String createAddressLookupTable(final ProgramDerivedAddress programDerivedAddress,
                                           final TestKeyPair authority,
                                           final TestKeyPair payer,
                                           final Slot slot)
    {
        final Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createAddressLookupTable(
                programDerivedAddress,
                authority.getSolana4jPublicKey(),
                slot,
                Solana.blockhash(recentBlockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority));

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String extendAddressLookupTable(final TestPublicKey addressLookupTable,
                                           final TestKeyPair authority,
                                           final TestKeyPair payer,
                                           final List<TestPublicKey> addressesToAdd,
                                           final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().extendAddressLookupTable(
                addressLookupTable.getSolana4jPublicKey(),
                authority.getSolana4jPublicKey(),
                addressesToAdd.stream().map(TestPublicKey::getSolana4jPublicKey).collect(Collectors.toList()),
                Solana.blockhash(recentBlockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String createMintAccount(
            final TokenProgram tokenProgram,
            final TestKeyPair tokenMint,
            final int decimals,
            final TestKeyPair mintAuthority,
            final TestKeyPair freezeAuthority,
            final TestKeyPair payer,
            final int accountSpan,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash recentBlockhash = solanaApi.getRecentBlockHash();
        final long rentExemption = solanaApi.getMinimalBalanceForRentExemption(accountSpan);

        final String transactionBlob = getTransactionFactory().createMintAccount(
                tokenProgram,
                tokenMint.getSolana4jPublicKey(),
                decimals,
                mintAuthority.getSolana4jPublicKey(),
                freezeAuthority.getSolana4jPublicKey(),
                rentExemption,
                accountSpan,
                Solana.blockhash(recentBlockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, tokenMint),
                addressLookupTables
        );

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String mintTo(
            final TokenProgram tokenProgram,
            final TestPublicKey tokenMintAddress,
            final TestPublicKey to,
            final long amount,
            final TestKeyPair authority,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().mintTo(
                tokenProgram,
                tokenMintAddress.getSolana4jPublicKey(),
                authority.getSolana4jPublicKey(),
                Solana.destination(to.getSolana4jPublicKey(), amount),
                Solana.blockhash(recentBlockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables
        );

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String createTokenAccount(
            final TokenProgram tokenProgram,
            final TestKeyPair account,
            final TestPublicKey owner,
            final TestPublicKey tokenMintAddress,
            final TestKeyPair payer,
            final int accountSpan,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Long rentExemption = solanaApi.getMinimalBalanceForRentExemption(accountSpan);
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createTokenAccount(
                tokenProgram,
                rentExemption,
                accountSpan,
                account.getSolana4jPublicKey(),
                owner.getSolana4jPublicKey(),
                tokenMintAddress.getSolana4jPublicKey(),
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, account),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public long getBalance(final String address)
    {
        return solanaApi.getBalance(address);
    }

    public String getTokenBalance(final String address)
    {
        return solanaApi.getTokenAccountBalance(address).getUiAmountString();
    }

    public String createNonceAccount(
            final TestKeyPair nonceAccount,
            final TestKeyPair nonceAuthority,
            final TestKeyPair payer,
            final int accountSpan,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Long rentExemption = solanaApi.getMinimalBalanceForRentExemption(accountSpan);
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createNonce(
                nonceAccount.getSolana4jPublicKey(),
                nonceAuthority.getSolana4jPublicKey(),
                Solana.blockhash(blockhash.getBlockhashBase58()),
                rentExemption,
                accountSpan,
                payer.getSolana4jPublicKey(),
                List.of(payer, nonceAccount, nonceAuthority),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String createMultiSigAccount(
            final TokenProgram tokenProgram,
            final TestKeyPair account,
            final List<PublicKey> multiSigSigners,
            final int requiredSigners,
            final int accountSpan,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Long rentExemption = solanaApi.getMinimalBalanceForRentExemption(accountSpan);
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createMultiSigAccount(
                tokenProgram,
                account.getSolana4jPublicKey(),
                multiSigSigners,
                requiredSigners,
                rentExemption,
                accountSpan,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, account),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String advanceNonce(
            final TestKeyPair account,
            final TestKeyPair authority,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().advanceNonce(
                account.getSolana4jPublicKey(),
                authority.getSolana4jPublicKey(),
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String tokenTransfer(
            final TokenProgram tokenProgram,
            final TestKeyPair from,
            final TestPublicKey to,
            final TestKeyPair owner,
            final long amount,
            final TestKeyPair payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().tokenTransfer(
                tokenProgram,
                from.getSolana4jPublicKey(),
                to.getSolana4jPublicKey(),
                owner.getSolana4jPublicKey(),
                amount,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                signers,
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String transfer(
            final TestKeyPair from,
            final TestPublicKey to,
            final long amount,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().solTransfer(
                from.getSolana4jPublicKey(),
                to.getSolana4jPublicKey(),
                amount,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer, from),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String createAssociatedTokenAccount(
            final TokenProgram tokenProgram,
            final ProgramDerivedAddress associatedTokenAddress,
            final PublicKey mint,
            final PublicKey owner,
            final boolean idempotent,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createAssociatedTokenAccount(
                tokenProgram,
                owner,
                associatedTokenAddress,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                mint,
                idempotent,
                payer.getSolana4jPublicKey(),
                List.of(payer),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String setTokenAccountAuthority(
            final TokenProgram tokenProgram,
            final PublicKey tokenAccount,
            final PublicKey tokenAccountOldAuthority,
            final PublicKey tokenAccountNewAuthority,
            final TokenProgramBase.AuthorityType authorityType,
            final TestKeyPair payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().setTokenAccountAuthority(
                tokenProgram,
                tokenAccount,
                tokenAccountOldAuthority,
                tokenAccountNewAuthority,
                authorityType,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer,
                signers,
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String setComputeUnits(final int computeUnitLimit, final long computeUnitPrice, final TestKeyPair payer)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().setComputeUnits(
                computeUnitLimit,
                computeUnitPrice,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer.getSolana4jPublicKey(),
                List.of(payer));

        return solanaApi.sendTransaction(transactionBlob);
    }

    public String setBpfUpgradeableProgramUpgradeAuthority(
            final PublicKey program,
            final PublicKey oldUpgradeAuthority,
            final PublicKey newUpgradeAuthority,
            final PublicKey payer,
            final List<TestKeyPair> signers,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().setBpfUpgradeableProgramUpgradeAuthority(
                program,
                oldUpgradeAuthority,
                newUpgradeAuthority,
                Solana.blockhash(blockhash.getBlockhashBase58()),
                payer,
                signers,
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob);
    }

    public void setMessageEncoding(final String messageEncoding)
    {
        if (messageEncoding.equals("V0"))
        {
            transactionBlobFactory = new V0TransactionBlobFactory();
        }
        else if (messageEncoding.equals("Legacy"))
        {
            transactionBlobFactory = new LegacyTransactionBlobFactory();
        }
        else
        {
            throw new RuntimeException("Unknown message encoding");
        }
    }

    private TransactionBlobFactory getTransactionFactory()
    {
        if (transactionBlobFactory == null)
        {
            throw new RuntimeException("Please set the message encoding used to create transactions for submitting to the solana blockchain.");
        }
        return transactionBlobFactory;
    }
}
