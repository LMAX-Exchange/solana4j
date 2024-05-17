package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import net.i2p.crypto.eddsa.math.Curve;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable.ED_25519_CURVE_SPEC;

public final class AssociatedTokenMetadataProgram
{
    private static final byte[] METADATA_MAGIC_STRING = "metadata".getBytes(UTF_8);
    private static final byte[] ADDRESS_MAGIC_STRING = "ProgramDerivedAddress".getBytes(UTF_8);
    private static final byte[] ASSOCIATED_TOKEN_METADATA_PROGRAM_ID = Base58.decode("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");
    public static final PublicKey ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT = Solana.account(ASSOCIATED_TOKEN_METADATA_PROGRAM_ID);

    public static byte[] deriveAssociatedTokenMetadata(final PublicKey mint)
    {
        final Curve curve = ED_25519_CURVE_SPEC.getCurve();
        int bumpSeed = 255;

        byte[] programAddress = new byte[32];
        try
        {
            while (bumpSeed > 0)
            {
                final ByteBuffer seeds = ByteBuffer.allocate(197);
                seeds.put(METADATA_MAGIC_STRING);
                ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT.write(seeds);
                mint.write(seeds);
                seeds.put(getNextSeed(bumpSeed));

                final ByteBuffer programId = ByteBuffer.allocate(32);
                ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT.write(programId);

                programAddress = createProgramAddress(seeds, programId);
                curve.createPoint(programAddress, false);
                bumpSeed--;
            }
        }
        catch (final RuntimeException re)
        {
            return programAddress;
        }

        throw new RuntimeException("Could not find a program address off the curve.");
    }

    public static TokenMetadata extractTokenMetadata(final String base64Metadata)
    {
        final byte[] metadata = Base64.getDecoder().decode(base64Metadata);

        final ByteBuffer nameLengthBuffer = ByteBuffer.allocate(4);
        nameLengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nameLengthBuffer.put(metadata, 65, 4);
        nameLengthBuffer.flip();
        final int nameLength = nameLengthBuffer.getInt();

        final ByteBuffer nameBuffer = ByteBuffer.allocate(nameLength);
        nameBuffer.put(metadata, 69, nameLength);
        final String name = new String(nameBuffer.array(), UTF_8).trim();

        return new TokenMetadata(name);
    }

    private static byte[] createProgramAddress(final ByteBuffer seeds, final ByteBuffer programId)
    {
        final MessageDigest messageDigest = Sha256Hash.newDigest();

        messageDigest.update(seeds.flip());
        messageDigest.update(programId.flip());
        messageDigest.update(ADDRESS_MAGIC_STRING);

        return messageDigest.digest();
    }

    private static byte[] getNextSeed(final int currentSignedByteValue)
    {
        final byte[] seed = new byte[1];

        seed[0] = (byte) (currentSignedByteValue);

        return seed;
    }
}
