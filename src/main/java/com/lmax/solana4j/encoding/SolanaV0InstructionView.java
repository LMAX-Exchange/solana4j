package com.lmax.solana4j.encoding;

import com.lmax.solana4j.api.AddressLookupTable;
import com.lmax.solana4j.api.MessageVisitor;
import com.lmax.solana4j.api.PublicKey;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

public class SolanaV0InstructionView extends SolanaInstructionView implements MessageVisitor.V0InstructionView
{
    private final MessageVisitor.V0AccountsView accountsView;

    SolanaV0InstructionView(
            final int program,
            final List<Integer> accountIndexes,
            final ByteBuffer data,
            final MessageVisitor.V0AccountsView accountsView)
    {
        super(program, accountIndexes, data);
        this.accountsView = accountsView;
    }

    @Override
    public List<PublicKey> accounts(final List<AddressLookupTable> addressLookupTables)
    {
        return accountIndexes()
                .stream()
                .map(idx -> accountsView.accounts(addressLookupTables).get(idx))
                .collect(Collectors.toList());
    }

    @Override
    public PublicKey program(final List<AddressLookupTable> addressLookupTables)
    {
        return accountsView.accounts(addressLookupTables).get(programIndex());
    }
}
