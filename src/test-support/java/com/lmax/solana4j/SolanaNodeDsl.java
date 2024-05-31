package com.lmax.solana4j;

import com.lmax.simpledsl.api.DslParams;
import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RequiredArg;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.assertions.IsEqualToAssertion;
import com.lmax.solana4j.assertions.IsNotNullAssertion;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AddressWithBumpSeed;
import com.lmax.solana4j.testclient.api.AccountInfo;
import com.lmax.solana4j.testclient.api.Commitment;
import com.lmax.solana4j.testclient.jsonrpc.SolanaClient;
import org.bouncycastle.util.encoders.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lmax.solana4j.testclient.api.Commitment.FINALIZED;
import static java.util.Arrays.stream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SolanaNodeDsl
{
    private final SolanaDriver solanaDriver;
    private final TestContext testContext;

    public SolanaNodeDsl(final String rpcUrl)
    {
        this.solanaDriver = new SolanaDriver(new SolanaClient(rpcUrl));
        this.testContext = new TestContext();
    }

    public void createKeyPair(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("alias"));

        final TestKeyPair testKeyPair = TestKeyPairGenerator.generateSolanaKeyPair();

        testContext.storeKeyPair(params.value("alias"), testKeyPair);
        testContext.storePublicKey(params.value("alias"), new TestPublicKey(testKeyPair.getPublicKeyBytes()));
    }

    public void airdrop(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amountSol"),
                new OptionalArg("rememberTransactionId"));

        final TestPublicKey address = testContext.getPublicKey(params.value("address"));
        final Sol sol = new Sol(Long.parseLong(params.value("amountSol")));

        final String transactionSignature = solanaDriver.requestAirdrop(address, sol.lamports());
        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, FINALIZED).getTransaction()));
    }

    public void retrieveAddressLookupTable(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("lookupTableAddress"),
                new OptionalArg("addresses").setAllowMultipleValues()
        );

        final TestPublicKey account = testContext.getPublicKey(params.value("lookupTableAddress"));

        final AccountInfo accountInfo = new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getAccountInfo(account, FINALIZED)));

        final List<PublicKey> expectedAddresses = stream(params.values("addresses"))
                .map(address -> testContext.getKeyPair(address).getSolana4jPublicKey())
                .collect(Collectors.toList());

        final AddressLookupTable addressLookupTable = AddressLookupTableProgram.deserializeAddressLookupTable(account.getSolana4jPublicKey(), Base64.decode(accountInfo.getData().get(0)));
        assertThat(addressLookupTable.getAddressLookups()).usingRecursiveComparison().isEqualTo(expectedAddresses);
        testContext.storeAddressLookupTable(params.value("lookupTableAddress"), addressLookupTable);
    }

    public void createAddressLookupTable(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("lookupTableAddress"),
                new RequiredArg("authority"),
                new RequiredArg("payer"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues());

        final TestKeyPair authority = testContext.getKeyPair(params.value("authority"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final long recentSlot = solanaDriver.getSlot(Commitment.FINALIZED);

        final AddressWithBumpSeed addressWithBumpSeed = AddressLookupTableProgram.deriveLookupTableAddress(Solana.account(authority.getPublicKeyBytes()), Solana.slot(recentSlot));
        testContext.storePublicKey(params.value("lookupTableAddress"), addressWithBumpSeed.getLookupTableAddress());

        final String transactionSignature = solanaDriver.createAddressLookupTable(addressWithBumpSeed, authority, payer, Solana.slot(recentSlot), addressLookupTables);
        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, FINALIZED).getTransaction()));
    }

    public void extendAddressLookupTable(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("lookupTableAddress"),
                new RequiredArg("authority"),
                new RequiredArg("payer"),
                new RequiredArg("addresses").setAllowMultipleValues(),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestPublicKey addressLookupTable = testContext.getPublicKey(params.value("lookupTableAddress"));
        final TestKeyPair authority = testContext.getKeyPair(params.value("authority"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final List<TestPublicKey> addresses = new ArrayList<>();
        for (final String address : params.values("addresses"))
        {
            addresses.add(testContext.getPublicKey(address));
        }

        final String transactionSignature = solanaDriver.extendAddressLookupTable(addressLookupTable, authority, payer, addresses, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, FINALIZED).getTransaction()));
    }

    public void waitForSlot(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("slot")
        );

        new Waiter().waitFor(new IsEqualToAssertion<>(true, () -> solanaDriver.getSlot(FINALIZED) > params.valueAsLong("slot")));
    }

    public void setMessageEncoding(final String messageEncoding)
    {
        solanaDriver.setMessageEncoding(messageEncoding);
    }
}
