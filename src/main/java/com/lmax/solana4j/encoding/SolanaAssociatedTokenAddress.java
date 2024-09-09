package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;

import java.util.List;

import static com.lmax.solana4j.programs.AssociatedTokenProgram.ASSOCIATED_TOKEN_PROGRAM_ACCOUNT;
import static java.util.Objects.requireNonNull;

final class SolanaAssociatedTokenAddress extends SolanaProgramDerivedAddress implements AssociatedTokenAddress
{
    private final PublicKey mint;
    private final PublicKey owner;

    static SolanaAssociatedTokenAddress deriveAssociatedTokenAddress(final PublicKey owner, final PublicKey mint, final PublicKey tokenProgramAccount)
    {
        requireNonNull(owner, "The owner public key must be specified, but was null");
        requireNonNull(mint, "The mint public key must be specified, but was null");
        requireNonNull(owner, "The owner public key must be specified, but was null");

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

    private SolanaAssociatedTokenAddress(final PublicKey address, final PublicKey owner, final PublicKey mint, final PublicKey programAccount, final int nonce)
    {
        super(address, programAccount, nonce);
        this.mint = mint;
        this.owner = owner;
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
