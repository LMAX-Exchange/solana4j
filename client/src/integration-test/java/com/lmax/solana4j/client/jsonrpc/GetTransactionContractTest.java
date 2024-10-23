package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

// https://solana.com/docs/rpc/http/gettransaction
public class GetTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    @Disabled
    void shouldGetTransaction()
    {
        final String transactionSignature = api.requestAirdrop(payerAccount, new Sol(BigDecimal.ONE).lamports());

        final var transaction = Waiter.waitFor(Condition.isNotNull(() -> api.getTransaction(transactionSignature)));
    }
}
