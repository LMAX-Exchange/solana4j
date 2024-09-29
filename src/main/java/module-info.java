/**
 * This module provides core functionality for interacting with the Solana blockchain,
 * focusing on token operations such as minting, transferring, and managing token accounts.
 * <p>
 * The module is part of the solana4j library and exports key packages for token management
 * and transaction building.
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