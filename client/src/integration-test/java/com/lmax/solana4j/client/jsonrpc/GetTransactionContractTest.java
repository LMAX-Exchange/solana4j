package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.assertion.Condition;
import com.lmax.solana4j.assertion.Waiter;
import com.lmax.solana4j.client.api.AccountKeys;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

// https://solana.com/docs/rpc/http/gettransaction
class GetTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = api.requestAirdrop(dummyAccount, Sol.lamports(BigDecimal.ONE)).getResponse();

        final var response = waitForTransactionSuccess(transactionSignature);

        assertThat(response.getSlot()).isGreaterThan(0);
        assertThat(response.getBlockTime()).isGreaterThan(0);
        assertThat(response.getVersion()).isEqualTo("legacy");

        final var encodedTransactionData = response.getTransactionData().getEncodedTransactionData();
        assertThat(encodedTransactionData.get(0)).isNotEmpty();
        assertThat(encodedTransactionData.get(1)).isEqualTo("base64");

        final var metadata = response.getMetadata();
        assertThat(metadata.getErr()).isNull();
        assertThat(metadata.getFee()).isGreaterThan(0);
        assertThat(metadata.getInnerInstructions()).isEmpty();
        assertThat(metadata.getLogMessages()).hasSize(2);
        assertThat(metadata.getPreBalances()).hasSize(3);
        assertThat(metadata.getPostBalances()).hasSize(3);
        assertThat(metadata.getPreTokenBalances()).isEmpty();
        assertThat(metadata.getPostTokenBalances()).isEmpty();
        assertThat(metadata.getRewards()).isEmpty();
        assertThat(metadata.getComputeUnitsConsumed()).isGreaterThan(0);
        assertThat(metadata.getLoadedAddresses().getReadonly()).isEmpty();
        assertThat(metadata.getLoadedAddresses().getWritable()).isEmpty();
        assertThat(metadata.getStatus().getKey()).isEqualTo("Ok");
    }

    @Test
    @Disabled
    void shouldGetTokenTransactionDefaultOptionalParams()
    {
        // need to contstruct a transaction between token accounts
        // might need to reconstruct token accounts in /accounts for this ...
        fail();
    }

    @Test
    void shouldGetTransactionBase58EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = api.requestAirdrop(dummyAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final var response = waitForTransactionSuccess(transactionSignature, Optional.of(optionalParams));

        final var encodedTransactionData = response.getTransactionData().getEncodedTransactionData();
        assertThat(encodedTransactionData.get(0)).isNotEmpty();
        assertThat(encodedTransactionData.get(1)).isEqualTo("base58");
    }

    @Test
    void shouldGetTransactionJsonEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = api.requestAirdrop(dummyAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "json");

        final var response = waitForTransactionSuccess(transactionSignature, Optional.of(optionalParams));

        final var parsedTransactionData = response.getTransactionData().getParsedTransactionData();

        final var message = parsedTransactionData.getMessage();
        assertThat(message.getRecentBlockhash()).isNotNull();
        assertThat(message.getAccountKeys().getEncodedAccountKeys()).hasSize(3);

        final var messageHeader = message.getHeader();
        assertThat(messageHeader.getNumReadonlySignedAccounts()).isEqualTo(0);
        assertThat(messageHeader.getNumReadonlyUnsignedAccounts()).isEqualTo(1);
        assertThat(messageHeader.getNumReadonlySignedAccounts()).isEqualTo(0);

        assertThat(message.getInstructions().size()).isEqualTo(1);

        final var instruction = message.getInstructions().get(0);
        assertThat(instruction.getData()).isEqualTo("3Bxs3zzLZLuLQEYX");
        assertThat(instruction.getAccounts()).hasSize(2);
        assertThat(instruction.getAccounts().get(0)).isEqualTo(0);
        assertThat(instruction.getAccounts().get(1)).isEqualTo(1);
        assertThat(instruction.getProgramIdIndex()).isEqualTo(2);
        assertThat(instruction.getStackHeight()).isEqualTo(null);

        assertThat(message.getRecentBlockhash()).isNotEmpty();

        final var signatures = parsedTransactionData.getSignatures();
        assertThat(signatures).hasSize(1);
    }

    @Test
    void shouldGetTransactionJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = api.requestAirdrop(dummyAccount, Sol.lamports(BigDecimal.ONE)).getResponse();
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final var response = waitForTransactionSuccess(transactionSignature, Optional.of(optionalParams));

        final var parsedTransactionData = response.getTransactionData().getParsedTransactionData();

        final var message = parsedTransactionData.getMessage();
        assertThat(message.getRecentBlockhash()).isNotNull();

        final var parsedAccountKeys = message.getAccountKeys().getParsedAccountKeys();
        assertThat(parsedAccountKeys).hasSize(3);

        assertThat(parsedAccountKeys.get(0).isSigner()).isTrue();
        assertThat(parsedAccountKeys.get(0).isWritable()).isTrue();
        assertThat(parsedAccountKeys.get(0).getSource()).isEqualTo(AccountKeys.AccountKeyParsed.KeySource.TRANSACTION);
        assertThat(parsedAccountKeys.get(0).getKey()).isNotEmpty();

        assertThat(parsedAccountKeys.get(1).isSigner()).isFalse();
        assertThat(parsedAccountKeys.get(1).isWritable()).isTrue();
        assertThat(parsedAccountKeys.get(1).getSource()).isEqualTo(AccountKeys.AccountKeyParsed.KeySource.TRANSACTION);
        assertThat(parsedAccountKeys.get(1).getKey()).isNotEmpty();

        assertThat(parsedAccountKeys.get(2).isSigner()).isFalse();
        assertThat(parsedAccountKeys.get(2).isWritable()).isFalse();
        assertThat(parsedAccountKeys.get(2).getSource()).isEqualTo(AccountKeys.AccountKeyParsed.KeySource.TRANSACTION);
        assertThat(parsedAccountKeys.get(2).getKey()).isEqualTo("11111111111111111111111111111111");

        assertThat(message.getHeader()).isNull();

        assertThat(message.getInstructions()).hasSize(1);

        final var instruction = message.getInstructions().get(0);

        // would have been present if encoding json not jsonParsed
        assertThat(instruction.getData()).isNull();
        assertThat(instruction.getAccounts()).isNull();
        assertThat(instruction.getProgramIdIndex()).isNull();
        assertThat(instruction.getStackHeight()).isEqualTo(null);

        // i think the best we can do here is really just return a Map<String, Object> and let the user do their own parsing
        // since the parsing is very much program specific
        assertThat(instruction.getProgram()).isEqualTo("system");
        assertThat(instruction.getProgramId()).isEqualTo("11111111111111111111111111111111");

        final var parsedInstruction = instruction.getInstructionParsed();
        assertThat(parsedInstruction.get("type")).isEqualTo("transfer");
        assertThat(parsedInstruction.get("info"))
                .usingRecursiveComparison()
                .ignoringFields("source")
                .isEqualTo(Map.of(
                        "destination", "4Nd1mnszWRVFzzsxMgcTzdFoC8Wx5mPQD9KZx3qtDr1M",
                        "lamports", 1000000000,
                        "source", "ignoredAsItChanges"
                )
        );
    }

    @Test
    void shouldReturnNullForUnknownTransactionSignature() throws SolanaJsonRpcClientException
    {
        assertThat(api.getTransaction("3wBQpRDgEKgNhbGJGzxfELHTyFas8mvf4x6bLWC989kBpgEVXPnwWS3tg33WEhVxnqbBTVXEQjmHun2tTbxHzSo").getResponse()).isNull();
    }

    @Test
    void shouldReturnErrorForMalformedTransactionSignature() throws SolanaJsonRpcClientException
    {
        final var transaction = api.getTransaction("iamamalformedtransactionsignature");
        assertThat(transaction.isSuccess()).isFalse();
        assertThat(transaction.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(transaction.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }

    private TransactionResponse waitForTransactionSuccess(final String transactionSignature)
    {
        return waitForTransactionSuccess(transactionSignature, Optional.empty());
    }

    private TransactionResponse waitForTransactionSuccess(final String transactionSignature, final Optional<SolanaClientOptionalParams> maybeOptionalParams)
    {
        return Waiter.waitFor(Condition.isNotNull(() ->
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
        }));
    }
}
