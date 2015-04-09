/*
 * Beállítási adatok kezelése
 * 
 */
package hhqcs.setup;

import hhqcs.HHQCS;
import hhqcs.HHQCSServer;
import hhqcs.tools.CheckPort;
import hhqcs.tools.FilesInDir;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Fehér Dávid, Gabesz
 */
public class SetupDataManager {

    /**
     * Beállítási kulcsok
     */
    private static final String KEY_PLANT_ID = "plant_id";
    private static final String KEY_IP_ADDRESS = "plc_ip_address";
    private static final String KEY_PORTDATA = "port_for_data";
    private static final String KEY_LIFESIGNAL = "port_for_lifesignal";
    private static final String KEY_THICKNESSDATA = "port_for_thickness";

    /**
     * Beállítási kulcsok a centralográf terminálnak.
     */
    private static final String KEY_CENTTERMINAL_IP_ADDRESS = "centterm_ip_address";
    private static final String KEY_CENTTERMINAL_PORT = "centterm_port";

    /**
     * A beállítási XML fájl útvonala.
     */
    private static String setupDataXMLPath = "";
    /**
     * A beállítási könyvtár útvonala.
     */
    private static String setupDataDirectoryPath = "";
    /**
     *
     */
    public static ArrayList<HHQCSServer> hhqcsServers = new ArrayList();
    /**
     *
     */
    private static String errorMessage = "";

    // private konstruktor - statikus osztály
    private SetupDataManager() {
    }

