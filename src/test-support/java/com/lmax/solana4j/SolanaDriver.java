package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.domain.TokenProgramFactory;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.solanaclient.api.AccountInfo;
import com.lmax.solana4j.solanaclient.api.Blockhash;
import com.lmax.solana4j.solanaclient.api.Commitment;
import com.lmax.solana4j.solanaclient.api.SolanaApi;
import com.lmax.solana4j.solanaclient.api.TransactionResponse;
import com.lmax.solana4j.transactionblobs.LegacyTransactionBlobFactory;
import com.lmax.solana4j.transactionblobs.TransactionBlobFactory;
import com.lmax.solana4j.transactionblobs.V0TransactionBlobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SolanaDriver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SolanaDriver.class);

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

    public TransactionResponse getTransactionResponse(final String transactionSignature, final Commitment commitment)
    {
        return solanaApi.getTransactionResponse(transactionSignature, commitment);
    }

    public long getSlot(final Commitment commitment)
    {
        final long slot = solanaApi.getSlot(commitment);
        LOGGER.info("Received slot {}.", slot);
        return slot;
    }

    public AccountInfo getAccountInfo(final TestPublicKey address, final Commitment commitment)
    {
        return solanaApi.getAccountInfo(address.getPublicKeyBase58(), commitment);
    }

    public String createAddressLookupTable(final ProgramDerivedAddress programDerivedAddress,
                                           final TestKeyPair authority,
                                           final TestKeyPair payer,
                                           final Slot slot,
                                           final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createAddressLookupTable(
                programDerivedAddress,
                authority.getSolana4jPublicKey(),
                slot,
                Solana.blockhash(recentBlockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables);

        LOGGER.info("About to send transaction blob {}.", transactionBlob);

        return solanaApi.sendTransaction(transactionBlob, Commitment.CONFIRMED);
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
                Solana.blockhash(recentBlockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
    }

    public String createMintAccount(
            final TokenProgram tokenProgram,
            final TestKeyPair account,
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
                account.getSolana4jPublicKey(),
                decimals,
                mintAuthority.getSolana4jPublicKey(),
                freezeAuthority.getSolana4jPublicKey(),
                rentExemption,
                accountSpan,
                Solana.blockhash(recentBlockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, account),
                addressLookupTables
        );

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
    }

    public String mintTo(
            final TokenProgramFactory tokenProgramFactory,
            final TestPublicKey tokenMintAddress,
            final TestPublicKey to,
            final long amount,
            final TestKeyPair authority,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().mintTo(
                tokenProgramFactory,
                tokenMintAddress.getSolana4jPublicKey(),
                authority.getSolana4jPublicKey(),
                SolanaEncoding.destination(to.getSolana4jPublicKey(), amount),
                SolanaEncoding.blockhash(recentBlockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables
        );

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
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

        final String transactionBlob = getTransactionFactory().initializeTokenAccount(
                tokenProgram,
                rentExemption,
                accountSpan,
                account.getSolana4jPublicKey(),
                owner.getSolana4jPublicKey(),
                tokenMintAddress.getSolana4jPublicKey(),
                Solana.blockhash(blockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, account),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
    }

    public long getBalance(final String address, final Commitment commitment)
    {
        return solanaApi.getBalance(address, commitment);
    }

    public String getTokenBalance(final String address, final Commitment commitment)
    {
        return solanaApi.getTokenAccountBalance(address, commitment).getUiAmountString();
    }

    public String createNonceAccount(
            final TestKeyPair account,
            final TestKeyPair authority,
            final TestKeyPair payer,
            final int accountSpan,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Long rentExemption = solanaApi.getMinimalBalanceForRentExemption(accountSpan);
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createNonce(
                account.getSolana4jPublicKey(),
                authority.getSolana4jPublicKey(),
                Solana.blockhash(blockhash.getBytes()),
                rentExemption,
                accountSpan,
                payer.getSolana4jPublicKey(),
                List.of(payer, account, authority),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
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
                Solana.blockhash(blockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, authority),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
    }

    public String tokenTransfer(
            final TokenProgram tokenProgram,
            final TestKeyPair from,
            final TestPublicKey to,
            final TestKeyPair owner,
            final long amount,
            final TestKeyPair payer,
            final List<AddressLookupTable> addressLookupTables)
    {
        final Blockhash blockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().tokenTransfer(
                tokenProgram.getFactory(),
                from.getSolana4jPublicKey(),
                to.getSolana4jPublicKey(),
                owner.getSolana4jPublicKey(),
                amount,
                Solana.blockhash(blockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, owner),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
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
                Solana.blockhash(blockhash.getBytes()),
                payer.getSolana4jPublicKey(),
                List.of(payer, from),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
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
