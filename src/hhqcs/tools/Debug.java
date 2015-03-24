/**
 * DEBUG engedélyezéskor a hibaüzeneteket kíírja a debug.log fájlba. Ellenőrzés,
 * hogy az utolsó üzenet és az újonnan kapott üzenetek mindegyike nem egyezik-e
 * meg. Az ellenőrzésre azért van szükség, nehogy teleírjuk hibaüzenettel a
 * merevlemezt.
 */
package hhqcs.tools;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Fehér Dávid, Gabesz
 */
public final class Debug {

    /**
     * Hibakeresési üzemmódban van-e a program hhqcs-ből
     */
    private boolean debugMode = false;
    /**
     * Logfájl íráshoz
     */
    private BufferedWriter out;
    /*
     * Előző üzeneteket tároló sor
     */
    private Deque<String> messages;
    /*
     * Az üzenet tároló mérete
     */
    private final int MESSAGESIZE = 3;
    /*
     * @logMode =0 Új log minden program újraindításakor @logMode =1 Naptári nap
     * körbejárásával tárolás
     */
    private int logMode = 0;
    
    private int dayOfMonth=0;

    /**
     *
     * @param debug boolean
     */
    public Debug(boolean debug) {

        this.debugMode = debug;
        this.messages = new ArrayDeque();
        init(true);
    }

    public Debug(boolean debug, int logMode) {
        if(debug){
            System.out.println("Debug engedélyezve...");
            if(logMode==0){
                System.out.println("Folyamatosan növekedő loggolás");
            }else if(logMode==1){
                System.out.println("Naptári nap szerinti loggolás");
            }
        }
        this.debugMode = debug;
        this.messages = new ArrayDeque();
        if (logMode >= 0 && logMode <= 1) {
            this.logMode = logMode;
        }
        init(true);
    }

    private void init(boolean appendFile) {
        try {
            String separator = System.getProperty("file.separator");
            String userDir = System.getProperty("user.dir");
            File log = new File(userDir + separator + "log");
            if (!log.exists()) {
                boolean mkdir = log.mkdir();
                if (mkdir) {
                    System.out.println("Új könyvtár létrehozva: " + log.getCanonicalPath());
                } else {
                    System.out.println("Hiba könyvtár létrehozása: " + log.getCanonicalPath());
                }
            } else {
                System.out.println("Loggolás a : " + log.getCanonicalPath() + " könyvtárba");
            }
            if (this.logMode == 0) {
                File[] contents = log.listFiles();
                String returned = separator + "log" + separator + "debug.log" + "." + contents.length;
                FileWriter fstream = new FileWriter(new File(userDir + returned), appendFile);
                this.out = new BufferedWriter(fstream);
                System.out.println("Loggolás a : " + userDir + returned + " fájlba");
            }else if(this.logMode==1){
                Calendar calendar= Calendar.getInstance();
                this.dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
                String returned = separator + "log" + separator + "debug.log" + "." + String.valueOf(this.dayOfMonth);
                FileWriter fstream = new FileWriter(new File(userDir + returned), appendFile);
                this.out = new BufferedWriter(fstream);
                System.out.println("Loggolás a : " + userDir + returned + " fájlba");
            }

        } catch (FileNotFoundException ex) {
            this.out = new BufferedWriter(new PrintWriter(System.out));
        } catch (UnsupportedEncodingException ex) {
            this.out = new BufferedWriter(new PrintWriter(System.out));
        } catch (IOException ex) {
            this.out = new BufferedWriter(new PrintWriter(System.out));
        }
    }

    /**
     *
     * @param aDebugMsg
     */
    private void printDebugMsg(String aDebugMsg) {
        if (this.debugMode) {
            /**
             * Ismételt hibaüzenet állapotának ellenőrzése
             */
            if(logMode==1){
                /*Naponta készítünk új fájlt*/
                Calendar calendar= Calendar.getInstance();
                if(this.dayOfMonth!=calendar.get(Calendar.DAY_OF_MONTH)){
                    this.dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
                    init(false);
                }
            }
            
            boolean check = false;
            try {
                if (this.messages.size() < this.MESSAGESIZE) {
                    /**
                     * Ha a kíírt üzeneteket, tároló vermet még nem puffereltük
                     * be, akkor hozzáadjuk az új üzenetet
                     */
                    messages.addLast(aDebugMsg);
                    /*
                     * Az ellenőrző bitet beállítjuk
                     */
                    check = true;
                } else {
                    for (Iterator<String> iter = this.messages.iterator(); iter.hasNext();) {
                        if (!(iter.next().equals(aDebugMsg))) {
                            /*
                             * Ha az új üzenettel nem egyezzik meg a korrábban
                             * kapott üzenet,akkor az ellenőrző bitet beállítjuk
                             */
                            check = true;
                        }
                    }
                    /*
                     * Eltávolítjuk a legrégebbi elemet
                     */
                    this.messages.removeFirst();
                    /*
                     * Hozzáadjuk az új elemet
                     */
                    this.messages.addLast(aDebugMsg);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            try {
                if (check) {
                    this.out.write("[" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(System.currentTimeMillis())) + "]\t" + aDebugMsg);
                    this.out.newLine();
                    this.out.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     *
     * @param plantMachine String
     * @param className String
     * @param message String
     * @param message2 String
     */
    public void printDebugMsg(String plantMachine, String className, String message, String message2) {
        String debugMsg = "";
        if (plantMachine != null) {
            debugMsg = "(" + plantMachine + ") ";
        }
        debugMsg = debugMsg + "[" + className + "] ";
        debugMsg = debugMsg + message + ":\n";
        debugMsg = debugMsg + "\t\t\t\t\t\t\t\t" + message2;
        printDebugMsg(debugMsg);
    }

    /**
     *
     * @param plantMachine String
     * @param className String
     * @param message String
     * @param message2 byte[]
     */
    public void printDebugMsg(String plantMachine, String className, String message, byte[] message2) {
        String debugMsg = "";
        if (plantMachine != null) {
            debugMsg = "(" + plantMachine + ") ";
        }
        debugMsg = debugMsg + "[" + className + "] ";
        debugMsg = debugMsg + message + ":\n";
        debugMsg = debugMsg + "\t\t\t\t\t\t\t\t" + message2;
        printDebugMsg(debugMsg);
    }

    /**
     *
     * @param plantMachine String
     * @param className String
     * @param message String
     * @param ex Exception
     */
    public void printDebugMsg(String plantMachine, String className, String message, Exception ex) {
        String debugMsg = "";
        if (plantMachine != null) {
            debugMsg = "(" + plantMachine + ") ";
        }
        debugMsg = debugMsg + "[" + className + "] ";
        debugMsg = debugMsg + message + ":\n";
        debugMsg = debugMsg + "\t\t\t\t\t\t\t\t" + PrintStackTraceToString.printStackTraceToString(ex);
        printDebugMsg(debugMsg);
    }

    /**
     *
     * @param plantMachine String
     * @param className String
     * @param message String
     */
    public void printDebugMsg(String plantMachine, String className, String message) {
        String debugMsg = "";
        if (plantMachine != null) {
            debugMsg = "(" + plantMachine + ") ";
        }
        debugMsg = debugMsg + "[" + className + "] ";
        debugMsg = debugMsg + message;
        printDebugMsg(debugMsg);
    }
}
