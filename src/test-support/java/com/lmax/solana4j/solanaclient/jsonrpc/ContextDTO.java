package com.lmax.solana4j.solanaclient.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.solanaclient.api.Context;

final class ContextDTO implements Context
{
    private final long slot;

    @JsonCreator
    ContextDTO(final @JsonProperty("slot") long slot)
    {
        this.slot = slot;
    }

    @Override
    public long getSlot()
    {
        return slot;
    }

    @Override
    public String toString()
    {
        return "Context{" +
               "slot=" + slot +
               '}';
    }
}
