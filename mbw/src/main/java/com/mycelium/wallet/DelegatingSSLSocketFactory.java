/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wallet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * {@link SSLSocketFactory} which delegates all invocations to the provided delegate
 * {@code SSLSocketFactory}.
 */
public class DelegatingSSLSocketFactory extends SSLSocketFactory {
    private final SSLSocketFactory mDelegate;

    DelegatingSSLSocketFactory(SSLSocketFactory delegate) {
        this.mDelegate = delegate;
    }

    /**
     * Invoked after obtaining a socket from the delegate and before returning it to the caller.
     *
     * <p>The default implementation does nothing.
     */
    protected SSLSocket configureSocket(SSLSocket socket) throws IOException {
        return socket;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return mDelegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return mDelegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        SSLSocket socket = (SSLSocket) mDelegate.createSocket();
        return configureSocket(socket);
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose)
            throws IOException {
        SSLSocket socket = (SSLSocket) mDelegate.createSocket(s, host, port, autoClose);
        return configureSocket(socket);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) mDelegate.createSocket(host, port);
        return configureSocket(socket);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException {
        SSLSocket socket = (SSLSocket) mDelegate.createSocket(host, port, localHost, localPort);
        return configureSocket(socket);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) mDelegate.createSocket(host, port);
        return configureSocket(socket);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
                               int localPort) throws IOException {
        SSLSocket socket = (SSLSocket) mDelegate.createSocket(address, port, localAddress, localPort);
        return configureSocket(socket);
    }
}
