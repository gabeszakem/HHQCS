/*
 * Kikészitői minősitő rendszer szerver programja
 */
package hhqcs;

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
    public static Debug debug = new Debug(true, 1);
    /**
     * SQL adatbázis engedélyezése
     */
    public static SQL sql;

    /**
     * Threadeket gyűjtő tömb
     */
    public static ArrayList<Thread> threads = new ArrayList<Thread>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

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
    }
}
