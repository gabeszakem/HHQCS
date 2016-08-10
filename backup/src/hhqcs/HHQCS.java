/*
 * Kikészitői minősitő rendszer szerver programja
 */
package hhqcs;

import form.LogViewer;
import form.TextAreaLogProgram;
import hhqcs.form.comco.Comco;
import hhqcs.form.Tray;
import hhqcs.setup.SetupDataManager;
import hhqcs.sql.OracleSQL;
import hhqcs.sql.PostgreSQL;
import hhqcs.tools.RedirectConsoleOutputToFile;
import java.awt.Color;
import tools.Debug;
import java.util.ArrayList;
import table.ColorStruct;

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
     * PostgreSQL adatbázis engedélyezése
     */
    public static PostgreSQL postgresql;

    /**
     * PostgreSQL adatbázis engedélyezése
     */
    public static OracleSQL oraclesql;

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
    public static final boolean LOGPANELISENABLED = false;

    public static LogViewer logViewer;

    /**
     * Loggolás megtekintésének az engedélyezése vagy tiltása.
     * (LOGVIEWERISENABLED = true)
     */
    public static final boolean LOGVIEWERISENABLED = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (LOGPANELISENABLED) {
            debug.printDebugMsg(null, HHQCS.class.getName(), "Logpanel engedélyezve...");
            try {
                /**
                 * Új logPanel létrehozása.
                 */
                textAreaLog = new TextAreaLogProgram(HHQCS.debug);

            } catch (Exception ex) {
                System.out.println("LogPanel inditása nem sikerült");
                debug.printDebugMsg(null, HHQCS.class.getName(), "(error)Logpanel indítása nem sikerült...", ex);
            }
        } else {
            RedirectConsoleOutputToFile out = new RedirectConsoleOutputToFile();
            debug.printDebugMsg(null, HHQCS.class.getName(), "Logpanel letiltva...");

        }

        /**
         * POSTGRE adatbázis létrehozása
         */
        postgresql = new PostgreSQL();

        /**
         * Oracle adatbázis létrehozása
         */
        oraclesql = new OracleSQL();

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
            debug.printDebugMsg(null, HHQCS.class.getName(), "(error)Hiba történt a tray icon létrehozása közben :", ex);
            Comco comco = new Comco();
            comco.setVisible(true);
        }

        if (LOGVIEWERISENABLED) {
            logViewer = new LogViewer(debug);
            ArrayList<ColorStruct> colors = new ArrayList();
            colors.add(new ColorStruct(1, new Color(243, 149, 149), "error"));
            colors.add(new ColorStruct(1, new Color(233, 173, 32), "warning"));
            colors.add(new ColorStruct(2, Color.GREEN, "(Húzvaegyengető)"));
            colors.add(new ColorStruct(2, Color.yellow, "(Georg hasító)"));
            colors.add(new ColorStruct(2, Color.ORANGE, "(1550-es hasító)"));
            colors.add(new ColorStruct(1, new Color(204, 229, 255), "info"));
            colors.add(new ColorStruct(1, Color.LIGHT_GRAY, "conn"));
            logViewer.setColor(colors);
        }
    }
}
