package org.pwl.telnet;

import org.pwl.cr.Logger;

import java.io.*;
import java.net.Socket;

public class Trans {

    public static void send(Socket socket, String encode, String text) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(text.getBytes(encode));
            bufferedOutputStream.flush();
        } catch (Exception e) {
            Logger.log(Thread.currentThread().getName() + " Socket closed.");
        }
    }

    public static String input(Socket socket, String encode) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write("> ".getBytes(encode));
            bufferedOutputStream.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encode));
            return bufferedReader.readLine();
        } catch (Exception e) {
            Logger.log(Thread.currentThread().getName() + " Socket closed.");
        }
        return "";
    }
}
