package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;
import java.util.Objects;

import static com.lmax.solana4j.programs.AssociatedTokenProgram.ASSOCIATED_TOKEN_PROGRAM_ACCOUNT;
import static java.util.Objects.requireNonNull;

final class SolanaAssociatedTokenAddress extends SolanaProgramDerivedAddress implements AssociatedTokenAddress
{
    private final PublicKey mint;
    private final PublicKey owner;

    public static SolanaAssociatedTokenAddress deriveAssociatedTokenAddress(final PublicKey owner, final PublicKey mint, final PublicKey tokenProgramAccount)
    {
        final ProgramDerivedAddress programDerivedAddress = SolanaProgramDerivedAddress.deriveProgramAddress(
                List.of(owner.bytes(), tokenProgramAccount.bytes(), mint.bytes()),
                ASSOCIATED_TOKEN_PROGRAM_ACCOUNT
        );
        return new SolanaAssociatedTokenAddress(
                new SolanaAccount(programDerivedAddress.address().bytes()),
                owner,
                mint,
                tokenProgramAccount,
                programDerivedAddress.nonce()
        );
    }


    SolanaAssociatedTokenAddress(final PublicKey address, final PublicKey owner, final PublicKey mint, final PublicKey programAccount, final int nonce)
    {
        super(address, programAccount, nonce);
        this.mint = requireNonNull(mint, "The mint public key must be specified, but was null");
        this.owner = requireNonNull(owner, "The owner public key must be specified, but was null");
    }

    @Override
    public PublicKey mint()
    {
        return mint;
    }

    @Override
    public PublicKey owner()
    {
        return owner;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }
        final SolanaAssociatedTokenAddress that = (SolanaAssociatedTokenAddress) o;
        return Objects.equals(mint, that.mint) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), mint, owner);
    }

    @Override
    public String toString()
    {
        return "SolanaAssociatedTokenAddress{" +
                "mint=" + mint +
                ", address=" + address +
                ", owner=" + owner +
                ", programId=" + programAccount +
                ", nonce=" + nonce +
                '}';
    }
}
