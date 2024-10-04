#### JMH Benchmarks

This `solana4j` library re-implements a few crucial pieces of functionality that are in standard crpytographic libraries,
mostly to avoid it requiring any direct dependencies. We thought it would be fun to compare the performance of our implementations
to that of these standard libraries.

To run the JMH benchmarks, it is easiest to run via `IntelliJ` with the `JMH Java Microbenchmark Harness` plugin installed.

##### Base58 Encoding

Dependency Replaced: `org.bitcoinj:bitcoinj-core:0.16.3`  
Benchmark Written: `Base58EncodingBenchmark`  

```text
Benchmark                                                    Mode  Cnt       Score       Error  Units
Base58EncodingBenchmark.base58DecodeBitcoinjImplementation  thrpt   10  804003.990 ±  8733.718  ops/s
Base58EncodingBenchmark.base58DecodeSolana4jImplementation  thrpt   10  805712.402 ± 10421.606  ops/s
Base58EncodingBenchmark.base58EncodeBitcoinjImplementation  thrpt   10  188832.893 ±  2511.813  ops/s
Base58EncodingBenchmark.base58EncodeSolana4jImplementation  thrpt   10  188675.567 ±  1932.977  ops/s
```

##### Finding whether a point lies on the Ed25519 curve

Dependency Replaced: `net.i2p.crypto:eddsa:0.3.0`  
Benchmark Written: `EddsaPointOnCurveBenchmark`  

```text
Benchmark                                                          Mode  Cnt       Score      Error  Units
EddsaPointOnCurveBenchmark.pointNotOnCurveEddsaImplementation     thrpt   10  148322.070 ± 2213.306  ops/s
EddsaPointOnCurveBenchmark.pointNotOnCurveSolana4jImplementation  thrpt   10   83202.803 ± 3213.023  ops/s
EddsaPointOnCurveBenchmark.pointOnCurveEddsaImplementation        thrpt   10   83929.318 ± 3607.478  ops/s
EddsaPointOnCurveBenchmark.pointOnCurveSolana4jImplementation     thrpt   10   84799.239 ± 5135.192  ops/s
```

#### Sha256 Hashing

Dependency Replaced: `org.bitcoinj:bitcoinj-core:0.16.3`    
Benchmark Written: `Sha256HashingBenchmark`  

```text
Benchmark                                                 Mode  Cnt        Score       Error  Units
Sha256HashingBenchmark.sha256HashBitcoinjImplementation  thrpt   10  9082306.173 ± 56286.482  ops/s
Sha256HashingBenchmark.sha256HashSolana4jImplementation  thrpt   10  9184423.015 ± 18583.452  ops/s
```


