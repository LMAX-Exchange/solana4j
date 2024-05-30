package com.lmax.solana4j.testclient.api;

import java.util.List;

public interface InnerInstruction
{
    long getIndex();

    List<Instruction> getInstructions();
}
