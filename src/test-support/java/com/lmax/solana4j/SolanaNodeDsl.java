package com.lmax.solana4j;

import com.lmax.simpledsl.api.DslParams;
import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RequiredArg;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.assertion.IsEqualToAssertion;
import com.lmax.solana4j.assertion.IsNotNullAssertion;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.solanaclient.api.AccountInfo;
import com.lmax.solana4j.solanaclient.api.Commitment;
import com.lmax.solana4j.solanaclient.jsonrpc.SolanaClient;
import com.lmax.solana4j.domain.Sol;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.util.TestKeyPairGenerator;
import org.bouncycastle.util.encoders.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lmax.solana4j.solanaclient.api.Commitment.CONFIRMED;
import static com.lmax.solana4j.solanaclient.api.Commitment.FINALIZED;
import static com.lmax.solana4j.programs.SystemProgram.MINT_ACCOUNT_LENGTH;
import static com.lmax.solana4j.programs.SystemProgram.NONCE_ACCOUNT_LENGTH;
import static com.lmax.solana4j.programs.TokenProgram.ACCOUNT_LAYOUT_SPAN;
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

    public void setMessageEncoding(final String messageEncoding)
    {
        solanaDriver.setMessageEncoding(messageEncoding);
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
        final Sol sol = new Sol(params.valueAsLong("amountSol"));

        final String transactionSignature = solanaDriver.requestAirdrop(address, sol.lamports());
        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
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

        final ProgramDerivedAddress programDerivedAddress = AddressLookupTableProgram.deriveAddress(authority.getSolana4jPublicKey(), SolanaEncoding.slot(recentSlot));
        testContext.storePublicKey(params.value("lookupTableAddress"), new TestPublicKey(programDerivedAddress.address().bytes()));

        final String transactionSignature = solanaDriver.createAddressLookupTable(programDerivedAddress, authority, payer, Solana.slot(recentSlot), addressLookupTables);
        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
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

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void waitForSlot(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("slot")
        );

        new Waiter().waitFor(new IsEqualToAssertion<>(true, () -> solanaDriver.getSlot(FINALIZED) > params.valueAsLong("slot")));
    }

    public void createMintAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new RequiredArg("decimals"),
                new RequiredArg("mintAuthority"),
                new RequiredArg("freezeAuthority"),
                new RequiredArg("payer"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair account = testContext.getKeyPair(params.value("account"));
        final int decimals = params.valueAsInt("decimals");
        final TestKeyPair mintAuthority = testContext.getKeyPair(params.value("mintAuthority"));
        final TestKeyPair freezeAuthority = testContext.getKeyPair(params.value("freezeAuthority"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.createMintAccount(tokenProgram, account, decimals, mintAuthority, freezeAuthority, payer, MINT_ACCOUNT_LENGTH, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void mintTo(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("tokenMint"),
                new RequiredArg("to"),
                new RequiredArg("authority"),
                new RequiredArg("payer"),
                new RequiredArg("amount"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestPublicKey tokenMint = testContext.getPublicKey(params.value("tokenMint"));
        final TestKeyPair authority = testContext.getKeyPair(params.value("authority"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final TestPublicKey to = testContext.getPublicKey(params.value("to"));
        final long amount = params.valueAsLong("amount");
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.mintTo(tokenProgram.getFactory(), tokenMint, to, amount, authority, payer, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void createTokenAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new RequiredArg("owner"),
                new RequiredArg("tokenMint"),
                new RequiredArg("payer"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair account = testContext.getKeyPair(params.value("account"));
        final TestPublicKey owner = testContext.getPublicKey(params.value("owner"));
        final TestPublicKey tokenMint = testContext.getPublicKey(params.value("tokenMint"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.createTokenAccount(
                tokenProgram,
                account,
                owner,
                tokenMint,
                payer,
                ACCOUNT_LAYOUT_SPAN,
                addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void tokenBalance(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amount"),
                new OptionalArg("commitment").setDefault("FINALIZED").setAllowedValues(Commitment.class));

        final TestPublicKey address = testContext.getPublicKey(params.value("address"));
        final String amount = params.value("amount");
        final Commitment commitment = params.valueAs("commitment", Commitment.class);

        new Waiter().waitFor(new IsEqualToAssertion<>(amount, () -> solanaDriver.getTokenBalance(address.getPublicKeyBase58(), commitment)));
    }

    public void balance(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amountSol"),
                new OptionalArg("commitment").setDefault("FINALIZED").setAllowedValues(Commitment.class));

        final TestPublicKey address = testContext.getPublicKey(params.value("address"));
        final Sol sol = new Sol(params.valueAsLong("amountSol"));
        final Commitment commitment = params.valueAs("commitment", Commitment.class);

        new Waiter().waitFor(new IsEqualToAssertion<>(sol.lamports(), () -> solanaDriver.getBalance(address.getPublicKeyBase58(), commitment)));
    }

    public void tokenTransfer(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("from"),
                new RequiredArg("to"),
                new RequiredArg("owner"),
                new RequiredArg("amount"),
                new RequiredArg("payer"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair from = testContext.getKeyPair(params.value("from"));
        final TestPublicKey to = testContext.getPublicKey(params.value("to"));
        final TestKeyPair owner = testContext.getKeyPair(params.value("owner"));
        final long amount = params.valueAsLong("amount");
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.tokenTransfer(tokenProgram, from, to, owner, amount, payer, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void createNonceAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new RequiredArg("authority"),
                new RequiredArg("payer"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair account = testContext.getKeyPair(params.value("account"));
        final TestKeyPair authority = testContext.getKeyPair(params.value("authority"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.createNonceAccount(account, authority, payer, NONCE_ACCOUNT_LENGTH, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void advanceNonce(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new RequiredArg("authority"),
                new RequiredArg("payer"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair account = testContext.getKeyPair(params.value("account"));
        final TestKeyPair authority = testContext.getKeyPair(params.value("authority"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.advanceNonce(account, authority, payer, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }

    public void transfer(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("from"),
                new RequiredArg("to"),
                new RequiredArg("amountSol"),
                new RequiredArg("payer"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair from = testContext.getKeyPair(params.value("from"));
        final TestPublicKey to = testContext.getPublicKey(params.value("to"));
        final Sol sol = new Sol(params.valueAsLong("amountSol"));
        final TestKeyPair payer = testContext.getKeyPair(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext::getAddressLookupTable)
                .toList();

        final String transactionSignature = solanaDriver.transfer(from, to, sol.lamports(), payer, addressLookupTables);

        new Waiter().waitFor(new IsNotNullAssertion<>(() -> solanaDriver.getTransactionResponse(transactionSignature, CONFIRMED).getTransaction()));
    }
}
