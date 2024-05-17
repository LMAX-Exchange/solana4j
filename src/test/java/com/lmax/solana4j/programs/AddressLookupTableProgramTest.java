package com.lmax.solana4j.programs;

import com.lmax.solana4j.Solana;
import com.lmax.solana4j.api.PublicKey;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AddressLookupTableProgramTest
{
    @Test
    void derivesCorrectAddressLookupAddressAndBumpSeed()
    {
        final PublicKey authority = Solana.account(Base58.decode("EYB1g5R8beNtVqDpKpmkKWtLdhBY8Wh7q3QT3U3fbw7y"));
        final AddressWithBumpSeed address = AddressLookupTableProgram.deriveLookupTableAddress(authority, Solana.slot(265008810));

        assertThat(Base58.encode(address.getLookupTableAddress())).isEqualTo("DmTtM8rQMcqR56ksBkayLd9KuYiFaGYZEAPnh5iUtgVV");
        assertThat(address.getBumpSeed()).isEqualTo(255);
    }
}
