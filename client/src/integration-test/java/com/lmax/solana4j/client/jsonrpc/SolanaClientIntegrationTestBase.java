package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.IntegrationTestBase;
import com.lmax.solana4j.Solana;
import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.SolanaApi;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

abstract class SolanaClientIntegrationTestBase extends IntegrationTestBase
{
    protected static final String PAYER = "sCR7NonpU3TrqvusEiA4MAwDMLfiY1gyVPqw2b36d8V";
    protected static final String PAYER_PRIV = "C3y41jMdtQeaF9yMBRLcZhMNoWhJNTS6neAR8fdT7CBR";

    // below accounts found in shared/src/test-support/resources/testcontainers/accounts

    protected static final String SOL_ACCOUNT = "4Nd1mnszWRVFzzsxMgcTzdFoC8Wx5mPQD9KZx3qtDr1M";

    protected static final String TOKEN_MINT = "2tokpcExDmewsSNRKuTLVLMUseiSkEdBQWBjeQLmuFaS";

    protected static final String TOKEN_ACCOUNT_1 = "CZJWrh6o1tchnKB1UkGn2f65DDD7BbA1u6Nz6bZfTTNC";
    protected static String tokenMintTransactionSignature1;

    protected static final String TOKEN_ACCOUNT_2 = "7uy6uq8nz3GikmAnrULr7bRfxJKeqQ1SrfeVKtu1YLyy";

    protected static final String TOKEN_ACCOUNT_OWNER = "7H1itW7F72uJbaXK2R4gP7J18HrQ2M683kL9YgUeeUHr";
    protected static final String TOKEN_ACCOUNT_OWNER_PRIV = "9pwyogLpuStHt7Hmuhe3Yh4PJJWw7jvTutfnM5ndQjHo";

    protected static final String TOKEN_MINT_AUTHORITY = "6Q6XBfRrdf6jrK2DraQ8XnYzkGsFz9c15DdUKS5aJHoJ";
    protected static final String TOKEN_MINT_AUTHORITY_PRIV = "5DJqyvfAjjkhsT8sPNkRBmDfbhgvxeTMys8m4YKZ2u2z";

    protected static final SolanaApi SOLANA_API = new SolanaJsonRpcClient(solanaRpcUrl, true);

    static
    {
        try
        {
            waitForSlot(35);

            final var airdropTransactionSignature = SOLANA_API.requestAirdrop(PAYER, 1000000).getResponse();
            waitForTransactionSuccess(airdropTransactionSignature);

            final byte[] mintToTokenAccount1TransactionBytes = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                    Solana.account(PAYER),
                    Solana.blockhash(SOLANA_API.getLatestBlockhash().getResponse().getBlockhashBase58()),
                    Solana.account(TOKEN_MINT),
                    Solana.account(TOKEN_MINT_AUTHORITY),
                    SolanaEncoding.destination(Solana.account(TOKEN_ACCOUNT_1), 10),
                    List.of(
                            new Solana4jJsonRpcTestHelper.Signer(Solana.account(PAYER), SolanaEncoding.decodeBase58(PAYER_PRIV)),
                            new Solana4jJsonRpcTestHelper.Signer(Solana.account(TOKEN_MINT_AUTHORITY), SolanaEncoding.decodeBase58(TOKEN_MINT_AUTHORITY_PRIV))
                    )
            );

            tokenMintTransactionSignature1 = SOLANA_API.sendTransaction(Base64.getEncoder().encodeToString(mintToTokenAccount1TransactionBytes)).getResponse();
            waitForTransactionSuccess(tokenMintTransactionSignature1);
        }
        catch (final SolanaJsonRpcClientException e)
        {
            throw new RuntimeException("Something went wrong in the test set-up.", e);
        }
    }

    protected static TransactionResponse waitForTransactionSuccess(final String transactionSignature)
    {
        return waitForTransactionSuccess(transactionSignature, Optional.empty());
    }

    protected static TransactionResponse waitForTransactionSuccess(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        return Waiter.waitForConditionMet(transactionResponseIsNotNull(transactionSignature, maybeOptionalParams));
    }

    protected static void waitForTransactionFailure(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        Waiter.waitForConditionNotMet(transactionResponseIsNotNull(transactionSignature, maybeOptionalParams));
    }

    private static void waitForSlot(final int slotNumber)
    {
        Waiter.waitForConditionMet(Condition.isTrue(() ->
        {
            try
            {
                return SOLANA_API.getSlot().getResponse() > slotNumber;
            }
            catch (SolanaJsonRpcClientException e)
            {
                throw new RuntimeException("Something went wrong in the test setup.", e);
            }
        }));
    }

    private static Condition<TransactionResponse> transactionResponseIsNotNull(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        return Condition.isNotNull(() ->
        {
            try
            {
                if (maybeOptionalParams.isPresent())
                {
                    return SOLANA_API.getTransaction(transactionSignature, maybeOptionalParams.get()).getResponse();
                }
                else
                {
                    return SOLANA_API.getTransaction(transactionSignature).getResponse();
                }
            }
            catch (SolanaJsonRpcClientException e)
            {
                throw new RuntimeException(e);
            }
        });
    }
}
