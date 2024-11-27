package com.lmax.solana4j.parameterisation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class TokenProgramArgumentsParameter implements ArgumentsProvider
{
    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context)
    {
        return Stream.of(
                Arguments.of("V0", "Token"),
                Arguments.of("V0", "Token2022"),
                Arguments.of("Legacy", "Token"),
                Arguments.of("Legacy", "Token2022")
        );
    }
}
