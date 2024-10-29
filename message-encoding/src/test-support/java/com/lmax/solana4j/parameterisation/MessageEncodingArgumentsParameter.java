package com.lmax.solana4j.parameterisation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class MessageEncodingArgumentsParameter implements ArgumentsProvider
{
    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context)
    {
        return Stream.of(
                Arguments.of("V0"),
                Arguments.of("Legacy")
        );
    }
}
