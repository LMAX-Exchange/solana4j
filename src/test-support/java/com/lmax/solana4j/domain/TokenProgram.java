package com.lmax.solana4j.domain;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;

public enum TokenProgram
{
    TOKEN_PROGRAM("Token", "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", com.lmax.solana4j.programs.TokenProgram::factory),
    TOKEN_2022_PROGRAM("Token2022", "TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb", com.lmax.solana4j.programs.Token2022Program::factory);

    private final String name;
    private final String address;
    private final TokenProgramFactory tokenProgramFactory;

    TokenProgram(final String name, final String address, final TokenProgramFactory tokenProgramFactory)
    {
        this.name = name;
        this.address = address;
        this.tokenProgramFactory = tokenProgramFactory;
    }

    public static TokenProgram fromName(final String name)
    {
        for (final TokenProgram tokenProgram : values())
        {
            if (tokenProgram.name.equals(name))
            {
                return tokenProgram;
            }
        }
        throw new RuntimeException("Unrecognised token program!!");
    }

    public TokenProgramFactory getFactory()
    {
        return tokenProgramFactory;
    }

    public PublicKey getProgram()
    {
        return Solana.account(Base58.decode(address));
    }
}
