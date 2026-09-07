package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.BlockResponse;
import com.lmax.solana4j.client.api.SolanaClientOptionalParams;
import com.lmax.solana4j.client.api.SolanaClientResponse;
import com.lmax.solana4j.client.api.TransactionResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// https://solana.com/docs/rpc/http/getblock
final class GetBlockContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetBlockDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse());
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        assertThat(response.getBlockHeight()).isGreaterThan(0);
        assertThat(response.getBlockhash().length()).isBetween(43, 44);
        assertThat(response.getBlockTime()).isGreaterThan(0);
        assertThat(response.getParentSlot()).isGreaterThan(0);
        assertThat(response.getPreviousBlockhash().length()).isBetween(43, 44);

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isNotEmpty();

        final TransactionResponse.TransactionMetadata metadata = transactions.get(0).getMetadata();
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
    void shouldReturnErrorForNonExistentSlot() throws SolanaJsonRpcClientException
    {
        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse() + 1_000_000L);
        assertThat(blockResponse.isSuccess()).isFalse();
    }

    @Test
    void shouldGetBlockBase58EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base58");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isNotEmpty();

        final List<String> encodedTransaction = transactions.get(0).getTransactionData().getEncodedTransactionData();
        assertThat(encodedTransaction.get(0)).isNotEmpty();
        assertThat(encodedTransaction.get(1)).isEqualTo("base58");
    }

    @Test
    void shouldGetBlockBase64EncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "base64");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isNotEmpty();

        final List<String> encodedTransaction = transactions.get(0).getTransactionData().getEncodedTransactionData();
        assertThat(encodedTransaction.get(0)).isNotEmpty();
        assertThat(encodedTransaction.get(1)).isEqualTo("base64");
    }

    @Test
    void shouldGetBlockJsonEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "json");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isNotEmpty();

        final TransactionResponse.TransactionData.TransactionDataParsed parsedTransaction = transactions.get(0).getTransactionData().getParsedTransactionData();
        assertThat(parsedTransaction.getSignatures()).isNotEmpty();
        assertThat(parsedTransaction.getMessage().getAccountKeys().getEncodedAccountKeys()).isNotEmpty();
    }

    @Test
    void shouldGetBlockJsonParsedEncodingOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("encoding", "jsonParsed");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isNotEmpty();

        final TransactionResponse.TransactionData.TransactionDataParsed parsedTransaction = transactions.get(0).getTransactionData().getParsedTransactionData();
        assertThat(parsedTransaction.getSignatures()).isNotEmpty();
        assertThat(parsedTransaction.getMessage().getAccountKeys().getParsedAccountKeys()).isNotEmpty();
    }

    @Test
    void shouldGetBlockFullTransactionDetailsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("transactionDetails", "full");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isNotEmpty();

        // transactionDetails: full implies encoding: jason (unless otherwise specified)
        final TransactionResponse.TransactionData.TransactionDataParsed parsedTransaction = transactions.get(0).getTransactionData().getParsedTransactionData();
        assertThat(parsedTransaction.getSignatures()).isNotEmpty();
        assertThat(parsedTransaction.getMessage().getAccountKeys().getEncodedAccountKeys()).isNotEmpty();
    }

    @Test
    void shouldGetBlockNoneTransactionDetailsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("transactionDetails", "none");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<TransactionResponse> transactions = response.getTransactions();
        assertThat(transactions).isEmpty();
    }

    @Test
    void shouldGetBlockAccountsTransactionDetailsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("transactionDetails", "accounts");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        assertThatCode(() -> SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams))
                .hasMessage("Solana4J does not support the transactionDetails value accounts");
    }

    @Test
    void shouldGetBlockSignaturesTransactionDetailsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("transactionDetails", "signatures");

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        assertThatCode(() -> SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams))
                .hasMessage("Solana4J does not support the transactionDetails value signatures");
    }

    @Test
    void shouldGetBlockRewardsTrueOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("rewards", true);

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<BlockResponse.Rewards> rewards = response.getRewards();
        assertThat(rewards).isNotEmpty();
        assertThat(rewards.get(0).getCommission()).isNull();
        assertThat(rewards.get(0).getLamports()).isGreaterThan(0);
        assertThat(rewards.get(0).getPostBalance()).isGreaterThan(0);
        assertThat(rewards.get(0).getPubkeyBase58().length()).isBetween(43, 44);
        assertThat(rewards.get(0).getRewardType()).isEqualTo("Fee");
    }

    @Test
    void shouldGetBlockRewardsFalseOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("rewards", false);

        final SolanaClientResponse<Long> slotResponse = SOLANA_API.getSlot();
        assertThat(slotResponse.isSuccess()).isTrue();

        SolanaClientResponse<BlockResponse> blockResponse = SOLANA_API.getBlock(slotResponse.getResponse(), optionalParams);
        assertThat(blockResponse.isSuccess()).isTrue();
        final BlockResponse response = blockResponse.getResponse();

        final List<BlockResponse.Rewards> rewards = response.getRewards();
        assertThat(rewards).isEmpty();
    }
}
