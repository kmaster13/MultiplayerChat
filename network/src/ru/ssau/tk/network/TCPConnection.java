package ru.ssau.tk.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;  //Поток, который получает входящее сообщение
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String ip, int port) throws IOException { //Конструктор создаёт сокет
        this(eventListener, new Socket(ip, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {   //Конструктор принимает сокет
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {  //Создание анонимного класса, для того, чтобы поток что-то выполнял
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                    disconnect();
                }
            }
        });
        rxThread.start();
    }

    // Модификатор доступа synchronized нужен для того, чтобы безопастно обращаться к методам из разных потоков

    public synchronized void sendString(String value) {
        try {
            out.write(value + "\n");
            out.flush();    //Сбрасывает все буферы и отправляет
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.isInterrupted();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}