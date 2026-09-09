package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.ClusterNode;

/**
 * Data Transfer Object for the output from <code>getClusterNodes</code> RPC call.
 */
public class ClusterNodeDTO implements ClusterNode
{
    private final long featureSet;
    private final String gossipAddress;
    private final String publicKey;
    private final String rpcAddress;
    private final long shredVersion;
    private final String tpuAddress;
    private final String version;
    private final String pubsubAddress;
    private final String serveRepairAddress;
    private final String tpuForwardsAddress;
    private final String tpuForwardsQuicAddress;
    private final String tpuQuicAddress;
    private final String tpuVoteAddress;
    private final String tvuAddress;
    private final String clientId;

    @JsonCreator
    ClusterNodeDTO(
            final @JsonProperty("featureSet") long featureSet,
            final @JsonProperty("gossip") String gossipAddress,
            final @JsonProperty("pubkey") String publicKey,
            final @JsonProperty("rpc") String rpcAddress,
            final @JsonProperty("shredVersion") long shredVersion,
            final @JsonProperty("tpu") String tpuAddress,
            final @JsonProperty("version") String version,
            final @JsonProperty("pubsub") String pubsubAddress,
            final @JsonProperty("serveRepair") String serveRepairAddress,
            final @JsonProperty("tpuForwards") String tpuForwardsAddress,
            final @JsonProperty("tpuForwardsQuic") String tpuForwardsQuicAddress,
            final @JsonProperty("tpuQuic") String tpuQuicAddress,
            final @JsonProperty("tpuVote") String tpuVoteAddress,
            final @JsonProperty("tvu") String tvuAddress,
            final @JsonProperty("clientId") String clientId)
    {
        this.featureSet = featureSet;
        this.gossipAddress = gossipAddress;
        this.publicKey = publicKey;
        this.rpcAddress = rpcAddress;
        this.shredVersion = shredVersion;
        this.tpuAddress = tpuAddress;
        this.version = version;
        this.pubsubAddress = pubsubAddress;
        this.serveRepairAddress = serveRepairAddress;
        this.tpuForwardsAddress = tpuForwardsAddress;
        this.tpuForwardsQuicAddress = tpuForwardsQuicAddress;
        this.tpuQuicAddress = tpuQuicAddress;
        this.tpuVoteAddress = tpuVoteAddress;
        this.tvuAddress = tvuAddress;
        this.clientId = clientId;
    }

    @Override
    @JsonProperty("featureSet")
    public long getFeatureSet()
    {
        return featureSet;
    }

    @Override
    @JsonProperty("gossip")
    public String getGossipAddress()
    {
        return gossipAddress;
    }

    @Override
    @JsonProperty("pubkey")
    public String getPublicKey()
    {
        return publicKey;
    }

    @Override
    @JsonProperty("rpc")
    public String getRpcAddress()
    {
        return rpcAddress;
    }

    @Override
    @JsonProperty("shredVersion")
    public long getShredVersion()
    {
        return shredVersion;
    }

    @Override
    @JsonProperty("tpu")
    public String getTpuAddress()
    {
        return tpuAddress;
    }

    @Override
    @JsonProperty("version")
    public String getVersion()
    {
        return version;
    }

    @Override
    @JsonProperty("pubsub")
    public String getPubsubAddress()
    {
        return pubsubAddress;
    }

    @Override
    @JsonProperty("serveRepair")
    public String getServeRepairAddress()
    {
        return serveRepairAddress;
    }

    @Override
    @JsonProperty("tpuForwards")
    public String getTpuForwardsAddress()
    {
        return tpuForwardsAddress;
    }

    @Override
    @JsonProperty("tpuForwardsQuic")
    public String getTpuForwardsQuicAddress()
    {
        return tpuForwardsQuicAddress;
    }

    @Override
    @JsonProperty("tpuQuic")
    public String getTpuQuicAddress()
    {
        return tpuQuicAddress;
    }

    @Override
    @JsonProperty("tpuVote")
    public String getTpuVoteAddress()
    {
        return tpuVoteAddress;
    }

    @Override
    @JsonProperty("tvu")
    public String getTvuAddress()
    {
        return tvuAddress;
    }

    @Override
    public String getClientId()
    {
        return clientId;
    }

    @Override
    public String toString()
    {
        return "ClusterNodeDTO{" +
               "featureSet=" + featureSet +
               ", gossipAddress='" + gossipAddress + '\'' +
               ", publicKey='" + publicKey + '\'' +
               ", rpcAddress='" + rpcAddress + '\'' +
               ", shredVersion=" + shredVersion +
               ", tpuAddress='" + tpuAddress + '\'' +
               ", version='" + version + '\'' +
               ", pubsubAddress='" + pubsubAddress + '\'' +
               ", serveRepairAddress='" + serveRepairAddress + '\'' +
               ", tpuForwardsAddress='" + tpuForwardsAddress + '\'' +
               ", tpuForwardsQuicAddress='" + tpuForwardsQuicAddress + '\'' +
               ", tpuQuicAddress='" + tpuQuicAddress + '\'' +
               ", tpuVoteAddress='" + tpuVoteAddress + '\'' +
               ", tvuAddress='" + tvuAddress + '\'' +
               ", clientId='" + clientId + '\'' +
               '}';
    }
}
