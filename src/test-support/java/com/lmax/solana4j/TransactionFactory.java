package com.lmax.solana4j;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.Blockhash;
import com.lmax.solana4j.api.PublicKey;
import com.lmax.solana4j.api.Slot;
import com.lmax.solana4j.programs.AddressWithBumpSeed;

import java.util.List;

public interface TransactionFactory
{
    String createAddressLookupTable(AddressWithBumpSeed addressWithBumpSeed,
                                    TestKeyPair authority,
                                    TestKeyPair payer,
                                    Slot recentSlot,
                                    Blockhash recentBlockhash,
                                    List<AddressLookupTable> addressLookupTables);

    String extendAddressLookupTable(PublicKey lookupAddress,
                                    TestKeyPair authority,
                                    TestKeyPair payer,
                                    List<PublicKey> addressesToAdd,
                                    Blockhash recentBlockhash,
                                    List<AddressLookupTable> addressLookupTables);
}
