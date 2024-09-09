package com.lmax.solana4j.encoding;

import com.lmax.solana4j.Solana;
import org.junit.jupiter.api.Test;

import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT1;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT2;
import static com.lmax.solana4j.Solana4jTestHelper.ACCOUNT3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SolanaAssociatedTokenAddressTest
{
    @Test
    void throwsOnNullOwnerKey()
    {
        assertThatThrownBy(() ->
                SolanaAssociatedTokenAddress.deriveAssociatedTokenAddress(null, Solana.account(ACCOUNT1), Solana.account(ACCOUNT2)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnNullMintKey()
    {
        assertThatThrownBy(() ->
                SolanaAssociatedTokenAddress.deriveAssociatedTokenAddress(Solana.account(ACCOUNT1), null, Solana.account(ACCOUNT2)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsOnNullTokenProgramKey()
    {
        assertThatThrownBy(() ->
                SolanaAssociatedTokenAddress.deriveAssociatedTokenAddress(Solana.account(ACCOUNT1), null, Solana.account(ACCOUNT2)))
                .isInstanceOf(NullPointerException.class);
    }

   @Test
   void shouldCreateAssociatedTokenAddress()
   {
       final SolanaAssociatedTokenAddress associatedTokenAddress = SolanaAssociatedTokenAddress.deriveAssociatedTokenAddress(
               Solana.account(ACCOUNT1),
               Solana.account(ACCOUNT2),
               Solana.account(ACCOUNT3));

       assertThat(associatedTokenAddress.owner()).isEqualTo(Solana.account(ACCOUNT1));
       assertThat(associatedTokenAddress.mint()).isEqualTo(Solana.account(ACCOUNT2));
       assertThat(associatedTokenAddress.nonce()).isEqualTo(254);
       assertThat(associatedTokenAddress.programAccount.base58()).isEqualTo("US517G5965aydkZ46HS38QLi7UQiSojurfbQfKCELFx");
       assertThat(associatedTokenAddress.address.base58()).isEqualTo("5rvict4a6xzcf1kwsQTRDY4NhjgWjgfZtquSPwLsRoPM");
   }
}