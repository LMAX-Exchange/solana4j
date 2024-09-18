package com.lmax.solana4j;

import com.lmax.simpledsl.api.DslParams;
import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RepeatingArgGroup;
import com.lmax.simpledsl.api.RepeatingGroup;
import com.lmax.simpledsl.api.RequiredArg;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.domain.Sol;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.BpfLoaderUpgradeableProgram;
import com.lmax.solana4j.programs.SystemProgram;
import com.lmax.solana4j.solanaclient.api.AccountInfo;
import com.lmax.solana4j.solanaclient.api.TransactionData;
import com.lmax.solana4j.solanaclient.jsonrpc.SolanaClient;
import com.lmax.solana4j.util.TestKeyPairGenerator;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Base64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lmax.solana4j.assertion.Condition.isEqualTo;
import static com.lmax.solana4j.assertion.Condition.isNotNull;
import static com.lmax.solana4j.assertion.Condition.isTrue;
import static com.lmax.solana4j.encoding.SolanaEncoding.deriveAssociatedTokenAddress;
import static com.lmax.solana4j.programs.SystemProgram.MINT_ACCOUNT_LENGTH;
import static com.lmax.solana4j.programs.SystemProgram.NONCE_ACCOUNT_LENGTH;
import static com.lmax.solana4j.programs.TokenProgram.ACCOUNT_LAYOUT_SPAN;
import static com.lmax.solana4j.programs.TokenProgram.MULTI_SIG_LAYOUT_SPAN;
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

        testContext.data(TestDataType.TEST_KEY_PAIR).store(params.value("alias"), testKeyPair);
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("alias"), new TestPublicKey(testKeyPair.getPublicKeyBytes()));
    }

    public void airdrop(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amountSol"));

        final String address = testContext.lookupOrLiteral(params.value("address"), TestDataType.TEST_PUBLIC_KEY);
        final Sol sol = new Sol(params.valueAsBigDecimal("amountSol"));

        final String transactionSignature = solanaDriver.requestAirdrop(new TestPublicKey(Base58.decode(address)), sol.lamports());

        final long recentBlockHeight = solanaDriver.getBlockHeight();
        waitForBlockHeight(recentBlockHeight + 1);

        Waiter.waitFor(transactionFinalized(transactionSignature));
    }

    public void retrieveAddressLookupTable(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("lookupTableAddress"),
                new OptionalArg("addresses").setAllowMultipleValues()
        );

        final TestPublicKey account = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("lookupTableAddress"));

        final AccountInfo accountInfo = Waiter.waitFor(isNotNull(() -> solanaDriver.getAccountInfo(account)));

        final List<PublicKey> expectedAddresses = stream(params.values("addresses"))
                .map(address -> testContext.data(TestDataType.TEST_KEY_PAIR).lookup(address).getSolana4jPublicKey())
                .collect(Collectors.toList());

        final AddressLookupTable addressLookupTable = AddressLookupTableProgram.deserializeAddressLookupTable(account.getSolana4jPublicKey(), Base64.decode(accountInfo.getData().get(0)));
        assertThat(addressLookupTable.getAddresses()).usingRecursiveComparison().isEqualTo(expectedAddresses);
        testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE).store(params.value("lookupTableAddress"), addressLookupTable);
    }

    public void createAddressLookupTable(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("lookupTableAddress"),
                new RequiredArg("authority"),
                new RequiredArg("payer"));

        final TestKeyPair authority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("authority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final long recentSlot = solanaDriver.getSlot();

        final ProgramDerivedAddress programDerivedAddress = AddressLookupTableProgram.deriveAddress(authority.getSolana4jPublicKey(), SolanaEncoding.slot(recentSlot));
        final TestPublicKey address = new TestPublicKey(programDerivedAddress.address().bytes());
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("lookupTableAddress"), address);

        // intermittency caused by slot not moving on to the next slot before including in the sent transaction
        waitForSlot(recentSlot + 1);
        final String transactionSignature = solanaDriver.createAddressLookupTable(programDerivedAddress, authority, payer, SolanaEncoding.slot(recentSlot));
        Waiter.waitFor(transactionFinalized(transactionSignature));
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

        final TestPublicKey addressLookupTable = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("lookupTableAddress"));
        final TestKeyPair authority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("authority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final List<TestPublicKey> addresses = new ArrayList<>();
        for (final String address : params.values("addresses"))
        {
            addresses.add(testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(address));
        }

        final String transactionSignature = solanaDriver.extendAddressLookupTable(addressLookupTable, authority, payer, addresses, addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));
    }

    public void waitForSlot(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("slot")
        );

        Waiter.waitFor(isTrue(() -> solanaDriver.getSlot() > params.valueAsLong("slot")));
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

        final TestKeyPair account = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("account"));
        final int decimals = params.valueAsInt("decimals");
        final TestKeyPair mintAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("mintAuthority"));
        final TestKeyPair freezeAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("freezeAuthority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.createMintAccount(tokenProgram, account, decimals, mintAuthority, freezeAuthority, payer, MINT_ACCOUNT_LENGTH, addressLookupTables);

        final long recentBlockHeight = solanaDriver.getBlockHeight();
        waitForBlockHeight(recentBlockHeight + 1);

        Waiter.waitFor(transactionFinalized(transactionSignature));
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

        final TestPublicKey tokenMint = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenMint"));
        final TestKeyPair authority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("authority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final TestPublicKey to = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("to"));
        final long amount = params.valueAsLong("amount");
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.mintTo(tokenProgram.getFactory(), tokenMint, to, amount, authority, payer, addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));
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

        final TestKeyPair account = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("account"));
        final TestPublicKey owner = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("owner"));
        final TestPublicKey tokenMint = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenMint"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.createTokenAccount(
                tokenProgram,
                account,
                owner,
                tokenMint,
                payer,
                ACCOUNT_LAYOUT_SPAN,
                addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));
    }

    public void tokenBalance(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amount"));

        final TestPublicKey address = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("address"));
        final String amount = params.value("amount");

        Waiter.waitFor(isEqualTo(amount, () -> solanaDriver.getTokenBalance(address.getPublicKeyBase58())));
    }

    public void balance(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amountSol"));

        final TestPublicKey address = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("address"));
        final Sol sol = new Sol(params.valueAsBigDecimal("amountSol"));

        Waiter.waitFor(isEqualTo(sol.lamports(), () -> solanaDriver.getBalance(address.getPublicKeyBase58())));
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
                new OptionalArg("signers").setAllowMultipleValues(),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair from = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("from"));
        final TestPublicKey to = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("to"));
        final TestKeyPair owner = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("owner"));
        final long amount = params.valueAsLong("amount");
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<TestKeyPair> signers = params.valuesAsList("signers").stream()
                .map(testContext.data(TestDataType.TEST_KEY_PAIR)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.tokenTransfer(tokenProgram, from, to, owner, amount, payer, signers, addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));
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

        final TestKeyPair account = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("account"));
        final TestKeyPair authority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("authority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.createNonceAccount(account, authority, payer, NONCE_ACCOUNT_LENGTH, addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));
    }

    public void createMultiSigAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("multiSig"),
                new RequiredArg("multiSigSigners").setAllowMultipleValues(),
                new RequiredArg("requiredSigners"),
                new RequiredArg("payer"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair multiSig = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("multiSig"));
        final List<PublicKey> multiSigSigners = params.valuesAsList("multiSigSigners")
                .stream()
                .map(pk -> testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(pk).getSolana4jPublicKey())
                .toList();
        final int requiredSigners = params.valueAsInt("requiredSigners");
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));

        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.createMultiSigAccount(
                tokenProgram,
                multiSig,
                multiSigSigners,
                requiredSigners,
                MULTI_SIG_LAYOUT_SPAN,
                payer,
                addressLookupTables
        );

        Waiter.waitFor(isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature).getTransaction()));
    }

    public void advanceNonce(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new RequiredArg("authority"),
                new RequiredArg("payer"),
                new OptionalArg("nonce"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair account = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("account"));
        final TestKeyPair authority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("authority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final Optional<TestPublicKey> nonce = params.valueAsOptional("nonce").map(key -> testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(key));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.advanceNonce(account, authority, payer, addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));

        if (nonce.isPresent())
        {
            final AccountInfo accountInfo = solanaDriver.getAccountInfo(account.getPublicKey());
            final PublicKey newNonce = SystemProgram.getNonceAccountValue(Base64.decode(accountInfo.getData().get(0)));
            assertThat(newNonce).isNotEqualTo(nonce);
        }
    }

    public void nonceValue(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new OptionalArg("rememberNonce")
        );

        final TestPublicKey account = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("account"));

        final AccountInfo accountInfo = solanaDriver.getAccountInfo(account);
        final PublicKey nonceAccountValue = SystemProgram.getNonceAccountValue(Base64.decode(accountInfo.getData().get(0)));

        final TestPublicKey address = new TestPublicKey(nonceAccountValue.bytes());
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store("rememberNonce", address);
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

        final TestKeyPair from = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("from"));
        final TestPublicKey to = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("to"));
        final Sol sol = new Sol(params.valueAsBigDecimal("amountSol"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.transfer(from, to, sol.lamports(), payer, addressLookupTables);

        Waiter.waitFor(transactionFinalized(transactionSignature));
    }

    private Condition<TransactionData> transactionFinalized(final String transactionSignature)
    {
        // intermittency caused by block height not moving on to the next block before checking the transaction response ... it's a bit weird
        waitForBlockHeight(solanaDriver.getBlockHeight() + 1);
        return isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature).getTransaction());
    }

    public void createAssociatedTokenAddress(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("associatedTokenAddress"),
                new RequiredArg("owner"),
                new RequiredArg("tokenMint"),
                new RequiredArg("payer"),
                new OptionalArg("idempotent").setAllowedValues("true", "false").setDefault("true"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestPublicKey tokenMint = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenMint"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final TestPublicKey owner = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("owner"));

        final AssociatedTokenAddress associatedTokenAddress = deriveAssociatedTokenAddress(owner.getSolana4jPublicKey(), tokenMint.getSolana4jPublicKey(), tokenProgram.getProgram());
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("associatedTokenAddress"), new TestPublicKey(associatedTokenAddress.address().bytes()));

        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.createAssociatedTokenAddress(
                tokenProgram,
                associatedTokenAddress,
                tokenMint.getSolana4jPublicKey(),
                owner.getSolana4jPublicKey(),
                params.valueAsBoolean("idempotent"),
                payer,
                addressLookupTables
        );

        Waiter.waitFor(isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature).getTransaction()));
    }

    public void maybeCreateAndExtendAddressLookupTables(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("messageEncoding").setAllowedValues("Legacy", "V0"),
                new RequiredArg("payer"),
                new RepeatingArgGroup(
                        new RequiredArg("lookupTableAddress"),
                        new RequiredArg("addresses").setAllowMultipleValues()
                )
        );

        final String messageEncoding = params.value("messageEncoding");
        final String payer = params.value("payer");
        createKeyPair("lookupAuthority");

        if (messageEncoding.equals("V0"))
        {
            for (final RepeatingGroup group : params.valuesAsGroup("lookupTableAddress"))
            {
                createAddressLookupTable(group.valueAsParam("lookupTableAddress"), "authority: lookupAuthority", payer);
                final String addresses = String.join(", ", group.valuesAsList("addresses"));
                extendAddressLookupTable(group.value("lookupTableAddress"), "authority: lookupAuthority", payer, "addresses: " + addresses);
            }
        }
    }

    public void setAuthority(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("account"),
                new RequiredArg("oldAuthority"),
                new RequiredArg("newAuthority"),
                new RequiredArg("authorityType").setAllowedValues(com.lmax.solana4j.programs.TokenProgram.AuthorityType.class),
                new RequiredArg("payer"),
                new RequiredArg("signers").setAllowMultipleValues(),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestPublicKey account = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("account"));
        final TestPublicKey oldAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("oldAuthority"));
        final TestPublicKey newAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("newAuthority"));
        final com.lmax.solana4j.programs.TokenProgram.AuthorityType authorityType = com.lmax.solana4j.programs.TokenProgram.AuthorityType.valueOf(params.value("authorityType"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<TestKeyPair> signers = params.valuesAsList("signers").stream()
                .map(testContext.data(TestDataType.TEST_KEY_PAIR)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.setAuthority(
                tokenProgram,
                account.getSolana4jPublicKey(),
                oldAuthority.getSolana4jPublicKey(),
                newAuthority.getSolana4jPublicKey(),
                authorityType,
                payer,
                signers,
                addressLookupTables);

        Waiter.waitFor(isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature).getTransaction()));
    }

    public void setComputeUnits(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("computeUnitLimit"),
                new RequiredArg("computeUnitPrice"),
                new RequiredArg("payer")
        );

        final int computeUnitLimit = params.valueAsInt("computeUnitLimit");
        final int computeUnitPrice = params.valueAsInt("computeUnitPrice");
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final String transactionSignature = solanaDriver.setComputeUnits(computeUnitLimit, computeUnitPrice, payer);

        Waiter.waitFor(isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature).getTransaction()));
    }

    public void setUpgradeAuthority(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("program"),
                new RequiredArg("oldAuthorityPublicKey"),
                new RequiredArg("oldAuthorityPrivateKey"),
                new RequiredArg("newAuthority"),
                new RequiredArg("payer"),
                new OptionalArg("addressLookupTables")
        );

        final String program = testContext.lookupOrLiteral(params.value("program"), TestDataType.TEST_PUBLIC_KEY);
        final String oldAuthorityPublicKey = testContext.lookupOrLiteral(params.value("oldAuthorityPublicKey"), TestDataType.TEST_PUBLIC_KEY);
        final String oldAuthorityPrivateKey = testContext.lookupOrLiteral(params.value("oldAuthorityPrivateKey"), TestDataType.TEST_KEY_PAIR);

        final String newAuthority = testContext.lookupOrLiteral(params.value("newAuthority"), TestDataType.TEST_PUBLIC_KEY);

        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .toList();

        final String transactionSignature = solanaDriver.setUpgradeAuthority(
                Solana.account(Base58.decode(program)),
                Solana.account(Base58.decode(oldAuthorityPublicKey)),
                Solana.account(Base58.decode(newAuthority)),
                payer.getSolana4jPublicKey(),
                List.of(payer, new TestKeyPair(oldAuthorityPublicKey, oldAuthorityPrivateKey)),
                addressLookupTables);

        Waiter.waitFor(isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature).getTransaction()));
    }

    public void upgradeAuthority(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new OptionalArg("authority")
        );

        final String address = testContext.lookupOrLiteral(params.value("address"), TestDataType.TEST_PUBLIC_KEY);
        final String authority = testContext.lookupOrLiteral(params.value("authority"), TestDataType.TEST_PUBLIC_KEY);

        final ProgramDerivedAddress programDataAddress = BpfLoaderUpgradeableProgram.deriveAddress(Solana.account(Base58.decode(address)));

        final AccountInfo accountInfo = Waiter.waitFor(isNotNull(() -> solanaDriver.getAccountInfo(new TestPublicKey(programDataAddress.address().bytes()))));

        final byte[] accountInfoBytes = Base64.decode(accountInfo.getData().get(0));

        assertThat(Base58.encode(Arrays.copyOfRange(accountInfoBytes, 13, 45))).isEqualTo(authority);
    }

    private void waitForSlot(final long slot)
    {
        Waiter.waitFor(isTrue(() -> solanaDriver.getSlot() > slot));
    }

    private void waitForBlockHeight(final long blockHeight)
    {
        Waiter.waitFor(isTrue(() -> solanaDriver.getBlockHeight() > blockHeight));
    }
}
