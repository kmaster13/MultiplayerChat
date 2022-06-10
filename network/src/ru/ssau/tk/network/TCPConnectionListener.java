package ru.ssau.tk.network;

// Слой абстракции, позволяющий по разному реагировать на события
public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);

    void onReceiveString(TCPConnection tcpConnection, String value);

    void onDisconnect(TCPConnection tcpConnection);

    void onException(TCPConnection tcpConnection, Exception e);
}