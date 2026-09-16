#!/usr/bin/env python3
"""
Update solana-versions.properties with fetched versions.

This script reads the fetched versions from JSON and updates the properties file,
preserving existing versions if new ones couldn't be fetched.
"""

import json
from datetime import datetime


def get_version(versions, existing_versions, key):
    """Get version, preferring new value but falling back to existing."""
    new_val = versions.get(key)
    if new_val and new_val != 'unknown':
        return new_val
    existing_val = existing_versions.get(key)
    if existing_val and existing_val != 'unknown':
        return existing_val
    return 'unknown'


def main():
    """Update solana-versions.properties with fetched versions."""
    # Load fetched versions
    with open('fetched_versions.json', 'r') as f:
        versions = json.load(f)
    
    # Read existing file to preserve versions that couldn't be fetched
    existing_versions = {}
    try:
        with open('solana-versions.properties', 'r') as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith('#') and '=' in line:
                    key, value = line.split('=', 1)
                    if value != 'unknown':
                        existing_versions[key] = value
    except FileNotFoundError:
        pass
    
    # Update or add version entries
    updated_lines = []
    updated_lines.append("# Solana versions for testing\n")
    updated_lines.append("# This file is automatically updated by the update-solana-versions workflow\n")
    updated_lines.append(f"# Last updated: {datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S UTC')}\n")
    updated_lines.append("# Format: network.name=version\n")
    updated_lines.append("\n")
    
    # Add all versions
    updated_lines.append("# Current production version (from gradle.properties)\n")
    updated_lines.append(f"current={get_version(versions, existing_versions, 'current')}\n")
    updated_lines.append("\n")
    
    updated_lines.append("# Solana mainnet-beta network\n")
    updated_lines.append(f"mainnet-beta={get_version(versions, existing_versions, 'mainnet-beta')}\n")
    updated_lines.append("\n")
    
    updated_lines.append("# Solana testnet network\n")
    updated_lines.append(f"testnet={get_version(versions, existing_versions, 'testnet')}\n")
    updated_lines.append("\n")
    
    updated_lines.append("# Solana devnet network\n")
    updated_lines.append(f"devnet={get_version(versions, existing_versions, 'devnet')}\n")
    updated_lines.append("\n")
    
    updated_lines.append("# Latest Agave release\n")
    updated_lines.append(f"agave-latest={get_version(versions, existing_versions, 'agave-latest')}\n")
    updated_lines.append("\n")
    
    updated_lines.append("# Latest Pythnet release (if different from Agave)\n")
    updated_lines.append(f"pythnet-latest={get_version(versions, existing_versions, 'pythnet-latest')}\n")
    updated_lines.append("\n")
    
    updated_lines.append("# Latest Firedancer release (when available)\n")
    updated_lines.append(f"firedancer-latest={get_version(versions, existing_versions, 'firedancer-latest')}\n")
    
    # Write updated file
    with open('solana-versions.properties', 'w') as f:
        f.writelines(updated_lines)
    
    print("Updated solana-versions.properties")


if __name__ == '__main__':
    main()
