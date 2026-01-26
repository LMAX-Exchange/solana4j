/**
 * This module provides functionality for communicating with a solana node.
 */
module com.lmax.solana4j.client {
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires org.slf4j;

    exports com.lmax.solana4j.client.api;
    exports com.lmax.solana4j.client.jsonrpc;
}