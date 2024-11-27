/**
 * This module provides functionality for the communication with a Solana blockchain.
 * <p>
 *      This module provides APIs for interacting with the Solana blockchain,
 *      including JSON-RPC communication and serialization of data structures.
 * </p>
 */
module com.lmax.solana4j.client {
    requires com.fasterxml.jackson.databind;
    requires java.net.http;

    exports com.lmax.solana4j.client.api;
    exports com.lmax.solana4j.client.jsonrpc;
}