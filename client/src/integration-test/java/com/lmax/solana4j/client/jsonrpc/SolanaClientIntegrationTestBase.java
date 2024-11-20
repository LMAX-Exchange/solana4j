package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.Solana;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    // TODO: bring in some kind of key generator as hard coding them in some tests is a bit nasty
    // TODO: ensure all fields are being read in at least a single test
    // TODO: refactor interfaces - perhaps combine them into logical units per endpoint
    protected final String payer = "sCR7NonpU3TrqvusEiA4MAwDMLfiY1gyVPqw2b36d8V";
    protected final String payerPriv = "C3y41jMdtQeaF9yMBRLcZhMNoWhJNTS6neAR8fdT7CBR";

    // below accounts found in shared/src/test-support/resources/testcontainers/accounts

    // solana account
    protected final String solAccount = "4Nd1mnszWRVFzzsxMgcTzdFoC8Wx5mPQD9KZx3qtDr1M";

    // token accounts
    protected final String tokenMint = "2tokpcExDmewsSNRKuTLVLMUseiSkEdBQWBjeQLmuFaS";

    protected final String tokenAccount1 = "CZJWrh6o1tchnKB1UkGn2f65DDD7BbA1u6Nz6bZfTTNC";
    protected String mintToTokenAccount1TransactionSignature;

    protected final String tokenAccount2 = "7uy6uq8nz3GikmAnrULr7bRfxJKeqQ1SrfeVKtu1YLyy";
    protected byte[] mintToTokenAccount2TransactionBytes;

    protected final String tokenAccountOwner = "7H1itW7F72uJbaXK2R4gP7J18HrQ2M683kL9YgUeeUHr";
    protected final String tokenAccountAltPriv = "9pwyogLpuStHt7Hmuhe3Yh4PJJWw7jvTutfnM5ndQjHo";

    protected final String tokenMintAuthority = "6Q6XBfRrdf6jrK2DraQ8XnYzkGsFz9c15DdUKS5aJHoJ";
    protected final String tokenMintAuthorityPriv = "5DJqyvfAjjkhsT8sPNkRBmDfbhgvxeTMys8m4YKZ2u2z";

    protected SolanaApi api;

    @BeforeAll
    void setupOnceSolanaClientBase() throws SolanaJsonRpcClientException
    {
        api = new SolanaJsonRpcClient(rpcUrl, true);

        waitForSlot(35);

        final var airdropTransactionSignature = api.requestAirdrop(payer, 1000000).getResponse();
        waitForTransactionSuccess(airdropTransactionSignature);

        final byte[] mintToTokenAccount1TransactionBytes = createMintToTransactionBytes(tokenAccount1);
        mintToTokenAccount1TransactionSignature = api.sendTransaction(Base64.getEncoder().encodeToString(mintToTokenAccount1TransactionBytes)).getResponse();
        waitForTransactionSuccess(mintToTokenAccount1TransactionSignature);

        mintToTokenAccount2TransactionBytes = createMintToTransactionBytes(tokenAccount2);
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

    private void waitForSlot(final int slotNumber)
    {
        Waiter.waitForConditionMet(Condition.isTrue(() ->
        {
            try
            {
                return api.getSlot().getResponse() > slotNumber;
            }
            catch (SolanaJsonRpcClientException e)
            {
                throw new RuntimeException("Something went wrong in the test setup.", e);
            }
        }));
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
                Solana.account(payer),
                Solana.blockhash(api.getLatestBlockhash().getResponse().getBlockhashBase58()),
                Solana.account(tokenMint),
                Solana.account(tokenMintAuthority),
                Solana.destination(Solana.account(tokenAccountAlt1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(payer), SolanaEncoding.decodeBase58(payerPriv)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(tokenMintAuthority), SolanaEncoding.decodeBase58(tokenMintAuthorityPriv))
                )
        );
    }
}
