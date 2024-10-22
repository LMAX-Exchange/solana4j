package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetAccountInfoContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetAccountInfo()
    {
//        {
//            "jsonrpc": "2.0",
//                "result": {
//            "context": {
//                "apiVersion": "1.18.25",
//                        "slot": 733
//            },
//            "value": {
//                "data": [
//                "",
//                        "base64"
//      ],
//                "executable": false,
//                        "lamports": 1000000000,
//                        "owner": "11111111111111111111111111111111",
//                        "rentEpoch": 18446744073709551615,
//                        "space": 0
//            }
//        },
//            "id": 8
//        }


        final var accountInfo = Waiter.waitFor(Condition.isNotNull(() -> api.getAccountInfo(account)));

        assertThat(accountInfo.getOwner()).isEqualTo("11111111111111111111111111111111");
        assertThat(accountInfo.getData().get(0)).isEqualTo("");
        assertThat(accountInfo.getData().get(1)).isEqualTo("base64");
        assertThat(accountInfo.getLamports()).isEqualTo(1000000000);
        assertThat(accountInfo.isExecutable()).isEqualTo(false);
        assertThat(accountInfo.getRentEpoch()).isEqualTo(0);
        assertThat(accountInfo.getSpace()).isEqualTo(0);
    }
}
