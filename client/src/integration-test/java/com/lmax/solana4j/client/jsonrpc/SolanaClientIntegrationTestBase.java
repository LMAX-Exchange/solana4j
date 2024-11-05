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
    protected final String payerAccount = "EjmK3LTW8oBSJp14zvQ55PMvPJCuQwRrnjd17P4vrQYo";
    protected final String tokenAccount = "BxoHF6TfQYXkpThMC7hVeex37U3duprVZuFAa7Akortf";
    protected final String tokenMintAccount = "x9wKn11SwXuSgYf3AyUgmw1vLq1XawuqxeXNwbda4Kg";
    protected final String nonceAccount = "6qS7fCwPYCSJ6msth7h1AB6g8aGe6rro1agHAamM4rAM";
    protected final String associatedTokenAccount = "4swkJXi9iwk1sRB4QAk4ZmX1WCWfpP9ButmqCfkEyaqC";
    protected final String multiSigAccount = "13SkCFWQiqAN4YTdH3SxNoUNL4RvcxCxMXXQxpJue1UW";

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
