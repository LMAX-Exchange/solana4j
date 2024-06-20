package com.lmax.solana4j.solanaclient.api;

import java.util.List;

public interface Message
{
    List<String> getAccountKeys();

    Header getHeader();

    List<Instruction> getInstructions();

    String getRecentBlockhash();

    List<AddressTableLookup> getAddressTableLookups();
}
