package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.ProgramDerivedAddress;
import org.assertj.core.api.AssertionsForClassTypes;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SolanaProgramDerivedAddressTest
{

    @Test
    void shouldCalculateAProgramDerivedAddress()
    {
        //Some precomputed values from web3.js
        final SolanaAccount owner = new SolanaAccount(Base58.decode("DgmAEaJx5mDvwKUuexiN2urBkuzEMzMrgBMj2SoadyyK"));
        final SolanaAccount program = new SolanaAccount(Base58.decode("MuMQqQg7tcaerMu7RKMuAvXjLLF4yeQ6swdqUu4eDNN"));

        final ProgramDerivedAddress programDerivedAddress = SolanaProgramDerivedAddress.deriveProgramAddress(owner, program);

        assertThat(programDerivedAddress.address().base58()).isEqualTo("5BQ9r1Q7HLCPQS6QnTaqcXFhwS8hb7uwhxLknJuNLh8E");
        assertThat(programDerivedAddress.nonce()).isEqualTo(255);
    }

    @Test
    void shouldCalculateAProgramDerivedAddressFromMultipleSeeds()
    {
        final SolanaAccount program = new SolanaAccount(Base58.decode("FTCuVnzaBZQXGz7D5mweRnWgY4fbS8rg42SD6envtoUD"));

        final SolanaAccount seed = new SolanaAccount(Base58.decode("Wd4UqPtgrnYAH6pxMrzr6aNv4CmTFgwDfPQi9BYjPt7"));
        final ProgramDerivedAddress programDerivedAddress = SolanaProgramDerivedAddress.deriveProgramAddress(
                List.of("aString".getBytes(StandardCharsets.UTF_8), "anotherString".getBytes(StandardCharsets.UTF_8), seed.bytes),
                program);

        AssertionsForClassTypes.assertThat(programDerivedAddress.address().base58()).isEqualTo("FiZFCNEX1WJbP1UEyr2o4uyhtEFoZc3cMyYScf6LAYDx");
        AssertionsForClassTypes.assertThat(programDerivedAddress.nonce()).isEqualTo(254);
    }
}
