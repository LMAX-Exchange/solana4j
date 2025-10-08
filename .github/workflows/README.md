# Solana Version Testing Automation

This directory contains GitHub Actions workflows that automate testing of Solana4j against multiple Solana releases.

## Overview

The automation consists of two main workflows:

### 1. Update Solana Versions (`update-solana-versions.yml`)

**Purpose**: Automatically track and update Solana versions from various sources.

**Schedule**: Runs daily at 2 AM UTC (can also be triggered manually via `workflow_dispatch`).

**What it does**:
- Queries Solana network RPC endpoints (mainnet-beta, testnet, devnet) to get their current versions
- Fetches the latest releases from:
  - Anza's Agave repository (https://github.com/anza-xyz/agave/)
  - Pyth Network's Pythnet repository (https://github.com/pyth-network/pythnet)
  - Firedancer repository (https://github.com/firedancer-io/firedancer) - when available
- Updates the `solana-versions.properties` file with the fetched versions
- Preserves existing versions if new versions cannot be fetched (network failures, etc.)
- Commits changes directly to the repository if new versions are detected

**Configuration**:
- The workflow uses Python to make HTTP requests to RPC endpoints and GitHub API
- Network timeouts are set to 10 seconds
- Failed requests are logged but don't fail the workflow

### 2. Test Solana Versions Matrix (`test-solana-versions.yml`)

**Purpose**: Run the full test suite against multiple Solana versions in parallel.

**Triggers**: 
- Push to `master` branch
- Pull requests targeting `master`
- Manual trigger via `workflow_dispatch`

**What it does**:
1. **Prepare Matrix**: Reads `solana-versions.properties` and creates a test matrix with all unique versions
2. **Test Execution**: For each version in the matrix:
   - Updates `gradle.properties` with the specific Solana version
   - Runs the full Gradle build including tests
   - Saves test results as artifacts
3. **Failure Reporting**: If any tests fail:
   - Creates a GitHub issue with details about the failure
   - Links to workflow run and test artifacts
   - Adds to existing open issue if one already exists

**Benefits**:
- Parallel execution speeds up testing
- Early detection of compatibility issues with new releases
- Automated issue creation reduces manual monitoring

## File Structure

```
.github/workflows/
├── update-solana-versions.yml   # Scheduled version updates
└── test-solana-versions.yml     # Matrix testing workflow

solana-versions.properties        # Version tracking file
```

## Solana Versions Properties File

The `solana-versions.properties` file tracks Solana versions from different sources:

```properties
# Current production version (from gradle.properties)
current=2.1.21

# Solana mainnet-beta network
mainnet-beta=2.1.21

# Solana testnet network  
testnet=2.1.21

# Solana devnet network
devnet=2.1.21

# Latest Agave release
agave-latest=2.1.21

# Latest Pythnet release (if different from Agave)
pythnet-latest=2.1.21

# Latest Firedancer release (when available)
firedancer-latest=2.1.21
```

This file is automatically updated by the `update-solana-versions` workflow and should not be manually edited.

## Manual Testing

To manually test against a specific Solana version:

1. Update `gradle.properties`:
   ```bash
   sed -i "s/SOLANA_VERSION=.*/SOLANA_VERSION=2.1.21/" gradle.properties
   ```

2. Run the tests:
   ```bash
   ./gradlew build
   ```

## Troubleshooting

### Workflow Failures

If the update workflow fails:
- Check the workflow logs for HTTP request errors
- Verify that the network endpoints are accessible
- Ensure GitHub API rate limits haven't been exceeded

If the test workflow fails:
- Review the test artifacts uploaded with the workflow run
- Check if the failure is specific to a particular Solana version
- Review any automatically created GitHub issues for details

### Version Format

The workflows expect version numbers in the format `X.Y.Z` (e.g., `2.1.21`). The scripts handle various tag formats from GitHub releases (e.g., `v2.1.21` or `2.1.21`).

## Future Enhancements

Potential improvements to this automation:

1. **Pull Request Creation**: Instead of direct commits, create PRs for version updates to allow review
2. **Selective Testing**: Only test against versions that have changed
3. **Performance Metrics**: Track and report test execution times across versions
4. **Notification Integration**: Send notifications (Slack, email) when new versions are detected or tests fail
5. **Agave Feature Sets**: Track feature set numbers alongside versions for more precise compatibility testing

## Contributing

When modifying these workflows:
- Test changes locally where possible using `act` or similar tools
- Update this README if behavior changes
- Consider the impact on CI/CD execution time and GitHub Actions usage
