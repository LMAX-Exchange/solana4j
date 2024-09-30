/**
 * This module provides functionality for reading and writing messages on the Solana blockchain.
 * <p>
 *     The Solana Legacy and V0 encoding schemes are both supported. The library has support for interacting
 *     with core programs such as the SystemProgram, TokenProgram and AssociatedTokenProgram. Support for
 *     more programs can be easily extended.
 * </p>
 */
module com.lmax.solana4j {
    requires org.bitcoinj.core;
    requires net.i2p.crypto.eddsa;

    exports com.lmax.solana4j;
    exports com.lmax.solana4j.api;
    exports com.lmax.solana4j.programs;
    exports com.lmax.solana4j.encoding;
}