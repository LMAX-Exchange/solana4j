package com.lmax.solana4j.programs;

import com.lmax.solana4j.parameterisation.ParameterizedMessageEncodingTest;
import org.junit.jupiter.api.BeforeEach;

final class StakeProgramIntegrationTest extends SolanaProgramsIntegrationTestBase
{
    @BeforeEach
    void beforeEachTest()
    {
        solana.createKeyPair("payer");
        solana.airdropSol("payer", "100000");
    }

    @ParameterizedMessageEncodingTest
    void shouldCreateAndInitializeStakeAccount(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("stakeAccount");
        solana.createKeyPair("stakeAuthority");
        solana.createKeyPair("withdrawAuthority");

        solana.createStakeAccount(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "withdrawAuthority: withdrawAuthority",
                "payer: payer",
                "amountSol: 1");

        solana.verifyStakeAccount("stakeAccount", "stakeAuthority", "withdrawAuthority");
    }

    @ParameterizedMessageEncodingTest
    void shouldDelegateStake(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("stakeAccount");
        solana.createKeyPair("stakeAuthority");
        solana.createKeyPair("withdrawAuthority");

        // Use the validator's vote account from the test validator
        solana.getValidatorVoteAccount("validatorVoteAccount");

        solana.createStakeAccount(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "withdrawAuthority: withdrawAuthority",
                "payer: payer",
                "amountSol: 10000");

        solana.verifyStakeAccount("stakeAccount", "stakeAuthority", "withdrawAuthority");

        solana.delegateStake(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "voteAccount: validatorVoteAccount",
                "payer: payer");

        solana.verifyStakeDelegated("stakeAccount", "validatorVoteAccount");
    }

    @ParameterizedMessageEncodingTest
    void shouldDeactivateStake(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("stakeAccount");
        solana.createKeyPair("stakeAuthority");
        solana.createKeyPair("withdrawAuthority");

        // Use the validator's vote account from the test validator
        solana.getValidatorVoteAccount("validatorVoteAccount");

        solana.createStakeAccount(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "withdrawAuthority: withdrawAuthority",
                "payer: payer",
                "amountSol: 10000");

        solana.delegateStake(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "voteAccount: validatorVoteAccount",
                "payer: payer");

        solana.deactivateStake(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "payer: payer");

        solana.verifyStakeDeactivating("stakeAccount");
    }

    @ParameterizedMessageEncodingTest
    void shouldWithdrawFromStake(final String messageEncoding)
    {
        solana.setMessageEncoding(messageEncoding);

        solana.createKeyPair("stakeAccount");
        solana.createKeyPair("stakeAuthority");
        solana.createKeyPair("withdrawAuthority");
        solana.createKeyPair("recipient");

        solana.createStakeAccount(
                "stakeAccount: stakeAccount",
                "stakeAuthority: stakeAuthority",
                "withdrawAuthority: withdrawAuthority",
                "payer: payer",
                "amountSol: 10000");

        solana.airdropSol("recipient", "0.001");

        solana.withdrawFromStake(
                "stakeAccount: stakeAccount",
                "withdrawAuthority: withdrawAuthority",
                "recipient: recipient",
                "payer: payer",
                "amountSol: 0.5");

        solana.solBalance("recipient", "0.501");
    }
}