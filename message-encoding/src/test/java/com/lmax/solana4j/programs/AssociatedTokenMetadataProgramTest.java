package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.encoding.SolanaEncoding;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AssociatedTokenMetadataProgramTest
{
    @Test
    void derivesCorrectProgramDerivedAddressForTokenMetadataProgram()
    {
        final PublicKey mint = Solana.account(SolanaEncoding.decodeBase58("HDLRMKW1FDz2q5Zg778CZx26UgrtnqpUDkNNJHhmVUFr"));
        final ProgramDerivedAddress metadataAddress = AssociatedTokenMetadataProgram.deriveAddress(mint);

        assertArrayEquals(SolanaEncoding.decodeBase58("Ff68e9DL9p1GUBkhRXxdv61wiYd8X6iFWTS6XWgsDptP"), metadataAddress.address().bytes());
        assertThat(metadataAddress.nonce()).isEqualTo(253);
        assertThat(metadataAddress.programId().base58()).isEqualTo("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");
    }

    @Test
    void extractsCorrectTokenMetadata()
    {
        final String base64Metadata =
                "BCiUQD3vjdOcERjci+tEza693iY9vZaYShTZe6wqiAB78OPrKcLB3KUmyL/8gHzzJ59be" +
                "O+RuLlO2jE+Lxq8y8kgAAAATUlMTElPTlNZAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKAAAATUlMTEkAAAAAAMgAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAf4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";

        final String tokenName = AssociatedTokenMetadataProgram.extractTokenName(base64Metadata);
        assertThat("MILLIONSY").isEqualTo(tokenName);
    }
}
