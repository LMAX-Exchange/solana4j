package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getaccountinfo
class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetAccountInfo()
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : {
//            "context" : {
//                "apiVersion" : "1.18.25",
//                        "slot" : 479
//            },
//            "value" : {
//                "data" : [ "", "base64" ],
//                "executable" : false,
//                        "lamports" : 600000,
//                        "owner" : "11111111111111111111111111111111",
//                        "rentEpoch" : 18446744073709551615,
//                        "space" : 0
//            }
//        },
//            "id" : 4
//        }

        final var accountInfo = Waiter.waitFor(Condition.isNotNull(() -> api.getAccountInfo(payerAccount)));

        assertThat(accountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
        assertThat(accountInfo.getData().get(0)).isEqualTo("");
        assertThat(accountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(600000L);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
        assertThat(accountInfo.getSpace()).isEqualTo(0);
    }

    @Test
    @Disabled
    void shouldGetTokenAccountInfo()
    {
//        {
//            "jsonrpc" : "2.0",
//                "result" : {
//            "context" : {
//                "apiVersion" : "1.18.25",
//                        "slot" : 479
//            },
//            "value" : {
//                "data" : [ "", "base64" ],
//                "executable" : false,
//                        "lamports" : 600000,
//                        "owner" : "11111111111111111111111111111111",
//                        "rentEpoch" : 18446744073709551615,
//                        "space" : 0
//            }
//        },
//            "id" : 4
//        }

        final var tokenAccountInfo = Waiter.waitFor(Condition.isNotNull(() -> api.getAccountInfo(tokenAccount)));


//        assertThat(tokenAccountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
//        assertThat(tokenAccountInfo.getData().get(0)).isEqualTo("");
//        assertThat(tokenAccountInfo.getData().get(1)).isEqualTo("base64");
//        assertThat(tokenAccountInfo.getLamports()).isEqualTo(600000L);
//        assertThat(tokenAccountInfo.isExecutable()).isEqualTo(false);
//        assertThat(tokenAccountInfo.getRentEpoch()).isEqualTo("18446744073709551615");
//        assertThat(tokenAccountInfo.getSpace()).isEqualTo(0);
    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForAccount()
    {

    }

    @Test
    @Disabled
    void shouldThrowRpcExceptionForMalformedAccount()
    {

    }
}
