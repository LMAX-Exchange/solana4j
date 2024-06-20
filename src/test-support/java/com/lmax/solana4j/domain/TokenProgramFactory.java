package com.lmax.solana4j.domain;

import com.lmax.solana4j.api.TransactionBuilderBase;
import com.lmax.solana4j.programs.TokenProgram;

public interface TokenProgramFactory
{
    TokenProgram<?> factory(TransactionBuilderBase tb);
}
