package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static com.lmax.solana4j.Solana4jJsonRpcTestHelper.DATA;
import static com.lmax.solana4j.Solana4jJsonRpcTestHelper.LAMPORTS;
import static com.lmax.solana4j.Solana4jJsonRpcTestHelper.OBJECT_MAPPER;
import static com.lmax.solana4j.Solana4jJsonRpcTestHelper.OWNER;
import static com.lmax.solana4j.Solana4jJsonRpcTestHelper.RENT_EPOCH;
import static com.lmax.solana4j.Solana4jJsonRpcTestHelper.SLOT;
import static org.assertj.core.api.Assertions.assertThat;

class AccountInfoSerializationTest
{
    @Test
    void shouldSerializeAccountInfo() throws JsonProcessingException
    {
        final var context = new ContextDTO(SLOT);
        final var accountInfoValue = new AccountInfoDTO.AccountInfoValueDTO(
                LAMPORTS,
                OWNER,
                DATA,
                true,
                RENT_EPOCH
        );
        final var accountInfo = new AccountInfoDTO(
                context,
                accountInfoValue);

        final var accountInfoValueWrite = OBJECT_MAPPER.writeValueAsString(accountInfo);
        final var accountInfoValueRead = OBJECT_MAPPER.readValue(accountInfoValueWrite, AccountInfoDTO.class);

        assertThat(accountInfoValueRead.getContext()).usingRecursiveComparison().isEqualTo(context);
        assertThat(accountInfoValueRead.getValue()).usingRecursiveComparison().isEqualTo(accountInfoValue);
    }
}
