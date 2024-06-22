package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.ProgramDerivedAddress;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AddressLookupTableProgramTest
{
    @Test
    void derivesCorrectProgramDerivedAddressForAddressLookupTableProgram()
    {
        final PublicKey authority = Solana.account(Base58.decode("EYB1g5R8beNtVqDpKpmkKWtLdhBY8Wh7q3QT3U3fbw7y"));
        final ProgramDerivedAddress programDerivedAddress = AddressLookupTableProgram.deriveAddress(authority, Solana.slot(265008810));

        assertThat(Base58.encode(programDerivedAddress.address().bytes())).isEqualTo("DmTtM8rQMcqR56ksBkayLd9KuYiFaGYZEAPnh5iUtgVV");
        assertThat(programDerivedAddress.nonce()).isEqualTo(255);
    }
}
