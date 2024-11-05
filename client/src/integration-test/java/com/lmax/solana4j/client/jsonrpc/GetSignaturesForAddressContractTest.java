package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class GetSignaturesForAddressContractTest extends SolanaClientIntegrationTestBase
{
    private String transactionSignature1;
    private String transactionSignature2;
    private String transactionSignature3;

    @BeforeEach
    void beforeAll() throws SolanaJsonRpcClientException
    {
        transactionSignature1 = api.requestAirdrop(payerAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        transactionSignature2 = api.requestAirdrop(payerAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        transactionSignature3 = api.requestAirdrop(payerAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);
    }

    @Test
    void shouldGetSignaturesForAddressDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var signaturesForAddress = api.getSignaturesForAddress(payerAccount).getResponse();

        assertThat(signaturesForAddress).hasSize(3);

        // ordered by most recent first
        final var signature3 = signaturesForAddress.get(0);
        assertThat(signature3.getSignature()).isEqualTo(transactionSignature3);
        assertThat(signature3.getConfirmationStatus()).isEqualTo(Commitment.FINALIZED);
        assertThat(signature3.getMemo()).isNull();
        assertThat(signature3.getErr()).isNull();
        assertThat(signature3.getSlot()).isGreaterThan(0L);
        assertThat(signature3.getBlockTime()).isGreaterThan(0L);

        final var signature2 = signaturesForAddress.get(1);
        assertThat(signature2.getSignature()).isEqualTo(transactionSignature2);
        assertThat(signature2.getConfirmationStatus()).isEqualTo(Commitment.FINALIZED);
        assertThat(signature2.getMemo()).isNull();
        assertThat(signature2.getErr()).isNull();
        assertThat(signature2.getSlot()).isGreaterThan(0L);
        assertThat(signature2.getBlockTime()).isGreaterThan(0L);

        final var signature1 = signaturesForAddress.get(2);
        assertThat(signature1.getSignature()).isEqualTo(transactionSignature1);
        assertThat(signature1.getConfirmationStatus()).isEqualTo(Commitment.FINALIZED);
        assertThat(signature1.getMemo()).isNull();
        assertThat(signature1.getErr()).isNull();
        assertThat(signature1.getSlot()).isGreaterThan(0L);
        assertThat(signature1.getBlockTime()).isGreaterThan(0L);
    }

    @Test
    void shouldGetSignaturesForAddressWithLimitOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("limit", 2);

        final var signaturesForAddress = api.getSignaturesForAddress(payerAccount, optionalParams).getResponse();

        assertThat(signaturesForAddress).hasSize(2);
    }

    @Test
    void shouldGetSignaturesForAddressWithBeforeOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("before", transactionSignature2);

        final var signaturesForAddress = api.getSignaturesForAddress(payerAccount, optionalParams).getResponse();

        // does not include the before point
        assertThat(signaturesForAddress).hasSize(1);
        assertThat(signaturesForAddress.get(0).getSignature()).isEqualTo(transactionSignature1);
    }

    @Test
    void shouldGetSignaturesForAddressWithUntilOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("until", transactionSignature1);

        final var signaturesForAddress = api.getSignaturesForAddress(payerAccount, optionalParams).getResponse();

        // does not include the until point
        assertThat(signaturesForAddress).hasSize(2);
        assertThat(signaturesForAddress.get(0).getSignature()).isEqualTo(transactionSignature3);
        assertThat(signaturesForAddress.get(1).getSignature()).isEqualTo(transactionSignature2);
    }

    @Test
    void shouldReturnErrorForMinContextSlotNotReached() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("minContextSlot", 10000000000L);

        final var response = api.getSignaturesForAddress(payerAccount, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
