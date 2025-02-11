package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.TransactionResponse;
import com.lmax.solana4j.domain.Sol;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/gettransaction
final class GetTransactionContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = SOLANA_API.requestAirdrop(PAYER, Sol.lamports(BigDecimal.ONE)).getResponse();

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
        // inner instructions are program invocations that occur inside the execution of a main (outer)
        // transaction instruction
        assertThat(metadata.getInnerInstructions()).isEmpty();
        assertThat(metadata.getLogMessages()).hasSize(2);
        assertThat(metadata.getPreBalances()).hasSize(3);
        assertThat(metadata.getPostBalances()).hasSize(3);
        assertThat(metadata.getPreTokenBalances()).isEmpty();
        assertThat(metadata.getPostTokenBalances()).isEmpty();
        //  the transaction must be associated with staking-related activities where rewards are distributed
        // e.g. validator vote transactions
        assertThat(metadata.getRewards()).isEmpty();
        assertThat(metadata.getComputeUnitsConsumed()).isGreaterThan(0);
        // accounts that were dynamically loaded during the execution of a transaction
        // e.g. address lookup tables
        assertThat(metadata.getLoadedAddresses().getReadonly()).isEmpty();
        assertThat(metadata.getLoadedAddresses().getWritable()).isEmpty();
        assertThat(metadata.getStatus().getKey()).isEqualTo("Ok");
    }

    @Test
    void shouldGetTokenTransactionDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final var response = SOLANA_API.getTransaction(tokenMintTransactionSignature1).getResponse();

        final var preTokenBalances = response.getMetadata().getPreTokenBalances();
        assertThat(preTokenBalances).hasSize(1);
        final var preTokenBalance = preTokenBalances.get(0);
        assertThat(preTokenBalance.getAccountIndex()).isEqualTo(3);
        assertThat(preTokenBalance.getOwner()).isEqualTo("7H1itW7F72uJbaXK2R4gP7J18HrQ2M683kL9YgUeeUHr");
        assertThat(preTokenBalance.getMint()).isEqualTo("2tokpcExDmewsSNRKuTLVLMUseiSkEdBQWBjeQLmuFaS");
        assertThat(preTokenBalance.getProgramId()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
        final var preUiTokenAmount = preTokenBalance.getUiTokenAmount();
        assertThat(preUiTokenAmount.getDecimals()).isEqualTo(18);

        final var postTokenBalances = response.getMetadata().getPostTokenBalances();
        assertThat(postTokenBalances).hasSize(1);
        final var postTokenBalance = postTokenBalances.get(0);
        assertThat(postTokenBalance.getAccountIndex()).isEqualTo(3);
        assertThat(postTokenBalance.getOwner()).isEqualTo("7H1itW7F72uJbaXK2R4gP7J18HrQ2M683kL9YgUeeUHr");
        assertThat(postTokenBalance.getMint()).isEqualTo("2tokpcExDmewsSNRKuTLVLMUseiSkEdBQWBjeQLmuFaS");
        assertThat(postTokenBalance.getProgramId()).isEqualTo("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");
        final var postUiTokenAmount = postTokenBalance.getUiTokenAmount();
        assertThat(postUiTokenAmount.getDecimals()).isEqualTo(18);
        assertThat(postUiTokenAmount.getUiAmount()).isEqualTo(0.0f);
        assertThat(postUiTokenAmount.getUiAmountString()).isEqualTo("0.00000000000000001");
        assertThat(postUiTokenAmount.getAmount()).isEqualTo("10");
    }

    @Test
    void shouldGetTransactionBase58EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = SOLANA_API.requestAirdrop(PAYER, Sol.lamports(BigDecimal.ONE)).getResponse();
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
        final String transactionSignature = SOLANA_API.requestAirdrop(PAYER, Sol.lamports(BigDecimal.ONE)).getResponse();
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
        assertThat(messageHeader.getNumRequiredSignatures()).isEqualTo(1);

        assertThat(message.getInstructions().size()).isEqualTo(1);

        final var instruction = message.getInstructions().get(0);
        assertThat(instruction.getData()).isEqualTo("3Bxs3zzLZLuLQEYX");
        final var accountIndexes = instruction.getAccounts().getIndexes();
        assertThat(accountIndexes).hasSize(2);
        assertThat(accountIndexes.get(0)).isEqualTo(0);
        assertThat(accountIndexes.get(1)).isEqualTo(1);
        assertThat(instruction.getAccounts().getAddresses()).isNull();
        assertThat(instruction.getProgramIdIndex()).isEqualTo(2);
        // stack height refers to the execution depth of an instruction within a transaction,
        // especially when dealing with nested program invocations (inner instructions)
        assertThat(instruction.getStackHeight()).isEqualTo(null);

        assertThat(message.getRecentBlockhash()).isNotEmpty();

        final var signatures = parsedTransactionData.getSignatures();
        assertThat(signatures).hasSize(1);
    }

    @Test
    void shouldGetTransactionJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final String transactionSignature = SOLANA_API.requestAirdrop(PAYER, Sol.lamports(BigDecimal.ONE)).getResponse();
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
        assertThat(parsedAccountKeys.get(0).getSource()).isEqualTo(TransactionResponse.Message.AccountKeys.AccountKeyParsed.KeySource.TRANSACTION);
        assertThat(parsedAccountKeys.get(0).getKey()).isNotEmpty();

        assertThat(parsedAccountKeys.get(1).isSigner()).isFalse();
        assertThat(parsedAccountKeys.get(1).isWritable()).isTrue();
        assertThat(parsedAccountKeys.get(1).getSource()).isEqualTo(TransactionResponse.Message.AccountKeys.AccountKeyParsed.KeySource.TRANSACTION);
        assertThat(parsedAccountKeys.get(1).getKey()).isNotEmpty();

        assertThat(parsedAccountKeys.get(2).isSigner()).isFalse();
        assertThat(parsedAccountKeys.get(2).isWritable()).isFalse();
        assertThat(parsedAccountKeys.get(2).getSource()).isEqualTo(TransactionResponse.Message.AccountKeys.AccountKeyParsed.KeySource.TRANSACTION);
        assertThat(parsedAccountKeys.get(2).getKey()).isEqualTo("11111111111111111111111111111111");

        assertThat(message.getHeader()).isNull();

        assertThat(message.getInstructions()).hasSize(1);

        final var instruction = message.getInstructions().get(0);

        // would have been present if encoding json not jsonParsed
        assertThat(instruction.getData()).isNull();
        // it's curious that instruction.getAccounts() is NULL when encoding is jsonParsed ...
        assertThat(instruction.getAccounts()).isNull();
        assertThat(instruction.getProgramIdIndex()).isNull();
        // stack height refers to the execution depth of an instruction within a transaction,
        // especially when dealing with nested program invocations (inner instructions)
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
                        "destination", "sCR7NonpU3TrqvusEiA4MAwDMLfiY1gyVPqw2b36d8V",
                        "lamports", 1000000000,
                        "source", "ignoredAsItChanges"
                )
        );
    }

    @Test
    void shouldReturnNullForUnknownTransactionSignature() throws SolanaJsonRpcClientException
    {
        assertThat(SOLANA_API.getTransaction("3wBQpRDgEKgNhbGJGzxfELHTyFas8mvf4x6bLWC989kBpgEVXPnwWS3tg33WEhVxnqbBTVXEQjmHun2tTbxHzSo").getResponse()).isNull();
    }

    @Test
    void shouldReturnErrorForMalformedTransactionSignature() throws SolanaJsonRpcClientException
    {
        final var transaction = SOLANA_API.getTransaction("iamamalformedtransactionsignature");
        assertThat(transaction.isSuccess()).isFalse();
        assertThat(transaction.getError().getErrorCode()).isEqualTo(-32602L);
        assertThat(transaction.getError().getErrorMessage()).isEqualTo("Invalid param: Invalid");
    }
}
