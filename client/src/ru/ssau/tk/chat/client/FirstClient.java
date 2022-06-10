package ru.ssau.tk.chat.client;

import javax.swing.*;

public class FirstClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }
}