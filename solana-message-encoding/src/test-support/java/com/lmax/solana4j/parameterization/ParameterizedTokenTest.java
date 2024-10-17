package com.lmax.solana4j.parameterization;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest(name = "{index}: (Message Encoding {0}, Token Program {1})")
@ArgumentsSource(TokenProgramArgumentsParameter.class)
public @interface ParameterizedTokenTest
{
}
