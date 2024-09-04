package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import net.i2p.crypto.eddsa.math.GroupElement;
import org.bitcoinj.core.Sha256Hash;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.lmax.solana4j.api.PublicKey.PUBLIC_KEY_LENGTH;
import static java.util.Objects.requireNonNull;
import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

class SolanaProgramDerivedAddress implements ProgramDerivedAddress
{
    public static final byte[] PROGRAM_DERIVED_ADDRESS_BYTES = "ProgramDerivedAddress".getBytes(StandardCharsets.UTF_8);
    private static final int BUMP_LENGTH = 1;

    final PublicKey address;
    final PublicKey programAccount;
    final int nonce;

    static ProgramDerivedAddress deriveProgramAddress(final List<byte[]> seeds, final PublicKey programId)
    {
        final int seedLength = seeds.stream().mapToInt(seed -> seed.length).sum();
        final int byteLength = seedLength + PUBLIC_KEY_LENGTH + PROGRAM_DERIVED_ADDRESS_BYTES.length + BUMP_LENGTH;

        int bumpSeed = 255;
        while (bumpSeed > 0)
        {
            final ByteBuffer seedsBuffer = ByteBuffer.allocate(byteLength);
            seeds.forEach(seedsBuffer::put);
            seedsBuffer.put(new byte[]{(byte) bumpSeed});

            programId.write(seedsBuffer);
            seedsBuffer.put(PROGRAM_DERIVED_ADDRESS_BYTES);

            final byte[] programAddress = Sha256Hash.hash(seedsBuffer.array());

            if (isOffCurve(programAddress))
            {
                return new SolanaProgramDerivedAddress(new SolanaAccount(programAddress), programId, bumpSeed);
            }
            bumpSeed--;
        }
        throw new RuntimeException("Could not find a program address off the curve.");
    }

    private static boolean isOffCurve(final byte[] programAddress)
    {
        try
        {
            final GroupElement point = ED_25519_CURVE_SPEC.getCurve().createPoint(programAddress, false);
            return !point.isOnCurve();
        }
        catch (final IllegalArgumentException e)
        {
            return true;
        }
    }

    public static ProgramDerivedAddress deriveProgramAddress(final PublicKey owner, final PublicKey programId)
    {
        return deriveProgramAddress(List.of(owner.bytes()), programId);
    }

    SolanaProgramDerivedAddress(final PublicKey address, final PublicKey programAccount, final int nonce)
    {
        this.address = requireNonNull(address, "The address public key must be specified, but was null");
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
        return nonce == that.nonce && Objects.equals(address, that.address) && Objects.equals(programAccount, that.programAccount);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(address, programAccount, nonce);
    }

    @Override
    public String toString()
    {
        return "SolanaProgramDerivedAccount{" +
                "address=" + address +
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
