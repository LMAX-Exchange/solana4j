package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.ByteBufferSigner;
import com.lmax.solana4j.api.Message;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.SignedMessageBuilder;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


final class SolanaSignedMessageBuilder implements SignedMessageBuilder
{
    private static final int SIGNATURE_LENGTH = 64;

    private final ByteBuffer buffer;
    private final Map<PublicKey, ByteBufferSigner> signers = new HashMap<>();

    SolanaSignedMessageBuilder(final ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    @Override
    public SignedMessageBuilder by(final PublicKey account, final ByteBufferSigner signer)
    {
        signers.put(account, signer);
        return this;
    }

    @Override
    public Message build()
    {
        class SigningInfo
        {
            final List<PublicKey> signatories;
            final ByteBuffer transaction;

            SigningInfo(final List<PublicKey> signatories, final ByteBuffer transaction)
            {
                this.signatories = signatories;
                this.transaction = transaction;
            }
        }
        final var message = new SolanaMessageReader(buffer.duplicate()).read();

        final var info = message.accept(
                message1 -> new SigningInfo(
                        message1.accounts().staticAccounts().subList(0, message1.countAccountsSigned()),
                        message1.transaction()));
        final ByteBuffer signingBufferView = this.buffer.duplicate();
        final SolanaMessageFormattingCommon formatter = new SolanaMessageFormattingCommon(signingBufferView);
        final int expectedSignatureCount = formatter.readInt();
        if (expectedSignatureCount != info.signatories.size())
        {
            throw new IllegalStateException("message is malformed");
        }
        for (final var account : info.signatories)
        {
            final var signer = signers.get(account);
            if (signer != null)
            {
                final ByteBuffer signatureBuffer = signingBufferView.duplicate();
                final int nextSignature = signatureBuffer.position() + SIGNATURE_LENGTH;
                signatureBuffer.limit(nextSignature);
                final ByteBuffer writableSignatureView = signatureBuffer.slice();
                final ByteBuffer transactionView = info.transaction.duplicate();
                signer.sign(transactionView, writableSignatureView);
                signingBufferView.position(nextSignature);
            }
            else
            {
                signingBufferView.position(signingBufferView.position() + SIGNATURE_LENGTH);
            }
        }
        return new SolanaMessage(buffer);
    }
}
