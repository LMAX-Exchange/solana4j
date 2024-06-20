package com.lmax.solana4j.solanaclient.api;

import java.util.List;

public interface InnerInstruction
{
    long getIndex();

    List<Instruction> getInstructions();
}
