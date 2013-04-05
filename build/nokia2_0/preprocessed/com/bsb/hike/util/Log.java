/**
Project      : Hike
Filename     : Log.java
Author       : sudheer
Comments     : 
Copyright    : Copyright © 2012, bsb.com 
Written under contract by Robosoft Technologies Pvt. Ltd.
History      : NA
 */
package com.bsb.hike.util;

import com.bsb.hike.Hike;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

/**
 * Log controlling whether to log or not based on the static
 * field <code<LOG_LEVEL</code>.
 * 
 * @author sudheer
 */
public class Log implements AppConstants {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    private static final int LOG_LEVEL = VERBOSE;
    private static final String TAG_PREFIX = "com.bsb.hike.";
    static FileConnection file = null;
    static DataOutputStream stream = null;
    private static final String LINE_BREAK = "\n";
    private static final boolean LOG_TO_FILE = !LOCAL;
    
    public static int v(String tag, String msg) {
        return LOG_LEVEL <= VERBOSE ? log(TAG_PREFIX + tag, msg) : 0;
    }
    
    public static int v(String tag, Object msg) {
        return LOG_LEVEL <= VERBOSE ? log(TAG_PREFIX + tag, msg != null ? msg.toString() : "null") : 0;
    }    

    public static int d(String tag, String msg) {
        return LOG_LEVEL <= DEBUG ? log(TAG_PREFIX + tag, msg) : 0;
    }

    public static int i(String tag, String msg) {
        return LOG_LEVEL <= INFO ? log(TAG_PREFIX + tag, msg) : 0;
    }

    public static int w(String tag, String msg) {
        return LOG_LEVEL <= WARN ? log(TAG_PREFIX + tag, msg) : 0;
    }

    public static int e(String tag, String msg) {
        return LOG_LEVEL <= ERROR ? log(TAG_PREFIX + tag, msg) : 0;
    }
    
    private static int log(String tag, String msg) {
        String log = "[" + tag + "] " + msg + " millis since launch: " + (new Date().getTime() - Hike.launchTime);
        System.out.println(log);
        writeLogToFile(log);
        return 1;
    }

    public static void openLogFile() {
        if (LOG_TO_FILE) {
            try {
                file = (FileConnection) Connector.open(AppConstants.LOG_FILE_URL, Connector.READ_WRITE);

                // If the file already exists it will be replaced.
                if (!file.exists()) {
                    file.create();
                }
                stream = file.openDataOutputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch(SecurityException ex){
                ex.printStackTrace();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public static void closeLogFile() {
        try {
            if (stream != null) {
                stream.close();
            }
            if (file != null) {
                file.close();
            }
        } catch (IOException ex) {
        }
    }

    private static void writeLogToFile(String text) {
        if (LOG_TO_FILE && stream != null) {
            if (TextUtils.isEmpty(text)) {
                return;
            }
            text += LINE_BREAK;
            try {
                stream.write(text.getBytes(TEXT_ENCODING));
                stream.flush();
            } catch (IOException ex) {
            }
        }
    }
}
