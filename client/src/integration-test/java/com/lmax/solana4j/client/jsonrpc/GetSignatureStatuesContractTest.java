package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getsignaturestatuses
class GetSignatureStatuesContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetSignatureStatusesDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "GZ3kNvLp4nW9VLnRTQLMPv5pPgjTruHVWwYZUevDfi3p";
        final var transactionSignature1 = api.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        final var transactionSignature2 = api.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        final var transactionSignature3 = api.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);

        final var response = api.getSignatureStatuses(List.of(transactionSignature1, transactionSignature2, transactionSignature3)).getResponse();

        assertThat(response).hasSize(3);

        final var signatureStatus1 = response.get(0);
        assertThat(signatureStatus1.getErr()).isNull();
        assertThat(signatureStatus1.getSlot()).isGreaterThan(0L);
        assertThat(signatureStatus1.getConfirmationStatus()).isEqualTo(Commitment.FINALIZED);
        assertThat(signatureStatus1.getStatus().getKey()).isEqualTo("Ok");
        // number of blocks since signature confirmation, null if rooted, as well as finalized by a supermajority of the cluster
        // since we're finalized this is going to be null
        assertThat(signatureStatus1.getConfirmations()).isNull();

        final var signatureStatus2 = response.get(1);
        assertThat(signatureStatus2.getErr()).isNull();
        assertThat(signatureStatus2.getSlot()).isGreaterThan(0L);
        assertThat(signatureStatus2.getConfirmationStatus()).isEqualTo(Commitment.FINALIZED);
        assertThat(signatureStatus2.getStatus().getKey()).isEqualTo("Ok");
        // number of blocks since signature confirmation, null if rooted, as well as finalized by a supermajority of the cluster
        // since we're finalized this is going to be null
        assertThat(signatureStatus2.getConfirmations()).isNull();

        final var signatureStatus3 = response.get(2);
        assertThat(signatureStatus3.getErr()).isNull();
        assertThat(signatureStatus3.getSlot()).isGreaterThan(0L);
        assertThat(signatureStatus3.getConfirmationStatus()).isEqualTo(Commitment.FINALIZED);
        assertThat(signatureStatus3.getStatus().getKey()).isEqualTo("Ok");
        // number of blocks since signature confirmation, null if rooted, as well as finalized by a supermajority of the cluster
        // since we're finalized this is going to be null
        assertThat(signatureStatus3.getConfirmations()).isNull();
    }

    @Test
    void shouldGetSignatureStatusesWithSearchTransactionHistoryOptionalParam() throws SolanaJsonRpcClientException
    {
        // difficult to actually test the effect of this
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("searchTransactionHistory", true);

        final var randomAccount = "H3mn8y54jLo8dPfNV6eRxBphgWBzMdQJYme3NxYXm3uB";
        final var transactionSignature1 = api.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        final var transactionSignature2 = api.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        final var transactionSignature3 = api.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);

        final var response = api.getSignatureStatuses(List.of(transactionSignature1, transactionSignature2, transactionSignature3), optionalParams).getResponse();

        assertThat(response).hasSize(3);
    }

    @Test
    void shouldReturnErrorIfTransactionFailed() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "8LZ4rT2Dq3vnqN6W9wqfhEDzJhKjsiD2AX1cZ1vLkXeZ";
        // this should create an error - there is an airdrop limit
        final var transactionSignature1 = api.requestAirdrop(randomAccount, Sol.lamports(new BigDecimal("100000000000000"))).getResponse();
        waitForTransactionSuccess(transactionSignature1);

        final var response = api.getSignatureStatuses(List.of(transactionSignature1)).getResponse();

        assertThat(response).hasSize(1);
        // some random error
        assertThat(response.get(0).getErr()).isEqualTo(Map.of("InstructionError", List.of(0, Map.of("Custom", 1))));
    }

    @Test
    void shouldReturnNullResponseForUnknownTransactions() throws SolanaJsonRpcClientException
    {
        final var response = api.getSignatureStatuses(List.of("5F3u76cRyDHyWcHkFdRq1p8JLpJK8G8Z1uFbMhsyhRThNxWe4VjhYdLEyaM1wWqGqVt2aZyKPMPj9CMKo4nLhAhN"));

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getResponse().get(0)).isNull();
    }
}
