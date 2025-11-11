package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.VoteAccount;
import com.lmax.solana4j.client.api.VoteAccounts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// https://solana.com/docs/rpc/http/getvoteaccounts
public class GetVoteAccountsContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetVoteAccountsDefaultOptionalParams() throws SolanaJsonRpcClientException
    {
        final VoteAccounts voteAccounts = SOLANA_API.getVoteAccounts().getResponse();
        assertThat(voteAccounts).isNotNull();

        final List<VoteAccount> currentVoteAccounts = voteAccounts.getCurrent();
        assertThat(currentVoteAccounts).isNotNull();
        assertThat(currentVoteAccounts).isNotEmpty();

        final VoteAccount firstVoteAccount = currentVoteAccounts.get(0);
        assertThat(firstVoteAccount.getVotePubkey()).isNotNull();
        assertThat(firstVoteAccount.getNodePubkey()).isNotNull();
    }

    @Test
    void shouldGetVoteAccountsVotePubKeyOptionalParam() throws SolanaJsonRpcClientException
    {
        final VoteAccounts voteAccounts = SOLANA_API.getVoteAccounts().getResponse();
        assertThat(voteAccounts).isNotNull();

        final List<VoteAccount> currentVoteAccounts = voteAccounts.getCurrent();
        assertThat(currentVoteAccounts).isNotNull();
        assertThat(currentVoteAccounts).isNotEmpty();

        final VoteAccount firstVoteAccount = currentVoteAccounts.get(0);

        final SolanaJsonRpcClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("votePubkey", firstVoteAccount.getVotePubkey());
        final VoteAccounts filteredVoteAccounts = SOLANA_API.getVoteAccounts(optionalParams).getResponse();

        final List<VoteAccount> currentFilteredVoteAccounts = filteredVoteAccounts.getCurrent();
        assertThat(currentFilteredVoteAccounts).isNotNull();
        assertThat(currentFilteredVoteAccounts).isNotEmpty();
        final VoteAccount firstFilteredVoteAccount = currentFilteredVoteAccounts.get(0);

        assertThat(firstFilteredVoteAccount.getVotePubkey()).isEqualTo(firstVoteAccount.getVotePubkey());
    }

    @Test
    void shouldGetVoteAccountsKeepUnstakedDelinquentsOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaJsonRpcClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("keepUnstakedDelinquents", true);
        final VoteAccounts voteAccounts = SOLANA_API.getVoteAccounts(optionalParams).getResponse();
        assertThat(voteAccounts).isNotNull();

        final List<VoteAccount> currentVoteAccounts = voteAccounts.getCurrent();
        assertThat(currentVoteAccounts).isNotNull();
        assertThat(currentVoteAccounts).isNotEmpty();
    }

    @Test
    void shouldGetVoteAccountsDelinquentSlotDistanceOptionalParam() throws SolanaJsonRpcClientException
    {
        final SolanaJsonRpcClientOptionalParams optionalParams = new SolanaJsonRpcClientOptionalParams();
        optionalParams.addParam("delinquentSlotDistance", 100000L);
        final VoteAccounts voteAccounts = SOLANA_API.getVoteAccounts(optionalParams).getResponse();
        assertThat(voteAccounts).isNotNull();

        final List<VoteAccount> currentVoteAccounts = voteAccounts.getCurrent();
        assertThat(currentVoteAccounts).isNotNull();
        assertThat(currentVoteAccounts).isNotEmpty();
    }
}