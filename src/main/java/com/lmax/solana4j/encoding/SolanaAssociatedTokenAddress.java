package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AssociatedTokenAddress;
import com.lmax.solana4j.api.PublicKey;
import net.i2p.crypto.eddsa.math.Curve;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.lmax.solana4j.programs.AssociatedTokenProgram.ASSOCIATED_TOKEN_PROGRAM_ACCOUNT;
import static java.util.Objects.requireNonNull;
import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

final class SolanaAssociatedTokenAddress extends SolanaProgramDerivedAddress implements AssociatedTokenAddress
{
    private final PublicKey mint;

    public static SolanaAssociatedTokenAddress deriveAssociatedTokenAddress(final PublicKey owner, final PublicKey mint, final PublicKey tokenProgramAccount)
    {
        final Curve curve = ED_25519_CURVE_SPEC.getCurve();
        int bumpSeed = 255;

        byte[] programAddress = new byte[32];
        try
        {
            while (bumpSeed > 0)
            {
                final ByteBuffer seeds = ByteBuffer.allocate(197);
                owner.write(seeds);
                tokenProgramAccount.write(seeds);
                mint.write(seeds);
                seeds.put(getNextSeed(bumpSeed));

                final ByteBuffer programAccountBuf = ByteBuffer.allocate(32);
                ASSOCIATED_TOKEN_PROGRAM_ACCOUNT.write(programAccountBuf);

                programAddress = createProgramAddress(seeds, programAccountBuf);
                curve.createPoint(programAddress, false);
                bumpSeed--;
            }
        }
        catch (final RuntimeException re)
        {
            return new SolanaAssociatedTokenAddress(new SolanaAccount(programAddress), owner, mint, tokenProgramAccount, bumpSeed);
        }

        throw new RuntimeException("Could not find a program address off the curve.");
    }


    SolanaAssociatedTokenAddress(final PublicKey address, final PublicKey owner, final PublicKey mint, final PublicKey programAccount, final int nonce)
    {
        super(address, owner, programAccount, nonce);

        this.mint = requireNonNull(mint, "The mint public key must be specified, but was null");
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
        return Objects.equals(mint, that.mint);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), mint);
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

    @Override
    public PublicKey mint()
    {
        return mint;
    }
}
