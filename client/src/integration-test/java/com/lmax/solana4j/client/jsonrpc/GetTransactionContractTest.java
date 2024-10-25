package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

// https://solana.com/docs/rpc/http/gettransaction
class GetTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    @Disabled
    void shouldGetTransaction()
    {
        {
//            "jsonrpc" : "2.0",
//                "result" : {
//            "blockTime" : 1729734716,
//                    "meta" : {
//                "computeUnitsConsumed" : 150,
//                        "err" : null,
//                        "fee" : 5000,
//                        "innerInstructions" : [ ],
//                "loadedAddresses" : {
//                    "readonly" : [ ],
//                    "writable" : [ ]
//                },
//                "logMessages" : [ "Program 11111111111111111111111111111111 invoke [1]", "Program 11111111111111111111111111111111 success" ],
//                "postBalances" : [ 999998999995000, 1000600000, 1 ],
//                "postTokenBalances" : [ ],
//                "preBalances" : [ 1000000000000000, 600000, 1 ],
//                "preTokenBalances" : [ ],
//                "rewards" : [ ],
//                "status" : {
//                    "Ok" : null
//                }
//            },
//            "slot" : 350,
//                    "transaction" : {
//                "message" : {
//                    "accountKeys" : [ "H7WjSbrW6sdg6yZ6x7KFKxeiKuBqiH4vmgxkQ7aNu32W", "EjmK3LTW8oBSJp14zvQ55PMvPJCuQwRrnjd17P4vrQYo", "11111111111111111111111111111111" ],
//                    "header" : {
//                        "numReadonlySignedAccounts" : 0,
//                                "numReadonlyUnsignedAccounts" : 1,
//                                "numRequiredSignatures" : 1
//                    },
//                    "instructions" : [ {
//                        "accounts" : [ 0, 1 ],
//                        "data" : "3Bxs3zzLZLuLQEYX",
//                                "programIdIndex" : 2,
//                                "stackHeight" : null
//                    } ],
//                    "recentBlockhash" : "Btxp5yqAkyqJ4C17zVmTKjcDkcPD9xvs3fZm9VD1Mxbq"
//                },
//                "signatures" : [ "4eZ5dKrog18DTHuqkb7mguksf1LprW7jG4EThCVfMvwcSgnqueMvFx9AibYRh5s52sJRN4gCGPcCSM47s9Ns3s2z" ]
//            },
//            "version" : "legacy"
//        },
//            "id" : 5
//        }

            final String transactionSignature = api.requestAirdrop(payerAccount, new Sol(BigDecimal.ONE).lamports());

            final var transaction = Waiter.waitFor(Condition.isNotNull(() -> api.getTransaction(transactionSignature)));
        }
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForUnknownTransactionSignature()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedTransactionSignature()
    {

    }
}
