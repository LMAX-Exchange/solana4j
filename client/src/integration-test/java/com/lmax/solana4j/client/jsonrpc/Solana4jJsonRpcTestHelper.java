package com.lmax.solana4j.client.jsonrpc;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;
import com.lmax.solana4j.buffer.ByteBufferPrimitiveArray;
import com.lmax.solana4j.programs.Token2022Program;
import com.lmax.solana4j.sign.BouncyCastleSigner;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

final class Solana4jJsonRpcTestHelper
{
    static byte[] createMintToTransactionBlob(
            final PublicKey payer,
            final Blockhash recentBlockhash,
            final PublicKey tokenMint,
            final PublicKey tokenMintAuthority,
            final Destination destination,
            final List<Signer> signers)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(recentBlockhash)
                .instructions(
                        tb -> Token2022Program.factory(tb)
                                .mintTo(
                                        tokenMint,
                                        tokenMintAuthority,
                                        List.of(destination))
                )
                .payer(payer)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final Signer signer : signers)
        {
            signedMessageBuilder.by(signer.signer, (transaction, signature) -> BouncyCastleSigner.sign(signer.privateKey, transaction, signature));
        }
        signedMessageBuilder.build();

        return ByteBufferPrimitiveArray.copy(buffer);
    }

    static byte[] createTransferTokenTransactionBlob(
            final PublicKey payer,
            final Blockhash recentBlockhash,
            final Destination destination,
            final PublicKey from,
            final PublicKey owner,
            final List<Signer> signers)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Solana.MAX_MESSAGE_SIZE);
        Solana.builder(buffer)
                .v0()
                .recent(recentBlockhash)
                .instructions(
                        tb -> Token2022Program.factory(tb)
                                .transfer(
                                        from,
                                        destination.getDestination(),
                                        owner,
                                        destination.getAmount(),
                                        signers.stream().map(x -> x.signer).collect(Collectors.toList()))
                )
                .payer(payer)
                .seal()
                .unsigned()
                .build();

        final SignedMessageBuilder signedMessageBuilder = Solana.forSigning(buffer);
        for (final Signer signer : signers)
        {
            signedMessageBuilder.by(signer.signer, (transaction, signature) -> BouncyCastleSigner.sign(signer.privateKey, transaction, signature));
        }
        signedMessageBuilder.build();

        return ByteBufferPrimitiveArray.copy(buffer);
    }

    static class Signer
    {
        private final PublicKey signer;
        private final byte[] privateKey;

        Signer(final PublicKey signer, final byte[] privateKey)
        {
            this.signer = signer;
            this.privateKey = privateKey;
        }
    }
}
