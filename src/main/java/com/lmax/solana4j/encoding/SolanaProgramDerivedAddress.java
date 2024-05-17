package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import net.i2p.crypto.eddsa.math.Curve;
import org.bitcoinj.core.Sha256Hash;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

class SolanaProgramDerivedAddress implements ProgramDerivedAddress
{
    final PublicKey address;
    final PublicKey owner;
    final PublicKey programAccount;
    final int nonce;

    // programId is just the Solana term for the public key of the program account
    public static ProgramDerivedAddress deriveProgramAddress(final PublicKey owner, final PublicKey programId)
    {
        final Curve curve = ED_25519_CURVE_SPEC.getCurve();
        int bumpSeed = 255;

        byte[] programAddress = new byte[32];
        try
        {
            while (bumpSeed > 0)
            {
                final ByteBuffer seeds = ByteBuffer.allocate(33);
                owner.write(seeds);
                seeds.put(getNextSeed(bumpSeed));

                final ByteBuffer programIdBuf = ByteBuffer.allocate(32);
                programId.write(programIdBuf);

                programAddress = createProgramAddress(seeds, programIdBuf);
                curve.createPoint(programAddress, false);
                bumpSeed--;
            }
        }
        catch (final RuntimeException re)
        {
            return new SolanaProgramDerivedAddress(new SolanaAccount(programAddress), owner, programId, bumpSeed);
        }

        throw new RuntimeException("Could not find a program address off the curve.");
    }

    /* protected */ static byte[] createProgramAddress(final ByteBuffer seeds, final ByteBuffer programAccount)
    {
        final MessageDigest messageDigest = Sha256Hash.newDigest();

        messageDigest.update(seeds.flip());
        messageDigest.update(programAccount.flip());
        messageDigest.update("ProgramDerivedAddress".getBytes(StandardCharsets.UTF_8));

        return messageDigest.digest();
    }

    /* protected */ static byte[] getNextSeed(final int currentSignedByteValue)
    {
        final byte[] seed = new byte[1];

        seed[0] = (byte) (currentSignedByteValue);

        return seed;
    }


    SolanaProgramDerivedAddress(final PublicKey address, final PublicKey owner, final PublicKey programAccount, final int nonce)
    {
        this.address = requireNonNull(address, "The address public key must be specified, but was null");
        this.owner = requireNonNull(owner, "The owner public key must be specified, but was null");
        this.programAccount = requireNonNull(programAccount, "The programId public key must be specified, but was null");
        this.nonce = nonce;
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
        final SolanaProgramDerivedAddress that = (SolanaProgramDerivedAddress) o;
        return nonce == that.nonce && Objects.equals(address, that.address) && Objects.equals(owner, that.owner) && Objects.equals(programAccount, that.programAccount);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(address, owner, programAccount, nonce);
    }

    @Override
    public String toString()
    {
        return "SolanaProgramDerivedAccount{" +
               "address=" + address +
               ", owner=" + owner +
               ", programAccount=" + programAccount +
               ", nonce=" + nonce +
               '}';
    }

    @Override
    public PublicKey address()
    {
        return address;
    }

    @Override
    public PublicKey owner()
    {
        return owner;
    }

    @Override
    public PublicKey programId()
    {
        return programAccount;
    }

    @Override
    public int nonce()
    {
        return nonce;
    }
}
