package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.encoding.TokenMetadata;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AssociatedTokenMetadataProgramTest
{
    @Test
    void derivesCorrectTokenMetadataProgramAddress()
    {
        final PublicKey mint = Solana.account(Base58.decode("HDLRMKW1FDz2q5Zg778CZx26UgrtnqpUDkNNJHhmVUFr"));
        final byte[] metadataAddress = AssociatedTokenMetadataProgram.deriveAssociatedTokenMetadata(mint);
        assertArrayEquals(Base58.decode("Ff68e9DL9p1GUBkhRXxdv61wiYd8X6iFWTS6XWgsDptP"), metadataAddress);
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

        final TokenMetadata metadata = AssociatedTokenMetadataProgram.extractTokenMetadata(base64Metadata);
        assertThat(new TokenMetadata("MILLIONSY").toString()).isEqualTo(metadata.toString());
    }
}
