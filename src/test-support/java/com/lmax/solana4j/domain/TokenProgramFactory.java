package com.lmax.solana4j.domain;

import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.programs.TokenProgram;

public interface TokenProgramFactory
{
    TokenProgram<?> factory(TransactionBuilder tb);
}
