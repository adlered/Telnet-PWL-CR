package org.pwl.telnet;

import org.pwl.cr.Util;

import java.io.IOException;
import java.net.Socket;

public class TelnetUtil implements Runnable {
    private Socket socket;

    public TelnetUtil(Socket socket) {
        this.socket = socket;
    }

    public static String apiKey = "";

    public void run() {
        RunningConfig runningConfig = new RunningConfig();
        Trans.send(socket, runningConfig.encode, "Welcome to PWL ChatRoom! What is your system?\r\n");
        Trans.send(socket, runningConfig.encode, "1: Windows (GB2312)\r\n");
        Trans.send(socket, runningConfig.encode, "2: Linux/macOS (UTF-8)\r\n");
        String quote = Trans.input(socket, runningConfig.encode);
        if (quote.length() > 1) {
            runningConfig.encode = "UTF-8";
        } else if (quote.equals("1")) {
            runningConfig.encode = "GB2312";
        } else if (quote.equals("2")) {
            runningConfig.encode = "UTF-8";
        } else {
            Trans.send(socket, runningConfig.encode, "指令错误，拜拜\r\n");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Trans.send(socket, runningConfig.encode, "=======================================\r\n");
        Trans.send(socket, runningConfig.encode, "= 鱼油，欢迎来到摸鱼派聊天室Telnet端！\r\n");
        Trans.send(socket, runningConfig.encode, "=======================================\r\n");
        Trans.send(socket, runningConfig.encode, "请输入摸鱼派社区用户名或邮箱\r\n");
        String username = Trans.input(socket, runningConfig.encode);
        Trans.send(socket, runningConfig.encode, "请输入摸鱼派社区密码\r\n");
        String password = Trans.input(socket, runningConfig.encode);
        Trans.send(socket, runningConfig.encode, "登录验证中，请稍等\r\n");
        apiKey = Util.login(username, password);
        if (!apiKey.isEmpty()) {
            Trans.send(socket, runningConfig.encode, "验证成功，欢迎你，" + username + "\r\n");
            Trans.send(socket, runningConfig.encode, Util.getHelpText());
            Util.getMessages(apiKey, 1, socket, runningConfig);
        } else {
            Trans.send(socket, runningConfig.encode, "验证失败，用户名或密码错误。\r\n");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
