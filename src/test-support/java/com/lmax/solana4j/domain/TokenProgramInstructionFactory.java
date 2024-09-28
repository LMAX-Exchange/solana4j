package com.lmax.solana4j.domain;

import com.lmax.solana4j.api.TransactionBuilder;
import com.lmax.solana4j.programs.token.TokenProgramBase;

public interface TokenProgramInstructionFactory
{
    TokenProgramBase.TokenProgramBaseFactory<?> factory(TransactionBuilder tb);
}