package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.encoding.SolanaEncoding;
import com.lmax.solana4j.encoding.TokenMetadata;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AssociatedTokenMetadataProgram
{
    private static final byte[] METADATA_MAGIC_STRING = "metadata".getBytes(UTF_8);
    private static final byte[] ASSOCIATED_TOKEN_METADATA_PROGRAM_ID = Base58.decode("metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s");
    public static final PublicKey ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT = Solana.account(ASSOCIATED_TOKEN_METADATA_PROGRAM_ID);

    public static ProgramDerivedAddress deriveAddress(final PublicKey mint)
    {
        return SolanaEncoding.deriveProgramAddress(List.of(METADATA_MAGIC_STRING, ASSOCIATED_TOKEN_METADATA_PROGRAM_ID, mint.bytes()), ASSOCIATED_TOKEN_METADATA_PROGRAM_ACCOUNT);
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
}
