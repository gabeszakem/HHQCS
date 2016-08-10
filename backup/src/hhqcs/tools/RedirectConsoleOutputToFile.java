/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.tools;

import hhqcs.HHQCS;
import static hhqcs.HHQCS.debug;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 *
 * @author gkovacs02
 */
public class RedirectConsoleOutputToFile {

    private final String SEPARATOR = System.getProperty("file.separator");
    private final String USERDIR = System.getProperty("user.dir");
    private final String FILENAME = "HHQCS_CONSOLE";
    private File log;
    private static String logPath = null;
    private static int dayOfMonth = 0;

    public RedirectConsoleOutputToFile() {
        init(true);
        checkNewDay();
    }

    private void init(boolean appendFile) {
        try {
            log = new File(USERDIR + SEPARATOR + "log");
            if (!log.exists()) {
                boolean mkdir = log.mkdir();
                if (mkdir) {
                    System.out.println("Új könyvtár létrehozva: " + log.getCanonicalPath());
                    debug.printDebugMsg(null, RedirectConsoleOutputToFile.class.getName(), "(info)Új könyvtár létrehozva: " + log.getCanonicalPath());
                    logPath = log.getCanonicalPath();
                } else {
                    System.out.println("Hiba könyvtár létrehozása: " + log.getCanonicalPath());
                    debug.printDebugMsg(null, RedirectConsoleOutputToFile.class.getName(), "(info)Hiba könyvtár létrehozása: " + log.getCanonicalPath());
                }
            } else {
                logPath = log.getCanonicalPath();
                System.out.println("Loggolás a : " + log.getCanonicalPath() + " könyvtárba");
                debug.printDebugMsg(null, RedirectConsoleOutputToFile.class.getName(), "Loggolás a : " + log.getCanonicalPath() + " könyvtárba");
            }
            Calendar calendar = Calendar.getInstance();
            RedirectConsoleOutputToFile.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            String returned = USERDIR+SEPARATOR + "log" + SEPARATOR + FILENAME + "." + String.valueOf(this.dayOfMonth);
            setOut(returned, appendFile);

        } catch (FileNotFoundException ex) {
            debug.printDebugMsg(null, RedirectConsoleOutputToFile.class.getName(), "(error)Logolás átirányítása nem sikerült", ex);
        } catch (UnsupportedEncodingException ex) {
            debug.printDebugMsg(null, RedirectConsoleOutputToFile.class.getName(), "(error)Logolás átirányítása nem sikerült", ex);
        } catch (IOException ex) {
            debug.printDebugMsg(null,RedirectConsoleOutputToFile.class.getName(), "(error)Logolás átirányítása nem sikerült", ex);
        }
    }

    private void setOut(String log, boolean append) {
        try {

            FileOutputStream fos = new FileOutputStream(log, append);
            PrintStream out = new PrintStream(fos);
            System.setOut(out);
            debug.printDebugMsg(null, HHQCS.class.getName(), "(info)Logolás átirányítva a " + log + " fájlba");
        } catch (Exception ex) {
            debug.printDebugMsg(null, HHQCS.class.getName(), "(error)Logolás átirányítása nem sikerült", ex);
        }
    }

    private void checkNewDay() {
        Thread thread;
        thread = new Thread() {
            @Override
            public void run() {

                while (true) {
                    try {
                        synchronized (this) {
                            wait(1000);
                            Calendar calendar = Calendar.getInstance();
                            if (RedirectConsoleOutputToFile.dayOfMonth != calendar.get(Calendar.DAY_OF_MONTH)) {
                                RedirectConsoleOutputToFile.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                                init(false);
                            }
                        }
                    } catch (Exception ex) {
                        debug.printDebugMsg(null, RedirectConsoleOutputToFile.class.getName(), "(error)Logolás átirányítása nem sikerült", ex);
                    }

                }
            }
        };
        thread.start();
    }
}
