## solana4j

### Background

At LMAX we are all Java developers. We are not Javascript developers. We are not Rust developers. To support our needs 
as a business we needed to read and write from and to the Solana blockchain from within our Java ecosystem. There was
no mature Java library that met our needs at the time our project started. We therefore challenged ourselves to write 
our own. This repository is the product of that challenge.

### `solana4j`

A pure Java library that encapsulates both the `Legacy` and `V0` encoding schemes, with support for many of 
the core Solana programs.

### `solana4j-json-rpc`

A Java client library to communicate with the Solana blockchain, with support for many of the endpoints documented
at `https://solana.com/docs/rpc`. This library requires the following dependencies:

* `com.fasterxml.jackson.core:jackson-databind:2.17.2`
* `com.fasterxml.jackson.core:jackson-annotations:2.17.2`
* `com.fasterxml.jackson.core:jackson-core:2.17.2`

### Requirements

* `jdk11` installation
* `docker` (for `testcontainers`)

If running on an `M[1,2,3,4] Mac` please read `shared/src/test-support/resources/README.md`

### PRs

* Fork the repository.
* Create a branch, for example: "amazing-new-feature-branch".
* Make your amazing changes. 
* Push your amazing changes to your "amazing-new-feature-branch" branch.
* Raise a PR.
* Wait patiently for comments.
* Receive praise.

The integration tests written act as living documentation. If you're making any changes, such
as extending the programs or endpoints supported, please can you continue extending this documentation!

Thanks.
MJ.