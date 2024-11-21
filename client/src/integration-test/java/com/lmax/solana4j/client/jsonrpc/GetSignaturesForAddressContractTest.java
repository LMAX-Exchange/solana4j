package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.Commitment;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getsignaturesforaddress
final class GetSignaturesForAddressContractTest extends SolanaClientIntegrationTestBase
{

    @Test
    void shouldGetSignaturesForAddressDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "3NyZjyEsExBWVp4C62MMSTGgS6crChPxvjyzF9e1D8Y7";
        final var transactionSignature1 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        final var transactionSignature2 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        final var transactionSignature3 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);

        final var signaturesForAddress = SOLANA_API.getSignaturesForAddress(randomAccount).getResponse();

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
    void shouldReturnErrorIfTransactionFailed() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "3xjY7TP9gbZx8iLWbFE2fDSfpRcW9k5mSLR5nFu4qUxT";
        // this should create an error - there is an airdrop limit
        final var transactionSignature1 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(new BigDecimal("100000000000000"))).getResponse();
        waitForTransactionSuccess(transactionSignature1);

        final var response = SOLANA_API.getSignaturesForAddress(randomAccount).getResponse();

        assertThat(response).hasSize(1);
        // some random error
        assertThat(response.get(0).getErr()).isEqualTo(Map.of("InstructionError", List.of(0, Map.of("Custom", 1))));
    }

    @Test
    void shouldGetSignaturesForAddressWithLimitOptionalParam() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "3n5wa3Qk1Qd5t8w6ZyV2FPRK1VVzNSLy2LhJ4LuoxXJX";
        final var transactionSignature1 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        final var transactionSignature2 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        final var transactionSignature3 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);

        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("limit", 2);

        final var signaturesForAddress = SOLANA_API.getSignaturesForAddress(randomAccount, optionalParams).getResponse();

        assertThat(signaturesForAddress).hasSize(2);
    }

    @Test
    void shouldGetSignaturesForAddressWithBeforeOptionalParam() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "C5D9ZEFnv3ZoFxSnFqXsMHR3HQykPj4Vb1pZc2PbWuyM";
        final var transactionSignature1 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        final var transactionSignature2 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        final var transactionSignature3 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);

        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("before", transactionSignature2);

        final var signaturesForAddress = SOLANA_API.getSignaturesForAddress(randomAccount, optionalParams).getResponse();

        // does not include the before point
        assertThat(signaturesForAddress).hasSize(1);
        assertThat(signaturesForAddress.get(0).getSignature()).isEqualTo(transactionSignature1);
    }

    @Test
    void shouldGetSignaturesForAddressWithUntilOptionalParam() throws SolanaJsonRpcClientException
    {
        final var randomAccount = "5uUghVUNUFRf6P7QJqMeMAyxXTzT1U7jkkT8Tm7U9ZAT";
        final var transactionSignature1 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature1);
        final var transactionSignature2 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature2);
        final var transactionSignature3 = SOLANA_API.requestAirdrop(randomAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        waitForTransactionSuccess(transactionSignature3);

        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("until", transactionSignature1);

        final var signaturesForAddress = SOLANA_API.getSignaturesForAddress(randomAccount, optionalParams).getResponse();

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

        final var response = SOLANA_API.getSignaturesForAddress(PAYER, optionalParams);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getError().getErrorCode()).isEqualTo(-32016L);
        assertThat(response.getError().getErrorMessage()).isEqualTo("Minimum context slot has not been reached");
    }
}