    /**
     * Inicializálás
     *
     * @param aAppPath Az alkalmazás elérési útja.
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public static void init(String aAppPath) {
        /*
         * A beállítási adatokat tartalmazó könyvtár elérési útja
         */
        setupDataDirectoryPath = aAppPath + System.getProperty("file.separator") + "setup";
        System.out.println("setup könyvtár helye: " + setupDataDirectoryPath);
        HHQCS.debug.printDebugMsg(null, SetupDataManager.class.getName(), "setup könyvtár helye: " + setupDataDirectoryPath);
        /*
         * A beállítási adatokat tartalmazó könyvtárban található fájlok
         */
        ArrayList files = FilesInDir.files(setupDataDirectoryPath);
        /*
         * A fájlok vizsgálata
         */
        for (int index = 0; index < files.size(); index++) {

            FileInputStream fis;
            /*
             * Properties létrehozása
             */
            Properties fileProperties = new Properties();
            /*
             * a fájl helye
             */
            setupDataXMLPath = setupDataDirectoryPath + System.getProperty("file.separator") + files.get(index).toString();
            /*
             * Beállítások betöltése
             */
            try {
                fis = new FileInputStream(new File(setupDataXMLPath));
                fileProperties.loadFromXML(fis);
                fis.close();
            } catch (FileNotFoundException ex) {
                /*
                 * hiba történt, hiba okának kiírása
                 */
                HHQCS.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                        "SetupDataManager.init(): Kivétel történt a beállítási adatfájl feldolgozása közben", ex);
                JOptionPane.showMessageDialog(null, "Kivétel történt a " + setupDataXMLPath + " beállítási adatfájl feldolgozása közben:\n" + ex.getMessage() + "\nA program nem dolgozza fel a beállítási adat fájlt.", "Hiba", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                /*
                 * hiba történt, hiba okának kiírása
                 */
                HHQCS.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                        "SetupDataManager.init(): Kivétel történt a beállítási adatfájl feldolgozása közben", ex);
                JOptionPane.showMessageDialog(null, "Kivétel történt a " + setupDataXMLPath + " beállítási adatfájl feldolgozása közben:\n" + ex.getMessage() + "\nA program nem dolgozza fel a beállítási adat fájlt.", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
            /*
             * setup létrehozása
             */
            Setup setup = new Setup();
            /*
             * Adathelyesség ellenőrzés miatt a hibatároló bit létrehozása, és
             * false -ba állítása
             */
            boolean error = false;
            /*
             * IP_ADDRESS vizsgálata
             */
            if (fileProperties.containsKey(KEY_IP_ADDRESS)) {
                String ipAddress_nowh = fileProperties.getProperty(KEY_IP_ADDRESS).replaceAll("\\s+", "");
                /*
                 * Formátum ellenőrzése
                 */
                Pattern p = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
                Matcher m = p.matcher(ipAddress_nowh);
                boolean match = m.matches();
                if (match) {
                    /*
                     * IP cím beállítása
                     */
                    setup.IPADDRESS = ipAddress_nowh;
                } else {
                    error = true;
                    errorMessage = "hibás IP cim formátum: " + ipAddress_nowh;
                }
            } else {
                if (!error) {
                    error = true;
                    errorMessage = "Nem található a \"" + KEY_IP_ADDRESS + "\" kulcs érték";
                }
            }
            /*
             * PLANT_ID vizsgálata
             */
            if (fileProperties.containsKey(KEY_PLANT_ID) && !error) {
                String plantId = fileProperties.getProperty(KEY_PLANT_ID);
                try {
                    /*
                     * plant id integerré átalakítható ?
                     */
                    setup.PLANTID = Integer.parseInt(plantId);
                } catch (Exception ex) {
                    error = true;
                    errorMessage = "hibás PLANT ID formátum: " + plantId;
                }
            } else {
                if (!error) {
                    error = true;
                    errorMessage = "Nem található a \"" + KEY_PLANT_ID + "\" kulcs érték";
                }
            }
            /*
             * PORTDATA vizsgálata
             */
            if (fileProperties.containsKey(KEY_PORTDATA) && !error) {
                String portData = fileProperties.getProperty(KEY_PORTDATA).replaceAll("\\s+", "");
                /*
                 * Formátum ellenőrzés
                 */
                if (CheckPort.check(portData)) {
                    setup.PORTDATA = portData;
                } else {
                    error = true;
                    errorMessage = "hibás Port az adatokhoz: " + portData;
                }
            } else {
                if (!error) {
                    error = true;
                    errorMessage = "Nem található a \"" + KEY_PORTDATA + "\" kulcs érték";
                }
            }
            /*
             * LIFESIGNAL viszgálata
             */
            if (fileProperties.containsKey(KEY_LIFESIGNAL) && !error) {
                String portLife = fileProperties.getProperty(KEY_LIFESIGNAL).replaceAll("\\s+", "");
                /*
                 * Formátum ellenőrzés
                 */
                if (CheckPort.check(portLife)) {
                    setup.PORTLIFE = portLife;
                } else {
                    error = true;
                    errorMessage = "hibás Port az életjelhez: " + portLife;
                }
            } else {
                if (!error) {
                    error = true;
                    errorMessage = "Nem található a \"" + KEY_LIFESIGNAL + "\" kulcs érték";
                }
            }
            /*
             * THICKNESSDATA vizsgálata
             */
            if (fileProperties.containsKey(KEY_THICKNESSDATA) && !error) {
                String portThickness = fileProperties.getProperty(KEY_THICKNESSDATA).replaceAll("\\s+", "");
                /*
                 * Formátum ellenőrzés
                 */
                if (CheckPort.check(portThickness)) {
                    setup.PORTTHICKNESS = portThickness;
                    setup.thicknessMessageEnable=true;
                } else {
                    setup.thicknessMessageEnable=false;
                    errorMessage = "hibás Port a vastagsághoz " + portThickness;
                }
            } else {
                setup.thicknessMessageEnable=false;
                if (!error) {
                    errorMessage = "Nem található a \"" + KEY_THICKNESSDATA + "\" kulcs érték";
                }
            }
            
            
            if (!error) {
                /*
                 * PLANTNAME lekérdezése adatbázisból
                 */
                setup.PLANTNAME = HHQCS.sql.minadat_ID(setup.PLANTID);
                if (setup.PLANTNAME == null) {
                    error = true;
                    errorMessage = "hibás Plant ID:" + setup.PLANTID + " Nem található az Id -hoz tartozó berendezés az adatbázisban";
                } else {
                    setup.PLANTTABLENAME = "minadat_" + setup.PLANTID;
                }
            }
            if (!error) {

                /**
                 * Centralográf terminál adatok feldolgozása
                 */
                setup.centTerminalMessageEnable = centTerminalSetup(setup, fileProperties);
                /*
                 * Adatok feldolgozása sikeres volt
                 */
                hhqcsServers.add(new HHQCSServer(setup));
            } else {
                /*
                 * Hiba üzenet kiírása
                 */
                HHQCS.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                        "SetupDataManager.init(): Hiba történt a " + setupDataXMLPath + " beállítási könyvtár feldolgozása közben", errorMessage);
                JOptionPane.showMessageDialog(null, "Kivétel történt a " + setupDataXMLPath + " beállítási könyvtár feldolgozása közben:\n"
                        + errorMessage, "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static boolean centTerminalSetup(Setup setup, Properties fileProperties) {
        boolean centTerminalEnable = true;
        String errorCentTerminalMessage = "";
        /**
         * Ip cím vizsgálata
         */
        if (fileProperties.containsKey(KEY_CENTTERMINAL_IP_ADDRESS)) {
            String ipAddress_nowh = fileProperties.getProperty(KEY_CENTTERMINAL_IP_ADDRESS).replaceAll("\\s+", "");
            /*
             * Formátum ellenőrzése
             */
            Pattern p = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
            Matcher m = p.matcher(ipAddress_nowh);
            boolean match = m.matches();
            if (match) {
                /*
                 * IP cím beállítása
                 */
                setup.centTerminalIpAddress = ipAddress_nowh;
            } else {
                centTerminalEnable = false;
                errorCentTerminalMessage = "hibás Centralográf terminál IP cim formátum: " + ipAddress_nowh;
            }
        } else {
            if (centTerminalEnable) {
                centTerminalEnable = false;
                errorCentTerminalMessage = "Nem található a \"" + KEY_CENTTERMINAL_IP_ADDRESS + "\" kulcs érték";
            }
        }

        /*
         * CentTerminalDATA vizsgálata
         */
        if (fileProperties.containsKey(KEY_CENTTERMINAL_PORT) && centTerminalEnable) {
            String centTerminalPort = fileProperties.getProperty(KEY_CENTTERMINAL_PORT).replaceAll("\\s+", "");
            /*
             * Formátum ellenőrzés
             */
            if (CheckPort.check(centTerminalPort)) {
                try {
                    setup.centTerminalPort = Integer.parseInt(centTerminalPort);
                } catch (NumberFormatException ex) {
                    centTerminalEnable = false;
                    errorCentTerminalMessage = "hibás Port a centterminálhoz: " + centTerminalPort + " "
                            + ex.getMessage();
                }
            } else {
                centTerminalEnable = false;
                errorCentTerminalMessage = "hibás Port a centterminálhoz: " + centTerminalPort;
            }
        } else {
            if (centTerminalEnable) {
                centTerminalEnable = false;
                errorCentTerminalMessage = "Nem található a \"" + KEY_CENTTERMINAL_PORT + "\" kulcs érték";
            }
        }
        if (centTerminalEnable) {
            System.out.println(new Date().toString() +" " + "A centralográf adatok sikeresen beállítva  "
                    + setup.centTerminalIpAddress + " ip címre "
                    + setup.centTerminalPort+ " portra.");
        } else {
            HHQCS.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                    "SetupDataManager.init(): Hiba történt a " + setupDataXMLPath + " beállítási könyvtár feldolgozása közben", errorCentTerminalMessage);
         /*   JOptionPane.showMessageDialog(null, "Kivétel történt a " + setupDataXMLPath + " beállítási könyvtár feldolgozása közben:\n"
                    + errorCentTerminalMessage, "Hiba", JOptionPane.ERROR_MESSAGE);*/
        }
        return centTerminalEnable;
    }
}
