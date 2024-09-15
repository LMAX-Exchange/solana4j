package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilder;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

final class BpfLoaderUpgradableProgram
{
    private static final byte[] PROGRAM_ID = Base58.decode("BPFLoaderUpgradeab1e11111111111111111111111");
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(PROGRAM_ID);

    private static final int SET_AUTHORITY_INSTRUCTION = 4;

    private final TransactionBuilder tb;

    public static BpfLoaderUpgradableProgram factory(final TransactionBuilder tb)
    {
        return new BpfLoaderUpgradableProgram(tb);
    }

    BpfLoaderUpgradableProgram(final TransactionBuilder tb)
    {
        this.tb = tb;
    }

    public BpfLoaderUpgradableProgram setUpgradeAuthority(
            final PublicKey programAddress,
            final PublicKey currentAuthorityAddress,
            final Optional<PublicKey> maybeNewAuthorityAddress)
    {
        final PublicKey programDataAddress = Solana.programDerivedAddress(List.of(programAddress.bytes()), PROGRAM_ACCOUNT).address();

        tb.append(ib ->
        {
            ib.program(PROGRAM_ACCOUNT)
                    .account(programDataAddress, false, true)
                    .account(currentAuthorityAddress, true, false);
            maybeNewAuthorityAddress.ifPresent(newAuthorityAddress -> ib.account(newAuthorityAddress, false, false));
            ib.data(4, bb -> bb.order(ByteOrder.LITTLE_ENDIAN).putInt(SET_AUTHORITY_INSTRUCTION));
        });

        return this;
    }
}
