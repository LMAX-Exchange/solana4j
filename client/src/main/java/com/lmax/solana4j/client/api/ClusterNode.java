package com.lmax.solana4j.client.api;

/**
 * Represents an object containing the metadata of a node within a Solana cluster.
 */
public interface ClusterNode
{
    /**
     * Returns the current feature set as a number.
     * Documentation for individual features is found <a href="https://docs.rs/solana-feature-set/latest/solana_feature_set/">here</a>
     *
     * @return the current feature set as a number.
     */
    long getFeatureSet();

    /**
     * Returns the Gossip Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Gossip Address for the node.
     */
    String getGossipAddress();

    /**
     * Returns the public key of the Solana node.
     *
     * @return the base-58 encoded public key of the Solana node.
     */
    String getPublicKey();

    /**
     * Returns the JSON-RPC for the node. Formatted as <code>ip:port</code>.
     * Will be <code>null</code> if the node does not have public RPC enabled.
     *
     * @return the JSON-RPC for the node.
     */
    String getRpcAddress();

    /**
     * Returns the version of the shred database on the Solana node containing the blocks available on that node.
     *
     * @return the version of the shred database on the Solana node.
     */
    long getShredVersion();

    /**
     * Returns the Transaction Processing Unit Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Transaction Processing Unit Address for the node.
     */
    String getTpuAddress();

    /**
     * Returns the version of solana core as a string.
     * This value is formatted as a semantic version number.
     *
     * @return the version of solana core as a string.
     */
    String getVersion();

    /**
     * Returns the PubSub Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the PubSub Address for the node.
     */
    String getPubsubAddress();

    /**
     * Returns the Shred Repair Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Shred Repair Address for the node.
     */
    String getServeRepairAddress();

    /**
     * Returns the Transaction Processing Unit Forwarding Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Transaction Processing Unit Forwarding Address for the node.
     */
    String getTpuForwardsAddress();

    /**
     * Returns the Transaction Processing Unit Forwarding QUIC Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Transaction Processing Unit Forwarding QUIC Address for the node.
     */
    String getTpuForwardsQuicAddress();

    /**
     * Returns the Transaction Processing Unit QUIC Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Transaction Processing Unit QUIC Address for the node.
     */
    String getTpuQuicAddress();

    /**
     * Returns the Transaction Processing Unit Vote Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Transaction Processing Unit Vote Address for the node.
     */
    String getTpuVoteAddress();

    /**
     * Returns the Transaction Validation Unit Vote Address for the node. Formatted as <code>ip:port</code>.
     *
     * @return the Transaction Validation Unit Vote Address for the node.
     */
    String getTvuAddress();

    /**
     * Returns the client ID of the node.
     *
     * @return the client ID, or {@code null} if not available
     */
    String getClientId();
}
