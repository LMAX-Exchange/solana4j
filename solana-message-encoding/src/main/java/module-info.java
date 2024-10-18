/**
 * This module provides functionality for reading and writing Solana messages.
 * <p>
 *     This module supports both the Solana Legacy and V0 encoding schemes. This module has support for interacting
 *     with core programs such as the SystemProgram, TokenProgram and AssociatedTokenProgram. This module can
 *     be easily extended to support more Solana Programs.
 * </p>
 */
module com.lmax.solana4j {
    exports com.lmax.solana4j;
    exports com.lmax.solana4j.api;
    exports com.lmax.solana4j.programs;
    exports com.lmax.solana4j.encoding;
    exports com.lmax.solana4j.util;
}