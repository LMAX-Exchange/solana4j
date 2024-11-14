package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SendTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldSendTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final String latestBlockhash = api.getLatestBlockhash().getResponse().getBlockhashBase58();

        final String mintToTransactionBlob = Solana4jJsonRpcTestHelper.createMintToTransactionBlob(
                Solana.account(payerAccount),
                Solana.blockhash(latestBlockhash),
                Solana.account(tokenMintAlt),
                Solana.account(tokenMintAltAuthority),
                Solana.destination(Solana.account(tokenAccountAlt1), 10),
                List.of(
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(payerAccount), SolanaEncoding.decodeBase58(payerAccountPriv)),
                        new Solana4jJsonRpcTestHelper.Signer(Solana.account(tokenMintAltAuthority), SolanaEncoding.decodeBase58(tokenMintAltAuthorityPriv))
                )
        );

        final SolanaClientResponse<String> response = api.sendTransaction(mintToTransactionBlob);

        assertThat(response.isSuccess()).isTrue();
    }
}
