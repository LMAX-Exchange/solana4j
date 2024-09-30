## solana4j

### Background

At `LMAX` we are all `Java` developers. We are not `Javascript` developers. We are not `Rust` developers. To support our needs
as a business we needed a way to read and write transactions from and to the `Solana Blockchain`. We challenged ourselves to 
encapsulate the `Solana` encoding scheme with a `Java` library rather than use any kind of shim calling the `Javascript` or `Rust` 
libraries supported by `Solana Labs`. This project is the product of that challenge.

This `solana4j` library supports both the `Legacy` and `VO` encoding schemes. It has support for interaction with some 
of the core `Solana Programs`, such as the `SystemProgram`, `TokenProgram` and `AssociatedTokenProgram`. The library can be 
trivially extended to support more native programs as the user needs. The scope of the current implemented programs simply encompasses
what we needed to implement for our needs as a business. 

We hope that people will use the library and extend it to their needs, so that together we can build a `Java` library with parity
to the aforementioned `Rust` and `Javascript` libraries. 

All the `Programs` that have been implemented have been conformance tested against a real `Solana Test Validator`. The hope
is that those tests act as living documentation of how the library can be used. Any future implemented programs should also
have accompanying integration tests so that we can carry on extending this living documentation!

We hope that you enjoy using the library, and we welcome any feedback. We especially welcome PRs!

### Features

##### Core Functionality

* Reading and Writing Legacy & V0 Message Encoding.
* Building Solana Messages for signing.
* Appending Signatures to Solana Messages for transaction submission.

#### Programs

  * `AddressLookupTableProgram`
  * `AssociatedTokenMetadataProgram`
  * `AssociatedTokenProgram`
  * `BpfLoaderUpgradeableProgram`
  * `ComputeBudgetProgram`
  * `SystemProgram`
  * `Token2022Program`
  * `TokenProgram`

#### Useful Commands

* `./gradlew build` - build the project
* `./gradlew javadocs` - build javadocs
* `./gradlew jar` - build jar
* `./gradlew sourcesJar` - build sources jar
* `./gradlew setupGitHooks` - create a git hook to prevent locally committing bad changes

#### Local Requirements

* `jdk11` installation
* `docker` (for `testcontainers`)