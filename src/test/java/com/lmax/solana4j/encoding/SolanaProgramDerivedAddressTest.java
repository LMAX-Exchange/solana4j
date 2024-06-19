package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.programs.AddressLookupTableProgram;
import com.lmax.solana4j.programs.AssociatedTokenMetadataProgram;
import org.assertj.core.api.AssertionsForClassTypes;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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

    @Test
    void shouldCalculateAProgramDerivedAddressForAddressLookupTable()
    {
        final PublicKey authority = Solana.account(Base58.decode("EYB1g5R8beNtVqDpKpmkKWtLdhBY8Wh7q3QT3U3fbw7y"));

        final Slot recentSlot = Solana.slot(265008810);

        final ProgramDerivedAddress programDerivedAddress = SolanaProgramDerivedAddress.deriveProgramAddress(
                List.of(authority.bytes(), recentSlot.bytes()),
                AddressLookupTableProgram.PROGRAM_ACCOUNT
        );

        assertThat(programDerivedAddress.address().base58()).isEqualTo("DmTtM8rQMcqR56ksBkayLd9KuYiFaGYZEAPnh5iUtgVV");
        assertThat(programDerivedAddress.nonce()).isEqualTo(255);
    }

    @Test
    void derivesCorrectTokenMetadataProgramAddress()
    {
        final PublicKey mint = Solana.account(Base58.decode("HDLRMKW1FDz2q5Zg778CZx26UgrtnqpUDkNNJHhmVUFr"));
        final byte[] metadataAddress = AssociatedTokenMetadataProgram.deriveAssociatedTokenMetadata(mint);
        assertArrayEquals(Base58.decode("Ff68e9DL9p1GUBkhRXxdv61wiYd8X6iFWTS6XWgsDptP"), metadataAddress);
    }
}
