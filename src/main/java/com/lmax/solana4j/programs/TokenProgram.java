package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.Destination;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilderBase;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lmax.solana4j.programs.SysVar.RENT;
import static com.lmax.solana4j.programs.SystemProgram.SYSTEM_PROGRAM_ACCOUNT;


@SuppressWarnings("unchecked") // return type cast is pretty safe after all as we're limited to our types only
public class TokenProgram<T extends TokenProgram<? extends TokenProgram<T>>>
{
    private static final byte[] TOKEN_PROGRAM_ID = Base58.decode("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(TOKEN_PROGRAM_ID);

    public static final int ACCOUNT_LAYOUT_SPAN = 165; // https://spl.solana.com/token

    private static final int INITIALIZE_MINT_INSTRUCTION = 0;
    private static final int INITIALIZE_ACCOUNT_INSTRUCTION = 1;
    private static final int INITIALIZE_MULTISIG_INSTRUCTION = 2;
    private static final int TRANSFER_INSTRUCTION = 3;
    private static final int APPROVE_INSTRUCTION = 4;
    private static final int REVOKE_INSTRUCTION = 5;
    private static final int SET_AUTHORITY_INSTRUCTION = 6;
    private static final int MINT_TO_INSTRUCTION = 7;
    private static final int BURN_INSTRUCTION = 8;
    private static final int CLOSE_ACCOUNT_INSTRUCTION = 9;
    private static final int FREEZE_ACCOUNT_INSTRUCTION = 10;
    private static final int THAW_ACCOUNT_INSTRUCTION = 11;
    private static final int TRANSFER_CHECKED_INSTRUCTION = 12;
    private static final int APPROVE_CHECKED_INSTRUCTION = 13;
    private static final int MINT_TO_CHECKED_INSTRUCTION = 14;
    private static final int BURN_CHECKED_INSTRUCTION = 15;
    private static final int INITIALIZE_ACCOUNT2_INSTRUCTION = 16;
    private static final int SYNC_NATIVE_INSTRUCTION = 17;
    private static final int INITIALIZE_ACCOUNT3_INSTRUCTION = 18;
    private static final int INITIALIZE_MULTISIG2_INSTRUCTION = 19;
    private static final int INITIALIZE_MINT2_INSTRUCTION = 20;
    public static final int AUTHORITY_TYPE_ACCOUNT_OWNER = 2;
    public static final int IDEMPOTENT_CREATE_INSTRUCTION = 1;
    public static final int CREATE_INSTRUCTION = 0;

    private final PublicKey programId;
    private final TransactionBuilderBase tb;

    public static TokenProgram<?> factory(final TransactionBuilderBase tb)
    {
        return new TokenProgram<>(PROGRAM_ACCOUNT, tb);
    }

    TokenProgram(final PublicKey tokenProgramId, final TransactionBuilderBase tb)
    {
        this.programId = tokenProgramId;
        this.tb = tb;
    }


    public T createInitializeAccountInstruction(final PublicKey account, final PublicKey mint, final PublicKey owner)
    {
        tb.append(ib -> ib
                .program(programId)
                .account(account, false, true)
                .account(mint, false, false)
                .account(owner, false, false)
                .account(RENT, false, false)
                .data(1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN).put((byte) INITIALIZE_ACCOUNT_INSTRUCTION))
        );
        return (T) this;
    }

