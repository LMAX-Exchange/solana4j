package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.Blockhash;
import com.lmax.solana4j.solanaclient.api.SolanaRpcResponse;
import com.lmax.solana4j.util.Base58;;

final class BlockhashDTO implements SolanaRpcResponse<Blockhash>
{
    private final ContextDTO context;
    private final BlockhashValueDTO value;

    @JsonCreator
    BlockhashDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") BlockhashValueDTO value)
    {
        this.context = context;
        this.value = value;
    }

    @Override
    public ContextDTO getContext()
    {
        return context;
    }

    @Override
    public BlockhashValueDTO getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "BlockhashDTO{" +
               "context=" + context +
               ", value=" + value +
               '}';
    }

    public static final class BlockhashValueDTO implements Blockhash
    {
        private final String blockhashBase58;
        private final int lastValidBlockHeight;

        @JsonCreator
        BlockhashValueDTO(
                final @JsonProperty("blockhash") String blockhash,
                final @JsonProperty("lastValidBlockHeight") int lastValidBlockHeight)
        {
            this.blockhashBase58 = blockhash;
            this.lastValidBlockHeight = lastValidBlockHeight;
        }

        @Override
        @JsonProperty("blockhash")
        public String getBlockhashBase58()
        {
            return blockhashBase58;
        }

        @Override
        @JsonIgnore
        public byte[] getBytes()
        {
            return Base58.decode(blockhashBase58);
        }

        @Override
        public int getLastValidBlockHeight()
        {
            return lastValidBlockHeight;
        }

        @Override
        public String toString()
        {
            return "BlockhashValueDTO{" +
                   "blockhashBase58='" + blockhashBase58 + '\'' +
                   ", lastValidBlockHeight=" + lastValidBlockHeight +
                   '}';
        }
    }
}
