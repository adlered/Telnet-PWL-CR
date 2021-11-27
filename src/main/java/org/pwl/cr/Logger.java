package org.pwl.cr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void log(String log) {
        System.out.println("[Telnet-PWL-CR] " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " + log);
    }
}
