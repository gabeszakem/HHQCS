/*
 * Kikészitői minősitő rendszer szerver programja
 */
package hhqcs;

import hhqcs.form.LogViewer;
import hhqcs.form.TextAreaLogProgram;
import hhqcs.form.comco.Comco;
import hhqcs.form.Tray;
import hhqcs.setup.SetupDataManager;
import hhqcs.sql.SQL;
import hhqcs.tools.Debug;
import java.util.ArrayList;

/**
 *
 * @author Gabesz
 */
public class HHQCS {

    /**
     * Debugolás engedélyezése, Napi loggolással
     */
    public static Debug debug = new Debug(true, 1, "HHQCS.log");
    /**
     * SQL adatbázis engedélyezése
     */
    public static SQL sql;

    /**
     * Threadeket gyűjtő tömb
     */
    public static ArrayList<Thread> threads = new ArrayList<>();
    
    /**
     * A berendezés loggolásának kijelzése a képernyőn.
     */
    public static TextAreaLogProgram textAreaLog;
    
    /**
     * Loggolás textareában engedélyezése vagy tiltása. (LOGPANELISENABLED =
     * true)
     */
    public static final boolean LOGPANELISENABLED = true;
    
    
    
    public static LogViewer logViewer;
    
    
    /**
     * Loggolás megtekintésének az engedélyezése vagy tiltása. (LOGVIEWERISENABLED =
     * true)
     */
    public static final boolean LOGVIEWERISENABLED=true;

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        
        if (LOGPANELISENABLED) {
            HHQCS.debug.printDebugMsg(null, HHQCS.class.getName(), "Logpanel engedélyezve...");
            try {
                /**
                 * Új logPanel létrehozása.
                 */
                textAreaLog = new TextAreaLogProgram();

            } catch (Exception ex) {
                System.out.println("LogPanel inditása nem sikerült");
                HHQCS.debug.printDebugMsg(null, HHQCS.class.getName(), "(error) Logpanel indítása nem sikerült...", ex);
            }
        } else {
            HHQCS.debug.printDebugMsg(null, HHQCS.class.getName(), "Logpanel letiltva...");
        }

        /**
         * SQL Adatbázis létrehozása
         */
        sql = new SQL();
        /**
         * Alkalmazás beállítása
         */
        SetupDataManager.init(System.getProperty("user.dir"));
        try {
            /**
             * Frame megjelenítése Ikonként fog futni, ha nem sikerül futatni,
             * akkor a framet futtatjuk
             */
            Tray tray = new Tray();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            HHQCS.debug.printDebugMsg(null, HHQCS.class.getName(),
                    "Hiba történt a tray icon létrehozása közben :", ex);
            Comco comco = new Comco();
            comco.setVisible(true);
        }
        
        if(LOGVIEWERISENABLED){
            logViewer= new LogViewer(debug);
        }
    }
}
