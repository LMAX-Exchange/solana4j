package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import org.junit.jupiter.api.Test;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class AssociatedTokenProgramTest
{
    @Test
    void throwsOnNullOwnerKey()
    {
        assertThatThrownBy(() ->
                AssociatedTokenProgram.deriveAddress(null, Solana.account(ACCOUNT1), Solana.account(ACCOUNT2)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnNullMintKey()
    {
        assertThatThrownBy(() ->
                AssociatedTokenProgram.deriveAddress(Solana.account(ACCOUNT1), null, Solana.account(ACCOUNT2)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnNullTokenProgramKey()
    {
        assertThatThrownBy(() ->
                AssociatedTokenProgram.deriveAddress(Solana.account(ACCOUNT1), null, Solana.account(ACCOUNT2)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldCreateAssociatedTokenAddress()
    {
        final PublicKey owner = Solana.account(ACCOUNT1);
        final PublicKey mint = Solana.account(ACCOUNT2);

        final ProgramDerivedAddress associatedTokenAddress = AssociatedTokenProgram.deriveAddress(
                owner,
                TokenProgram.PROGRAM_ACCOUNT,
                mint);

        assertThat(associatedTokenAddress.programId()).isEqualTo(AssociatedTokenProgram.ASSOCIATED_TOKEN_PROGRAM_ACCOUNT);
        assertThat(associatedTokenAddress.address().base58()).isEqualTo("BtHaGVQ4uRjgxGSoADNkPLxQUVBFhv3GaLbrU5jQnbmh");
        assertThat(associatedTokenAddress.nonce()).isEqualTo(255);
    }

    @Test
    void shouldCreateAssociatedToken2022Address()
    {
        final PublicKey owner = Solana.account(ACCOUNT1);
        final PublicKey mint = Solana.account(ACCOUNT2);

        final ProgramDerivedAddress associatedTokenAddress = AssociatedTokenProgram.deriveAddress(
                owner,
                Token2022Program.PROGRAM_ACCOUNT,
                mint);

        assertThat(associatedTokenAddress.programId()).isEqualTo(AssociatedTokenProgram.ASSOCIATED_TOKEN_PROGRAM_ACCOUNT);
        assertThat(associatedTokenAddress.address().base58()).isEqualTo("6TZpgbVr5gCryZfwJEEgTjp88DU4LWDkVA68ACUdC9gK");
        assertThat(associatedTokenAddress.nonce()).isEqualTo(253);
    }
}
