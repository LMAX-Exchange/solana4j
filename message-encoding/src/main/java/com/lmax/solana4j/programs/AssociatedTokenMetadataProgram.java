package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.encoding.SolanaEncoding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Program for managing associated token metadata on the blockchain.
 */
public final class AssociatedTokenMetadataProgram
{

    /**
     * The magic string used for metadata.
     * <p>
     * This constant defines the magic string "metadata" used in deriving the associated token metadata address.
     * </p>
     */
    private static final byte[] METADATA_MAGIC_STRING = "metadata".getBytes(UTF_8);

    /**
     * The program id for the associated token metadata program.
     */
    private static final byte[] ASSOCIATED_TOKEN_METADATA_PROGRAM_ID = SolanaEncoding.decodeBase58("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");

    /**
     * The public key for the associated token metadata program account.
     */
    public static final PublicKey ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT = Solana.account(ASSOCIATED_TOKEN_METADATA_PROGRAM_ID);

    private AssociatedTokenMetadataProgram()
    {
    }

    /**
     * Derives the program address for the given mint.
     * <p>
     * This method derives the program address for a specific token mint by using a combination of the
     * metadata magic string, the associated token metadata program id, and the mint's public key.
     * </p>
     *
     * @param mint the public key of the token mint
     * @return the derived program address as a {@link ProgramDerivedAddress}
     */
    public static ProgramDerivedAddress deriveAddress(final PublicKey mint)
    {
        return SolanaEncoding.deriveProgramAddress(
                List.of(METADATA_MAGIC_STRING, ASSOCIATED_TOKEN_METADATA_PROGRAM_ID, mint.bytes()),
                ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT
        );
    }

    /**
     * Extracts the token name from the base64-encoded metadata.
     *
     * @param base64Metadata the base64-encoded metadata string
     * @return the extracted token name as a string
     */
    public static String extractTokenName(final String base64Metadata)
    {
        final byte[] metadata = Base64.getDecoder().decode(base64Metadata);

        final ByteBuffer nameLengthBuffer = ByteBuffer.allocate(4);
        nameLengthBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nameLengthBuffer.put(metadata, 65, 4);
        nameLengthBuffer.flip();
        final int nameLength = nameLengthBuffer.getInt();

        final ByteBuffer nameBuffer = ByteBuffer.allocate(nameLength);
        nameBuffer.put(metadata, 69, nameLength);

        return new String(nameBuffer.array(), UTF_8).trim();
    }
}
