package com.lmax.solana4j;

import com.lmax.simpledsl.api.DslParams;
import com.lmax.simpledsl.api.OptionalArg;
import com.lmax.simpledsl.api.RepeatingArgGroup;
import com.lmax.simpledsl.api.RepeatingGroup;
import com.lmax.simpledsl.api.RequiredArg;
import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.SolanaClient;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.domain.Sol;
import com.lmax.solana4j.domain.TestKeyPair;
import com.lmax.solana4j.domain.TestKeyPairGenerator;
import com.lmax.solana4j.domain.TestPublicKey;
import com.lmax.solana4j.domain.TokenProgram;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AssociatedTokenProgram;
import com.lmax.solana4j.programs.BpfLoaderUpgradeableProgram;
import com.lmax.solana4j.programs.SystemProgram;
import com.lmax.solana4j.programs.TokenProgramBase;
import com.lmax.solana4j.store.TestContext;
import com.lmax.solana4j.store.TestDataType;
import org.bouncycastle.util.encoders.Base64;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static com.lmax.solana4j.programs.SystemProgram.MINT_ACCOUNT_LENGTH;
import static com.lmax.solana4j.programs.SystemProgram.NONCE_ACCOUNT_LENGTH;
import static com.lmax.solana4j.programs.TokenProgram.ACCOUNT_LAYOUT_SPAN;
import static com.lmax.solana4j.programs.TokenProgram.MULTI_SIG_LAYOUT_SPAN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SolanaNodeDsl
{
    private final SolanaDriver solanaDriver;
    private final TestContext testContext;

    public SolanaNodeDsl(final String rpcUrl)
    {
        this.solanaDriver = new SolanaDriver(SolanaClient.create(HttpClient.newHttpClient(), rpcUrl));
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

        final TestKeyPair testKeyPair = TestKeyPairGenerator.generateTestKeyPair();

        testContext.data(TestDataType.TEST_KEY_PAIR).store(params.value("alias"), testKeyPair);
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("alias"), new TestPublicKey(testKeyPair.getPublicKeyBytes()));
    }

    public void airdropSol(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amountSol"));

        final String address = testContext.lookupOrLiteral(params.value("address"), TestDataType.TEST_PUBLIC_KEY);
        final long lamports = Sol.lamports(params.valueAsBigDecimal("amountSol"));

        final String transactionSignature = solanaDriver.requestAirdrop(new TestPublicKey(SolanaEncoding.decodeBase58(address)), lamports);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));
    }

    public void verifyAddressLookupTable(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("lookupTableAddress"),
                new OptionalArg("addresses").setAllowMultipleValues()
        );

        final TestPublicKey lookupTableAddress = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("lookupTableAddress"));

        final List<PublicKey> expectedAddresses = Arrays.stream(params.values("addresses"))
                .map(address -> testContext.data(TestDataType.TEST_KEY_PAIR).lookup(address).getSolana4jPublicKey())
                .collect(Collectors.toList());

        final AddressLookupTable addressLookupTable = storeAddressLookupTable(lookupTableAddress, params.value("lookupTableAddress"));

        assertThat(addressLookupTable.getAddresses()).usingRecursiveComparison().isEqualTo(expectedAddresses);
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
        final TestPublicKey lookupTableAddress = new TestPublicKey(programDerivedAddress.address().bytes());
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("lookupTableAddress"), lookupTableAddress);

        // intermittency caused by slot not moving on to the next slot before including in the sent transaction
        waitForSlot(recentSlot + 1);
        final String transactionSignature = solanaDriver.createAddressLookupTable(programDerivedAddress, authority, payer, SolanaEncoding.slot(recentSlot));
        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));

        storeAddressLookupTable(lookupTableAddress, params.value("lookupTableAddress"));
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

        final TestPublicKey lookupTableAddress = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("lookupTableAddress"));
        final TestKeyPair authority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("authority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final List<TestPublicKey> addresses = new ArrayList<>();
        for (final String address : params.values("addresses"))
        {
            addresses.add(testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(address));
        }

        final String transactionSignature = solanaDriver.extendAddressLookupTable(lookupTableAddress, authority, payer, addresses, addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));

        storeAddressLookupTable(lookupTableAddress, params.value("lookupTableAddress"));
    }

    public void waitForSlot(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("slot")
        );

        Waiter.waitForConditionMet(Condition.isTrue(() -> solanaDriver.getSlot() > params.valueAsLong("slot")));
    }

    public void createMintAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("tokenMint"),
                new RequiredArg("decimals"),
                new RequiredArg("mintAuthority"),
                new RequiredArg("freezeAuthority"),
                new RequiredArg("payer"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair tokenMint = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("tokenMint"));
        final int decimals = params.valueAsInt("decimals");
        final TestKeyPair mintAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("mintAuthority"));
        final TestKeyPair freezeAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("freezeAuthority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.createMintAccount(
                tokenProgram,
                tokenMint,
                decimals,
                mintAuthority,
                freezeAuthority,
                payer,
                MINT_ACCOUNT_LENGTH,
                addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));
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
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.mintTo(tokenProgram, tokenMint, to, amount, authority, payer, addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));
    }

    public void verifyMintAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("tokenMint"),
                new RequiredArg("mintAuthority"),
                new RequiredArg("freezeAuthority"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token")
        );

        final TestPublicKey tokenMint = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenMint"));
        final TestPublicKey mintAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("mintAuthority"));
        final TestPublicKey freezeAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("freezeAuthority"));

        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));

        final AccountInfo accountInfo = solanaDriver.getAccountInfo(tokenMint);

        byte[] accountInfoBytes = Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0));

        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, 4, 36))).isEqualTo(mintAuthority.getPublicKeyBase58());
        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, 50, 82))).isEqualTo(freezeAuthority.getPublicKeyBase58());
        assertThat(accountInfo.getOwner()).isEqualTo(tokenProgram.getProgram().base58());
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
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.createTokenAccount(
                tokenProgram,
                account,
                owner,
                tokenMint,
                payer,
                ACCOUNT_LAYOUT_SPAN,
                addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));
    }

    public void tokenBalance(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amount"));

        final TestPublicKey address = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("address"));
        final String amount = params.value("amount");

        Waiter.waitForConditionMet(Condition.isEqualTo(amount, () -> solanaDriver.getTokenBalance(address.getPublicKeyBase58()).getUiAmountString()));
    }

    public void solBalance(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new RequiredArg("amountSol"));

        final TestPublicKey address = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("address"));
        final long lamports = Sol.lamports(params.valueAsBigDecimal("amountSol"));

        Waiter.waitForConditionMet(Condition.isEqualTo(lamports, () -> solanaDriver.getBalance(address.getPublicKeyBase58())));
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
                .collect(Collectors.toList());
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.tokenTransfer(tokenProgram, from, to, owner, amount, payer, signers, addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));
    }

    public void verifyTokenAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("tokenAccount"),
                new RequiredArg("tokenMint"),
                new RequiredArg("tokenAccountAuthority"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token")
        );

        final TestPublicKey tokenAccount = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenAccount"));
        final TestPublicKey tokenMint = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenMint"));
        final TestPublicKey tokenAccountAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenAccountAuthority"));

        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));

        final AccountInfo accountInfo = solanaDriver.getAccountInfo(tokenAccount);

        byte[] accountInfoBytes = Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0));

        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, 0, 32))).isEqualTo(tokenMint.getPublicKeyBase58());
        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, 32, 64))).isEqualTo(tokenAccountAuthority.getPublicKeyBase58());
        assertThat(accountInfo.getOwner()).isEqualTo(tokenProgram.getProgram().base58());
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
                .collect(Collectors.toList());
        final int requiredSigners = params.valueAsInt("requiredSigners");
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.createMultiSigAccount(
                tokenProgram,
                multiSig,
                multiSigSigners,
                requiredSigners,
                MULTI_SIG_LAYOUT_SPAN,
                payer,
                addressLookupTables
        );

        Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature)));
    }

    public void verifyMultiSigAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("multiSigAccount"),
                new RequiredArg("multiSigSigners").setAllowMultipleValues(),
                new RequiredArg("requiredSigners"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token")
        );

        final TestPublicKey multiSigAccount = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("multiSigAccount"));
        final List<PublicKey> multiSigSigners = params.valuesAsList("multiSigSigners")
                .stream()
                .map(pk -> testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(pk).getSolana4jPublicKey())
                .collect(Collectors.toList());
        final int requiredSigners = params.valueAsInt("requiredSigners");
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));

        final AccountInfo accountInfo = solanaDriver.getAccountInfo(multiSigAccount);

        byte[] accountInfoBytes = Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0));

        assertThat(accountInfoBytes[0]).isEqualTo((byte) requiredSigners);
        assertThat(accountInfoBytes[1]).isEqualTo((byte) multiSigSigners.size());
        assertThat(accountInfo.getOwner()).isEqualTo(tokenProgram.getProgram().base58());

        for (int i = 0; i < multiSigSigners.size(); i++)
        {
            final int signerStartIndex = 3 + (32 * i);
            final int signerEndIndex = signerStartIndex + 32;
            assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, signerStartIndex, signerEndIndex))).isEqualTo(multiSigSigners.get(i).base58());
        }
    }

    public void createNonceAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("nonceAccount"),
                new RequiredArg("nonceAuthority"),
                new RequiredArg("payer"),
                new RequiredArg("nonceAccountValue"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair nonceAccount = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("nonceAccount"));
        final TestKeyPair nonceAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("nonceAuthority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.createNonceAccount(nonceAccount, nonceAuthority, payer, NONCE_ACCOUNT_LENGTH, addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));

        final PublicKey nonceAccountValue = SystemProgram.getNonceAccountValue(
                Base64.decode(solanaDriver.getAccountInfo(nonceAccount.getPublicKey()).getData().getAccountInfoEncoded().get(0))
        );

        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("nonceAccountValue"), new TestPublicKey(nonceAccountValue.bytes()));
    }

    public void advanceNonce(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("nonceAccount"),
                new RequiredArg("nonceAuthority"),
                new RequiredArg("payer"),
                new RequiredArg("newNonceAccountValue"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestKeyPair nonceAccount = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("nonceAccount"));
        final TestKeyPair nonceAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("nonceAuthority"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.advanceNonce(nonceAccount, nonceAuthority, payer, addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));

        final PublicKey newNonceAccountValue = SystemProgram.getNonceAccountValue(
                Base64.decode(solanaDriver.getAccountInfo(nonceAccount.getPublicKey()).getData().getAccountInfoEncoded().get(0))
        );

        testContext.data(TestDataType.TEST_PUBLIC_KEY).store("newNonceAccountValue", new TestPublicKey(newNonceAccountValue.bytes()));
    }

    public void verifyNonceAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("nonceAccount"),
                new RequiredArg("nonceAuthority"),
                new RequiredArg("nonceAccountValue")
        );

        final TestPublicKey nonceAccount = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("nonceAccount"));
        final TestPublicKey expectedNonceAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("nonceAuthority"));
        final TestPublicKey expectedNonceAccountValue = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("nonceAccountValue"));

        final AccountInfo accountInfo = solanaDriver.getAccountInfo(nonceAccount);

        final byte[] accountInfoBytes = Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0));

        final PublicKey actualNonceAccountValue = SystemProgram.getNonceAccountValue(Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0)));

        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, 8, 40))).isEqualTo(expectedNonceAuthority.getPublicKeyBase58());
        assertThat(actualNonceAccountValue).isEqualTo((expectedNonceAccountValue.getSolana4jPublicKey()));
    }

    public void transferSol(final String... args)
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
        final long lamports = Sol.lamports(params.valueAsBigDecimal("amountSol"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.transfer(from, to, lamports, payer, addressLookupTables);

        Waiter.waitForConditionMet(transactionFinalized(transactionSignature));
    }

    public void createAssociatedTokenAccount(final String... args)
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

        final ProgramDerivedAddress associatedTokenAddress = AssociatedTokenProgram.deriveAddress(owner.getSolana4jPublicKey(), tokenProgram.getProgram(), tokenMint.getSolana4jPublicKey());
        testContext.data(TestDataType.TEST_PUBLIC_KEY).store(params.value("associatedTokenAddress"), new TestPublicKey(associatedTokenAddress.address().bytes()));

        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.createAssociatedTokenAccount(
                tokenProgram,
                associatedTokenAddress,
                tokenMint.getSolana4jPublicKey(),
                owner.getSolana4jPublicKey(),
                params.valueAsBoolean("idempotent"),
                payer,
                addressLookupTables
        );

        Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature)));
    }

    public void verifyAssociatedTokenAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("associatedTokenAddress"),
                new RequiredArg("tokenMint"),
                new RequiredArg("owner"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token")
        );

        final TestPublicKey associatedTokenAddress = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("associatedTokenAddress"));
        final TestPublicKey tokenMint = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenMint"));
        final TestPublicKey owner = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("owner"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));

        final AccountInfo accountInfo = solanaDriver.getAccountInfo(associatedTokenAddress);

        final byte[] accountDataBytes = Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0));

        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountDataBytes, 0, 32))).isEqualTo(tokenMint.getPublicKeyBase58());
        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountDataBytes, 32, 64))).isEqualTo(owner.getPublicKeyBase58());
        assertThat(accountInfo.getOwner()).isEqualTo(tokenProgram.getProgram().base58());
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

    public void setTokenAccountAuthority(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("tokenAccount"),
                new RequiredArg("tokenAccountOldAuthority"),
                new RequiredArg("tokenAccountNewAuthority"),
                new RequiredArg("authorityType").setAllowedValues(TokenProgramBase.AuthorityType.class),
                new RequiredArg("payer"),
                new OptionalArg("tokenProgram").setAllowedValues("Token", "Token2022").setDefault("Token"),
                new OptionalArg("addressLookupTables").setAllowMultipleValues()
        );

        final TestPublicKey tokenAccount = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenAccount"));
        final TestKeyPair tokenAccountOldAuthority = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("tokenAccountOldAuthority"));
        final TestPublicKey tokenAccountNewAuthority = testContext.data(TestDataType.TEST_PUBLIC_KEY).lookup(params.value("tokenAccountNewAuthority"));
        final TokenProgramBase.AuthorityType authorityType = TokenProgramBase.AuthorityType.valueOf(params.value("authorityType"));
        final TokenProgram tokenProgram = TokenProgram.fromName(params.value("tokenProgram"));
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.setTokenAccountAuthority(
                tokenProgram,
                tokenAccount.getSolana4jPublicKey(),
                tokenAccountOldAuthority.getSolana4jPublicKey(),
                tokenAccountNewAuthority.getSolana4jPublicKey(),
                authorityType,
                payer,
                List.of(payer, tokenAccountOldAuthority),
                addressLookupTables);

        Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature)));
    }

    public void setComputeUnits(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("computeUnitLimit"),
                new RequiredArg("computeUnitPrice"),
                new RequiredArg("payer"),
                new OptionalArg("rememberTransactionAs"),
                new OptionalArg("expectedErrorCode")
        );

        final int computeUnitLimit = params.valueAsInt("computeUnitLimit");
        final int computeUnitPrice = params.valueAsInt("computeUnitPrice");
        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));
        final String rememberTransactionAs = params.value("rememberTransactionAs");
        final OptionalInt expectedErrorCode = params.valuesAsOptionalInt("expectedErrorCode");

        final SolanaClientResponse<String> solanaClientResponse = solanaDriver.setComputeUnits(
                computeUnitLimit,
                computeUnitPrice,
                payer,
                expectedErrorCode);

        if (expectedErrorCode.isPresent())
        {
            assertThat(solanaClientResponse.isSuccess()).withFailMessage(
                    "We expected an error but received a successful response from the client."
            ).isFalse();
            assertThat(solanaClientResponse.getError().getErrorCode()).isEqualTo(expectedErrorCode.getAsInt());
        }
        else
        {
            assertThat(solanaClientResponse.isSuccess()).withFailMessage(
                    "We expected a success but received a an error from the client."
            ).isTrue();
            Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getTransactionResponse(solanaClientResponse.getResponse())));

            testContext.data(TestDataType.TRANSACTION_ID).store(rememberTransactionAs, solanaClientResponse.getResponse());
        }
    }

    public void setBpfUpgradeableProgramUpgradeAuthority(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("bpfUpgradeableProgram"),
                new RequiredArg("oldUpgradeAuthorityPublicKey"),
                new RequiredArg("oldUpgradeAuthorityPrivateKey"),
                new RequiredArg("newUpgradeAuthority"),
                new RequiredArg("payer"),
                new OptionalArg("addressLookupTables")
        );

        final String bpfUpgradeableProgram = testContext.lookupOrLiteral(params.value("bpfUpgradeableProgram"), TestDataType.TEST_PUBLIC_KEY);
        final String oldUpgradeAuthorityPublicKey = testContext.lookupOrLiteral(params.value("oldUpgradeAuthorityPublicKey"), TestDataType.TEST_PUBLIC_KEY);
        final String oldUpgradeAuthorityPrivateKey = testContext.lookupOrLiteral(params.value("oldUpgradeAuthorityPrivateKey"), TestDataType.TEST_KEY_PAIR);

        final String newUpgradeAuthority = testContext.lookupOrLiteral(params.value("newUpgradeAuthority"), TestDataType.TEST_PUBLIC_KEY);

        final TestKeyPair payer = testContext.data(TestDataType.TEST_KEY_PAIR).lookup(params.value("payer"));

        final List<AddressLookupTable> addressLookupTables = params.valuesAsList("addressLookupTables").stream()
                .map(testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE)::lookup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final String transactionSignature = solanaDriver.setBpfUpgradeableProgramUpgradeAuthority(
                Solana.account(bpfUpgradeableProgram),
                Solana.account(oldUpgradeAuthorityPublicKey),
                Solana.account(newUpgradeAuthority),
                payer.getSolana4jPublicKey(),
                List.of(payer, new TestKeyPair(oldUpgradeAuthorityPublicKey, oldUpgradeAuthorityPrivateKey)),
                addressLookupTables);

        Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature)));
    }

    public void verifyBpfUpgradeableAccount(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("address"),
                new OptionalArg("upgradeAuthority")
        );

        final String address = testContext.lookupOrLiteral(params.value("address"), TestDataType.TEST_PUBLIC_KEY);
        final String upgradeAuthority = testContext.lookupOrLiteral(params.value("upgradeAuthority"), TestDataType.TEST_PUBLIC_KEY);

        final ProgramDerivedAddress programDataAddress = BpfLoaderUpgradeableProgram.deriveAddress(Solana.account(SolanaEncoding.decodeBase58(address)));

        final AccountInfo accountInfo = Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getAccountInfo(new TestPublicKey(programDataAddress.address().bytes()))));

        final byte[] accountInfoBytes = Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0));

        assertThat(SolanaEncoding.encodeBase58(Arrays.copyOfRange(accountInfoBytes, 13, 45))).isEqualTo(upgradeAuthority);
        assertThat(accountInfo.getOwner()).isEqualTo("BPFLoaderUpgradeab1e11111111111111111111111");
    }


    public void verifyTransactionMetadata(final String... args)
    {
        final DslParams params = DslParams.create(
                args,
                new RequiredArg("transaction"),
                new OptionalArg("computeUnitsConsumed")
        );

        final String transactionId = testContext.data(TestDataType.TRANSACTION_ID).lookup(params.value("transaction"));
        final long computeUnitsConsumed = params.valueAsLong("computeUnitsConsumed");

        final TransactionResponse transactionResponse = solanaDriver.getTransactionResponse(transactionId);

        assertThat(transactionResponse.getMetadata().getComputeUnitsConsumed()).isEqualTo(computeUnitsConsumed);
    }

    private void waitForSlot(final long slot)
    {
        Waiter.waitForConditionMet(Condition.isTrue(() -> solanaDriver.getSlot() > slot));
    }

    private Condition<TransactionResponse> transactionFinalized(final String transactionSignature)
    {
        return Condition.isNotNull(() -> solanaDriver.getTransactionResponse(transactionSignature));
    }

    private AddressLookupTable storeAddressLookupTable(final TestPublicKey lookupTableAddress, final String lookupTableAlias)
    {
        final AccountInfo accountInfo = Waiter.waitForConditionMet(Condition.isNotNull(() -> solanaDriver.getAccountInfo(lookupTableAddress)));
        final AddressLookupTable addressLookupTable = AddressLookupTableProgram.deserializeAddressLookupTable(
                lookupTableAddress.getSolana4jPublicKey(),
                Base64.decode(accountInfo.getData().getAccountInfoEncoded().get(0)));
        testContext.data(TestDataType.ADDRESS_LOOKUP_TABLE).store(lookupTableAlias, addressLookupTable);
        return addressLookupTable;
    }
}
