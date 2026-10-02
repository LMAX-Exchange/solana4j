#!/usr/bin/env python3
"""
Fetch Solana versions from various sources.

This script queries Solana network RPC endpoints and GitHub releases
to discover the latest versions in use across different networks.
"""

import requests
import json
import sys
import re


def get_network_version(network_url):
    """Get Solana version from a network RPC endpoint."""
    try:
        response = requests.post(
            network_url,
            json={"jsonrpc": "2.0", "id": 1, "method": "getVersion"},
            timeout=10
        )
        if response.status_code == 200:
            data = response.json()
            version_str = data.get('result', {}).get('solana-core', '')
            # Extract version number (e.g., "1.18.22" from "1.18.22" or similar)
            match = re.search(r'(\d+\.\d+\.\d+)', version_str)
            if match:
                return match.group(1)
    except Exception as e:
        print(f"Error fetching version from {network_url}: {e}", file=sys.stderr)
    return None


def get_latest_github_release(owner, repo):
    """Get latest release version from GitHub repository."""
    try:
        response = requests.get(
            f"https://api.github.com/repos/{owner}/{repo}/releases/latest",
            timeout=10
        )
        if response.status_code == 200:
            data = response.json()
            tag_name = data.get('tag_name', '')
            # Extract version number, handling various tag formats
            match = re.search(r'v?(\d+\.\d+\.\d+)', tag_name)
            if match:
                return match.group(1)
    except Exception as e:
        print(f"Error fetching release from {owner}/{repo}: {e}", file=sys.stderr)
    return None


def main():
    """Fetch versions from different sources and save to JSON."""
    # Fetch versions from different sources
    versions = {}
    
    # Network endpoints
    print("Fetching mainnet-beta version...")
    versions['mainnet-beta'] = get_network_version('https://api.mainnet-beta.solana.com')
    
    print("Fetching testnet version...")
    versions['testnet'] = get_network_version('https://api.testnet.solana.com')
    
    print("Fetching devnet version...")
    versions['devnet'] = get_network_version('https://api.devnet.solana.com')
    
    # Latest releases from GitHub
    print("Fetching latest Agave release...")
    versions['agave-latest'] = get_latest_github_release('anza-xyz', 'agave')
    
    print("Fetching latest Pythnet release...")
    pythnet_version = get_latest_github_release('pyth-network', 'pythnet')
    versions['pythnet-latest'] = pythnet_version if pythnet_version else versions.get('agave-latest')
    
    # Firedancer is not yet released, so we'll use Agave for now
    print("Firedancer not yet released, using Agave version...")
    versions['firedancer-latest'] = versions.get('agave-latest')
    
    # Read current version from gradle.properties
    try:
        with open('gradle.properties', 'r') as f:
            for line in f:
                if line.startswith('SOLANA_VERSION='):
                    current_version = line.split('=')[1].strip()
                    versions['current'] = current_version
                    break
    except Exception as e:
        print(f"Error reading gradle.properties: {e}", file=sys.stderr)
    
    # Print results
    print("\nFetched versions:")
    for key, value in versions.items():
        print(f"  {key}: {value}")
    
    # Save to file for next step
    with open('fetched_versions.json', 'w') as f:
        json.dump(versions, f, indent=2)


if __name__ == '__main__':
    main()
