package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

public class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    protected final String dummyAccount = "4Nd1mnszWRVFzzsxMgcTzdFoC8Wx5mPQD9KZx3qtDr1M";
    // found in shared/src/test-support/resources/testcontainers/accounts
    protected final String payerAccount = "sCR7NonpU3TrqvusEiA4MAwDMLfiY1gyVPqw2b36d8V";
    protected final String tokenAccount = "BxoHF6TfQYXkpThMC7hVeex37U3duprVZuFAa7Akortf";
    // i want to ditch nonce account, it's kind of arbitrary
    protected final String nonceAccount = "6qS7fCwPYCSJ6msth7h1AB6g8aGe6rro1agHAamM4rAM";
    // i kind of only want one set of token accounts
    protected final String tokenAccountAlt1 = "CZJWrh6o1tchnKB1UkGn2f65DDD7BbA1u6Nz6bZfTTNC";
    protected final String tokenAccountAlt2 = "7uy6uq8nz3GikmAnrULr7bRfxJKeqQ1SrfeVKtu1YLyy";

    protected SolanaApi api;

    @BeforeEach
    void moreSetup()
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        Waiter.waitFor(Condition.isTrue(() ->
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
    }

    protected TransactionResponse waitForTransactionSuccess(final String transactionSignature)
    {
        return waitForTransactionSuccess(transactionSignature, Optional.empty());
    }

    protected TransactionResponse waitForTransactionSuccess(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        return Waiter.waitFor(Condition.isNotNull(() ->
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
        }));
    }
}
