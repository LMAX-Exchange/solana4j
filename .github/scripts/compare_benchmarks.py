#!/usr/bin/env python3
"""
Compare JMH benchmark results and fail if there's a meaningful performance regression.
"""

import json
import sys
import os

def load_results(filepath):
    """Load JMH results from JSON file."""
    if not os.path.exists(filepath):
        print(f"Warning: Results file not found: {filepath}")
        return []
    
    with open(filepath, 'r') as f:
        return json.load(f)

def compare_benchmarks(baseline_results, current_results, threshold_percent=10.0):
    """
    Compare benchmark results and detect regressions.
    
    Args:
        baseline_results: List of benchmark results from baseline
        current_results: List of benchmark results from current run
        threshold_percent: Percentage threshold for regression (default 10%)
    
    Returns:
        tuple: (has_regression, report_lines)
    """
    # Create a map of benchmark name to score for baseline
    baseline_map = {}
    for result in baseline_results:
        benchmark_name = result.get('benchmark', '')
        score = result.get('primaryMetric', {}).get('score', 0)
        baseline_map[benchmark_name] = score
    
    # Compare each current result with baseline
    regressions = []
    improvements = []
    unchanged = []
    
    for result in current_results:
        benchmark_name = result.get('benchmark', '')
        current_score = result.get('primaryMetric', {}).get('score', 0)
        
        if benchmark_name not in baseline_map:
            unchanged.append(f"  {benchmark_name}: NEW (no baseline)")
            continue
        
        baseline_score = baseline_map[benchmark_name]
        
        if baseline_score == 0:
            unchanged.append(f"  {benchmark_name}: UNCHANGED (baseline=0)")
            continue
        
        # Calculate percentage change
        # For throughput (ops/s), higher is better
        percent_change = ((current_score - baseline_score) / baseline_score) * 100
        
        if percent_change < -threshold_percent:
            # Regression: current is significantly slower
            regressions.append({
                'name': benchmark_name,
                'baseline': baseline_score,
                'current': current_score,
                'change': percent_change
            })
        elif percent_change > threshold_percent:
            # Improvement: current is significantly faster
            improvements.append({
                'name': benchmark_name,
                'baseline': baseline_score,
                'current': current_score,
                'change': percent_change
            })
        else:
            # No meaningful change
            unchanged.append(
                f"  {benchmark_name}: {current_score:.2f} ops/s "
                f"(baseline: {baseline_score:.2f}, change: {percent_change:+.2f}%)"
            )
    
    # Build report
    report_lines = []
    report_lines.append("=" * 80)
    report_lines.append("JMH Benchmark Performance Comparison")
    report_lines.append("=" * 80)
    report_lines.append(f"Regression threshold: {threshold_percent}%")
    report_lines.append("")
    
    has_regression = len(regressions) > 0
    
    if regressions:
        report_lines.append("⚠️  PERFORMANCE REGRESSIONS DETECTED:")
        report_lines.append("")
        for reg in regressions:
            report_lines.append(f"  ❌ {reg['name']}")
            report_lines.append(f"     Baseline: {reg['baseline']:.2f} ops/s")
            report_lines.append(f"     Current:  {reg['current']:.2f} ops/s")
            report_lines.append(f"     Change:   {reg['change']:.2f}% (REGRESSION)")
            report_lines.append("")
    
    if improvements:
        report_lines.append("✅ Performance Improvements:")
        report_lines.append("")
        for imp in improvements:
            report_lines.append(f"  ✓ {imp['name']}")
            report_lines.append(f"     Baseline: {imp['baseline']:.2f} ops/s")
            report_lines.append(f"     Current:  {imp['current']:.2f} ops/s")
            report_lines.append(f"     Change:   {imp['change']:+.2f}%")
            report_lines.append("")
    
    if unchanged:
        report_lines.append("No significant change:")
        report_lines.append("")
        report_lines.extend(unchanged)
        report_lines.append("")
    
    report_lines.append("=" * 80)
    
    return has_regression, report_lines

def main():
    if len(sys.argv) < 3:
        print("Usage: compare_benchmarks.py <baseline_results.json> <current_results.json> [threshold_percent]")
        sys.exit(1)
    
    baseline_file = sys.argv[1]
    current_file = sys.argv[2]
    threshold = float(sys.argv[3]) if len(sys.argv) > 3 else 10.0
    
    print(f"Loading baseline results from: {baseline_file}")
    baseline_results = load_results(baseline_file)
    
    print(f"Loading current results from: {current_file}")
    current_results = load_results(current_file)
    
    if not baseline_results:
        print("⚠️  No baseline results found. Skipping comparison.")
        print("Current benchmark results will serve as the new baseline.")
        sys.exit(0)
    
    if not current_results:
        print("❌ No current results found. Cannot perform comparison.")
        sys.exit(1)
    
    print(f"Comparing {len(current_results)} benchmarks against {len(baseline_results)} baseline benchmarks...")
    print()
    
    has_regression, report = compare_benchmarks(baseline_results, current_results, threshold)
    
    # Print report
    for line in report:
        print(line)
    
    # Exit with error code if regressions detected
    if has_regression:
        sys.exit(1)
    else:
        sys.exit(0)

if __name__ == '__main__':
    main()
