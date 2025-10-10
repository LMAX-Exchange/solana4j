#### JMH Benchmarks

This `solana4j` library re-implements a few crucial pieces of functionality that are in standard crpytographic libraries,
mostly to avoid it requiring any direct dependencies. We thought it would be fun to compare the performance of our implementations
to that of these standard libraries.

To run the JMH benchmarks, it is easiest to run via `IntelliJ` with the `JMH Java Microbenchmark Harness` plugin installed, or by running:
```shell
./gradlew message-encoding:jmh
```

##### Base58 Encoding

Dependency Replaced: `org.bitcoinj:bitcoinj-core:0.17`  
Benchmark Written: `Base58EncodingBenchmark`  

```text
Benchmark                                                    Mode  Cnt       Score       Error  Units
Base58EncodingBenchmark.base58DecodeBitcoinjImplementation  thrpt   10  657702.566 ± 13829.050  ops/s
Base58EncodingBenchmark.base58DecodeSolana4jImplementation  thrpt   10  637746.254 ± 45468.005  ops/s
Base58EncodingBenchmark.base58EncodeBitcoinjImplementation  thrpt   10  151737.050 ±  2739.912  ops/s
Base58EncodingBenchmark.base58EncodeSolana4jImplementation  thrpt   10  151160.799 ±  1654.049  ops/s
```

##### Finding whether a point lies on the Ed25519 curve

Dependency Replaced: `net.i2p.crypto:eddsa:0.3.0`  
Benchmark Written: `EddsaPointOnCurveBenchmark`  

```text
Benchmark                                                          Mode  Cnt       Score      Error  Units
EddsaPointOnCurveBenchmark.pointNotOnCurveEddsaImplementation     thrpt   10  119028.140 ±  1515.956  ops/s
EddsaPointOnCurveBenchmark.pointNotOnCurveSolana4jImplementation  thrpt   10   64066.815 ±  1541.287  ops/s
EddsaPointOnCurveBenchmark.pointOnCurveEddsaImplementation        thrpt   10   65981.391 ±  4517.252  ops/s
EddsaPointOnCurveBenchmark.pointOnCurveSolana4jImplementation     thrpt   10   62261.907 ±  2639.227  ops/s
```



