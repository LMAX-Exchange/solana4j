package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.Solana;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.junit.jupiter.api.BeforeEach;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class SolanaClientIntegrationTestBase extends IntegrationTestBase
{

    // TODO: sort out the below accounts - we do not need them all
    // TODO: bring in some kind of key generator as hard coding them in some tests is a bit nasty
    // TODO: ensure all fields are being read in at least a single test
    // TODO: refactor interfaces - perhaps combine them into logical units per endpoint
    protected final String dummyAccount = "4Nd1mnszWRVFzzsxMgcTzdFoC8Wx5mPQD9KZx3qtDr1M";
    // found in shared/src/test-support/resources/testcontainers/accounts
    protected final String tokenAccount = "BxoHF6TfQYXkpThMC7hVeex37U3duprVZuFAa7Akortf";
    // i want to ditch nonce account, it's kind of arbitrary
    protected final String nonceAccount = "6qS7fCwPYCSJ6msth7h1AB6g8aGe6rro1agHAamM4rAM";

    // payer account
    protected final String payerAccount = "sCR7NonpU3TrqvusEiA4MAwDMLfiY1gyVPqw2b36d8V";
    protected final String payerAccountPriv = "C3y41jMdtQeaF9yMBRLcZhMNoWhJNTS6neAR8fdT7CBR";

    // token accounts
    protected final String tokenMintAlt = "2tokpcExDmewsSNRKuTLVLMUseiSkEdBQWBjeQLmuFaS";

    protected final String tokenAccountAlt1 = "CZJWrh6o1tchnKB1UkGn2f65DDD7BbA1u6Nz6bZfTTNC";
    protected String mintToTokenAccount1TransactionBlobBase64;
    protected String mintToTokenAccount1TransactionBlobBase58;

    protected final String tokenAccountAlt2 = "7uy6uq8nz3GikmAnrULr7bRfxJKeqQ1SrfeVKtu1YLyy";
    protected String mintToTokenAccount2TransactionBlobBase58;
    protected String mintToTokenAccount2TransactionBlobBase64;

    protected final String tokenAccountAltOwner = "7H1itW7F72uJbaXK2R4gP7J18HrQ2M683kL9YgUeeUHr";
    protected final String tokenAccountAltOwnerPriv = "9pwyogLpuStHt7Hmuhe3Yh4PJJWw7jvTutfnM5ndQjHo";

    protected final String tokenMintAltAuthority = "6Q6XBfRrdf6jrK2DraQ8XnYzkGsFz9c15DdUKS5aJHoJ";
    protected final String tokenMintAltAuthorityPriv = "5DJqyvfAjjkhsT8sPNkRBmDfbhgvxeTMys8m4YKZ2u2z";

    protected SolanaApi api;

    @BeforeEach
    void moreSetup() throws SolanaJsonRpcClientException
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        Waiter.waitForConditionMet(Condition.isTrue(() ->
        {
            try
            {
                return api.getSlot().getResponse() > 35;
            }
            catch (SolanaJsonRpcClientException e)
            {
                throw new RuntimeException("Something went wrong in the test setup.", e);
            }
        }));

        final byte[] mintToTokenAccount1TransactionBytes = createMintToTransactionBytes(tokenAccountAlt1);

        mintToTokenAccount1TransactionBlobBase64 = Base64.getEncoder().encodeToString(mintToTokenAccount1TransactionBytes);
        mintToTokenAccount1TransactionBlobBase58 = SolanaEncoding.encodeBase58(mintToTokenAccount1TransactionBytes);

        final byte[] mintToTokenAccount2TransactionBytes = createMintToTransactionBytes(tokenAccountAlt2);

        mintToTokenAccount2TransactionBlobBase64 = Base64.getEncoder().encodeToString(mintToTokenAccount2TransactionBytes);
        mintToTokenAccount2TransactionBlobBase58 = SolanaEncoding.encodeBase58(mintToTokenAccount2TransactionBytes);
    }

    protected TransactionResponse waitForTransactionSuccess(final String transactionSignature)
    {
        return waitForTransactionSuccess(transactionSignature, Optional.empty());
    }

    protected TransactionResponse waitForTransactionSuccess(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        return Waiter.waitForConditionMet(transactionResponseIsNotNull(transactionSignature, maybeOptionalParams));
    }

    protected void waitForTransactionFailure(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        Waiter.waitForConditionNotMet(transactionResponseIsNotNull(transactionSignature, maybeOptionalParams));
    }

    private Condition<TransactionResponse> transactionResponseIsNotNull(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        return Condition.isNotNull(() ->
        {
            try
            {
                if (maybeOptionalParams.isPresent())
                {
                    return api.getTransaction(transactionSignature, maybeOptionalParams.get()).getResponse();
                }
                else
                {
                    return api.getTransaction(transactionSignature).getResponse();
                }
            }
            catch (SolanaJsonRpcClientException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    private byte[] createMintToTransactionBytes(final String tokenAccountAlt1) throws SolanaJsonRpcClientException
    {
        return Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(payerAccount),
                Solana.blockhash(api.getLatestBlockhash().getResponse().getBlockhashBase58()),
                Solana.account(tokenMintAlt),
                Solana.account(tokenMintAltAuthority),
                Solana.destination(Solana.account(tokenAccountAlt1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(payerAccount), SolanaEncoding.decodeBase58(payerAccountPriv)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(tokenMintAltAuthority), SolanaEncoding.decodeBase58(tokenMintAltAuthorityPriv))
                )
        );
    }
}
