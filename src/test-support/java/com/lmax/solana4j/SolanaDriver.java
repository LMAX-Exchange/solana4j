package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.programs.AddressWithBumpSeed;
import com.lmax.solana4j.testclient.api.AccountInfo;
import com.lmax.solana4j.testclient.api.Commitment;
import com.lmax.solana4j.testclient.api.SolanaApi;
import com.lmax.solana4j.testclient.api.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SolanaDriver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SolanaDriver.class);

    private final SolanaApi solanaApi;
    private TransactionFactory transactionFactory;

    public SolanaDriver(SolanaApi solanaApi)
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

    public String createAddressLookupTable(final AddressWithBumpSeed addressWithBumpSeed,
                                           final TestKeyPair authority,
                                           final TestKeyPair payer,
                                           final Slot slot,
                                           final List<AddressLookupTable> addressLookupTables)
    {
        final com.lmax.solana4j.testclient.api.Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().createAddressLookupTable(
                addressWithBumpSeed,
                authority,
                payer,
                slot,
                Solana.blockhash(recentBlockhash.getBytes()),
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
        final com.lmax.solana4j.testclient.api.Blockhash recentBlockhash = solanaApi.getRecentBlockHash();

        final String transactionBlob = getTransactionFactory().extendAddressLookupTable(
                addressLookupTable.getSolana4jPublicKey(),
                authority,
                payer,
                addressesToAdd.stream().map(TestPublicKey::getSolana4jPublicKey).collect(Collectors.toList()),
                Solana.blockhash(recentBlockhash.getBytes()),
                addressLookupTables);

        return solanaApi.sendTransaction(transactionBlob, Commitment.FINALIZED);
    }

    private TransactionFactory getTransactionFactory()
    {
        if (transactionFactory == null)
        {
            throw new RuntimeException("Please set the message encoding used to create transactions for submitting to the solana blockchain.");
        }
        return transactionFactory;
    }

    public void setMessageEncoding(final String messageEncoding)
    {
        if (messageEncoding.equals("V0"))
        {
            transactionFactory = new V0TransactionFactory();
        }
        else if (messageEncoding.equals("Legacy"))
        {
            transactionFactory = new LegacyTransactionFactory();
        }
        else
        {
            throw new RuntimeException("Unknown message encoding");
        }
    }
}
