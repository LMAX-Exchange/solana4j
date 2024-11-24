package com.lmax.solana4j.client.jsonrpc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lmax.solana4j.client.api.AccountInfo;
import com.lmax.solana4j.client.api.Blockhash;
import com.lmax.solana4j.client.api.SimulateTransactionResponse;
import com.lmax.solana4j.client.api.SolanaRpcResponse;
import com.lmax.solana4j.client.api.TransactionResponse;

import java.util.List;

final class SimulateTransactionResponseDTO implements SolanaRpcResponse<SimulateTransactionResponse>
{
    private final ContextDTO context;
    private final SimulateTransactionValueDTO value;

    SimulateTransactionResponseDTO(
            final @JsonProperty("context") ContextDTO context,
            final @JsonProperty("value") SimulateTransactionValueDTO value)
    {
        this.context = context;
        this.value = value;
    }


    @Override
    public Context getContext()
    {
        return context;
    }

    @Override
    public SimulateTransactionResponse getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "SimulateTransactionResponseDTO{" +
               "context=" + context +
               ", value=" + value +
               '}';
    }

    public static final class SimulateTransactionValueDTO implements SimulateTransactionResponse
    {
        private final Object err;
        private final List<String> logs;
        private final AccountInfo.AccountInfoData accounts;
        private final List<TransactionResponseDTO.MetaDTO.InnerInstructionDTO> innerInstructions;
        private final BlockhashDTO.BlockhashValueDTO replacementBlockhash;
        private final DataDTO returnData;
        private final int unitsConsumed;

        @JsonCreator
        SimulateTransactionValueDTO(
                final @JsonProperty("err") Object err,
                final @JsonProperty("logs") List<String> logs,
                final @JsonProperty("accounts") AccountInfo.AccountInfoData accounts,
                final @JsonProperty("innerInstructions") List<TransactionResponseDTO.MetaDTO.InnerInstructionDTO> innerInstructions,
                final @JsonProperty("replacementBlockhash") BlockhashDTO.BlockhashValueDTO replacementBlockhash,
                final @JsonProperty("returnData") DataDTO returnData,
                final @JsonProperty("unitsConsumed") int unitsConsumed)
        {
            this.err = err;
            this.logs = logs;
            this.accounts = accounts;
            this.innerInstructions = innerInstructions;
            this.replacementBlockhash = replacementBlockhash;
            this.returnData = returnData;
            this.unitsConsumed = unitsConsumed;
        }

        @Override
        public Object getErr()
        {
            return err;
        }

        @Override
        public List<String> getLogs()
        {
            return logs;
        }

        @Override
        public AccountInfo.AccountInfoData getAccounts()
        {
            return accounts;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public List<TransactionResponse.InnerInstruction> getInnerInstructions()
        {
            return (List) innerInstructions;
        }

        @Override
        public Blockhash getReplacementBlockhash()
        {
            return replacementBlockhash;
        }

        @Override
        public Data getReturnData()
        {
            return returnData;
        }

        @Override
        public int getUnitsConsumed()
        {
            return unitsConsumed;
        }

        @Override
        public String toString()
        {
            return "SimulateTransactionValueDTO{" +
                    "err=" + err +
                    ", logs=" + logs +
                    ", accounts=" + accounts +
                    ", innerInstructions=" + innerInstructions +
                    ", replacementBlockhash=" + replacementBlockhash +
                    ", returnData=" + returnData +
                    ", unitsConsumed=" + unitsConsumed +
                    '}';
        }
    }

    static final class DataDTO implements SimulateTransactionResponse.Data
    {
        private final String programId;
        private final List<String> data;

        DataDTO(final @JsonProperty("programId") String programId, final @JsonProperty("data") List<String> data)
        {
            this.programId = programId;
            this.data = data;
        }

        @Override
        public String getProgramId()
        {
            return programId;
        }

        @Override
        public List<String> getData()
        {
            return data;
        }
    }
}
