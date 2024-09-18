package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BpfUpgradeableProgramTest
{
    @Test
    void derivesCorrectProgramDerivedAddressForBpfUpgradeableProgram()
    {
        final PublicKey address = Solana.account(Base58.decode("EYB1g5R8beNtVqDpKpmkKWtLdhBY8Wh7q3QT3U3fbw7y"));
        final ProgramDerivedAddress programDerivedAddress = BpfLoaderUpgradeableProgram.deriveAddress(address);

        assertThat(Base58.encode(programDerivedAddress.address().bytes())).isEqualTo("6HRdAxQMgxR9vxnfsfeYYuyyEabzJ8SiA3H2SJ4NFU9d");
        assertThat(programDerivedAddress.nonce()).isEqualTo(255);
        assertThat(programDerivedAddress.programId().base58()).isEqualTo("BPFLoaderUpgradeab1e11111111111111111111111");
    }
}
