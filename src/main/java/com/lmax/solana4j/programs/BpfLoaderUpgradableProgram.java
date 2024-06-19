package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.TransactionBuilderBase;
import org.bitcoinj.core.Base58;

import java.nio.ByteOrder;
import java.util.Optional;

final class BpfLoaderUpgradableProgram
{
    private static final byte[] PROGRAM_ID = Base58.decode("BPFLoaderUpgradeab1e11111111111111111111111");
    public static final PublicKey PROGRAM_ACCOUNT = Solana.account(PROGRAM_ID);

    private static final int INITIALIZE_BUFFER_INSTRUCTION = 0;
    private static final int WRITE_INSTRUCTION = 1;
    private static final int DEPLOY_WITH_MAX_DATA_LEN_INSTRUCTION = 2;
    private static final int UPGRADE_INSTRUCTION = 3;
    private static final int SET_AUTHORITY_INSTRUCTION = 4;
    private static final int CLOSE_INSTRUCTION = 5;
    private static final int SET_AUTHORITY_CHECKED_INSTRUCTION = 7;

    private final TransactionBuilderBase tb;

    public static BpfLoaderUpgradableProgram factory(final TransactionBuilderBase tb)
    {
        return new BpfLoaderUpgradableProgram(tb);
    }

    BpfLoaderUpgradableProgram(final TransactionBuilderBase tb)
    {
        this.tb = tb;
    }

    public BpfLoaderUpgradableProgram setUpgradeAuthority(
            final PublicKey programAddress,
            final PublicKey currentAuthorityAddress,
            final Optional<PublicKey> maybeNewAuthorityAddress)
    {
        final PublicKey programDataAddress = Solana.programDerivedAddress(programAddress, PROGRAM_ACCOUNT).address();

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
