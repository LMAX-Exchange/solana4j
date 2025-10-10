package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.client.api.ClusterNode;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

// https://solana.com/docs/rpc/http/getclusternodes
public class GetClusterNodesContractTest extends SolanaClientIntegrationTestBase
{
    @Test
    void shouldGetListOfNodes() throws SolanaJsonRpcClientException
    {
        List<ClusterNode> nodes = SOLANA_API.getClusterNodes().getResponse();
        assertEquals(1, nodes.size(), "Actual nodes: " + nodes.stream()
                .map(node -> String.format("Node: %s, RPC: %s, Gossip: %s", node.getPublicKey(), node.getRpcAddress(), node.getGossipAddress()))
                .collect(Collectors.joining(" | ")));
    }

    @Test
    void shouldDecodeImportantFields() throws SolanaJsonRpcClientException
    {
        final ClusterNode clusterNode = SOLANA_API.getClusterNodes().getResponse().get(0);
        assertEquals("127.0.0.1:8899", clusterNode.getRpcAddress());
        assertEquals("127.0.0.1:8001", clusterNode.getGossipAddress());
        assertThat(clusterNode.getFeatureSet()).isGreaterThan(0L);

        // Unless we write a stub, we can't really hardcode the value here
        // A regular expression to check it's at least a valid semver will have to do
        assertThat(clusterNode.getVersion()).matches("\\d+\\.\\d+\\.\\d+");
    }
}