    public T createInitializeMintInstruction(
            final PublicKey tokenMintAddress,
            final byte decimals,
            final PublicKey mintAuthority,
            final Optional<PublicKey> freezeAuthority)
    {
        tb.append(ib -> ib
                .program(programId)
                .account(tokenMintAddress, false, true)
                .account(RENT, false, false)
                .data(67, bb ->
                {
                    bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) INITIALIZE_MINT_INSTRUCTION)
                            .put(decimals);
                    mintAuthority.write(bb);
                    bb.put((byte) (
                            freezeAuthority.isPresent()
                            ? 1
                            : 0));
                    if (freezeAuthority.isPresent())
                    {
                        final PublicKey freezeAuthorityKey = freezeAuthority.get();
                        freezeAuthorityKey.write(bb);
                    }
                    else
                    {
                        bb.position(bb.position() + PublicKey.PUBLIC_KEY_LENGTH);
                    }
                })
        );
        return (T) this;
    }

    public T createTokenAccountInstruction(
            final PublicKey payer,
            final PublicKey associatedTokenAddress,
            final PublicKey mint,
            final PublicKey associatedTokenProgramId,
            final PublicKey owner,
            final boolean idempotent)
    {
        tb.append(ib -> ib
                .program(associatedTokenProgramId)
                .account(payer, true, true)
                .account(associatedTokenAddress, false, true)
                .account(owner, false, false)
                .account(mint, false, false)
                .account(SYSTEM_PROGRAM_ACCOUNT, false, false)
                .account(programId, false, false)
                .account(RENT, false, false)
                .data(1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) (idempotent ? IDEMPOTENT_CREATE_INSTRUCTION : CREATE_INSTRUCTION)))
        );
        return (T) this;
    }

    public T createMintToInstruction(
            final PublicKey mint,
            final PublicKey authority,
            final Destination destination)
    {
        tb.append(ib -> ib
                .program(programId)
                .account(mint, false, true)
                .account(destination.getDestination(), false, true)
                .account(authority, true, false)
                .data(1 + 8, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) MINT_TO_INSTRUCTION)
                        .putLong(destination.getAmount()))
        );
        return (T) this;
    }

    public T createMintToInstructions(
            final PublicKey mint,
            final PublicKey authority,
            final List<Destination> destinations)
    {
        for (final Destination destination : destinations)
        {
            tb.append(ib -> ib
                    .program(programId)
                    .account(mint, false, true)
                    .account(destination.getDestination(), false, true)
                    .account(authority, true, false)
                    .data(1 + 8, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) MINT_TO_INSTRUCTION)
                            .putLong(destination.getAmount()))
            );
        }
        return (T) this;
    }

    public T createTransferInstruction(
            final PublicKey source,
            final PublicKey destination,
            final PublicKey owner,
            final long amount)
    {
        return createTransferInstruction(source, destination, owner, amount, Collections.emptyList());
    }

    public T createTransferInstruction(
            final PublicKey source,
            final PublicKey destination,
            final PublicKey owner,
            final long amount,
            final List<PublicKey> signers)
    {
        tb.append(ib ->
        {
            ib
                    .program(programId)
                    .data(1 + 8, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) TRANSFER_INSTRUCTION)
                            .putLong(amount))
                    .account(source, false, true)
                    .account(destination, false, true)
                    .account(owner, signers.isEmpty(), false);
            signers.forEach(signer -> ib
                    .account(signer, true, false));
        });
        return (T) this;
    }

    public T createInitializeMultisigInstruction(
            final PublicKey multisigPublicKey,
            final List<PublicKey> signers,
            final int requiredSignatures)
    {
        tb.append(ib ->
        {
            ib
                    .program(programId)
                    .data(1 + 1, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) INITIALIZE_MULTISIG_INSTRUCTION)
                            .put((byte) requiredSignatures))
                    .account(multisigPublicKey, false, true)
                    .account(RENT, false, false);
            signers.forEach(signer -> ib
                    .account(signer, false, false));
        });
        return (T) this;
    }

    public T createSetAuthorityInstruction(
            final PublicKey tokenAccount,
            final PublicKey newAuthority,
            final PublicKey oldAuthority,
            final List<PublicKey> signers,
            final AuthorityType authorityType
    )
    {
        final var newAuthorityBuffer = ByteBuffer.allocate(PublicKey.PUBLIC_KEY_LENGTH);
        newAuthority.write(newAuthorityBuffer);

        tb.append(ib ->
        {
            ib
                    .program(programId)
                    .data(1 + 1 + 1 + PublicKey.PUBLIC_KEY_LENGTH, bb -> bb.order(ByteOrder.LITTLE_ENDIAN)
                            .put((byte) SET_AUTHORITY_INSTRUCTION)
                            .put(authorityType.value)
                            .put((byte) 1)
                            .put(newAuthorityBuffer.flip())
                    )
                    .account(tokenAccount, false, true)
                    .account(oldAuthority, signers.isEmpty(), false);
            signers.forEach(signer -> ib
                    .account(signer, true, false));
        });
        return (T) this;
    }

    public enum AuthorityType
    {
        MINT_TOKEN((byte) 0),
        FREEZE_ACCOUNT((byte) 1),
        ACCOUNT_OWNER((byte) 2),
        CLOSE_ACCOUNT((byte) 3),
        TRANSFER_FEE_CONFIG((byte) 4),
        WITHHELD_WITHDRAW((byte) 5),
        CLOSE_MINT((byte) 6),
        INTEREST_RATE((byte) 7),
        PERMANENT_DELEGATE((byte) 8),
        CONFIDENTIAL_TRANSFER_MINT((byte) 9),
        TRANSFER_HOOK_PROGRAM_ID((byte) 10),
        CONFIDENTIAL_TRANSFER_FEE_CONFIG((byte) 11),
        METADATA_POINTER((byte) 12);

        final byte value;

        AuthorityType(final byte value)
        {
            this.value = value;
        }

    }
}
