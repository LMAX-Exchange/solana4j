#!/usr/bin/env python3
"""
Parse solana-versions.properties and create GitHub Actions matrix.

This script reads the versions file and creates a JSON matrix configuration
for GitHub Actions to test against all tracked versions.
"""

import json
import sys
import os


def main():
    """Parse versions file and output matrix configuration."""
    versions = {}
    try:
        with open('solana-versions.properties', 'r') as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith('#') and '=' in line:
                    key, value = line.split('=', 1)
                    if value != 'unknown':
                        versions[key] = value
    except FileNotFoundError:
        print("solana-versions.properties not found, using default", file=sys.stderr)
        # Fallback to gradle.properties
        try:
            with open('gradle.properties', 'r') as f:
                for line in f:
                    if line.startswith('SOLANA_VERSION='):
                        version = line.split('=')[1].strip()
                        versions['current'] = version
                        break
        except Exception as e:
            print(f"Error reading gradle.properties: {e}", file=sys.stderr)
            versions['current'] = '2.1.21'
    
    # Create matrix with unique versions
    matrix_config = {
        "include": [
            {"version": v, "label": k} 
            for k, v in versions.items()
        ]
    }
    
    # Deduplicate by version, keeping first label
    seen_versions = set()
    deduplicated = []
    for item in matrix_config["include"]:
        if item["version"] not in seen_versions:
            seen_versions.add(item["version"])
            deduplicated.append(item)
    
    matrix_config["include"] = deduplicated
    
    print(f"Matrix config: {json.dumps(matrix_config, indent=2)}")
    print(f"matrix={json.dumps(matrix_config)}")
    
    # Write to GitHub output
    with open(os.environ.get('GITHUB_OUTPUT', '/dev/stdout'), 'a') as f:
        f.write(f"matrix={json.dumps(matrix_config)}\n")


if __name__ == '__main__':
    main()
