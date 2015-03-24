/*
 * A fájlokat kiolvassa a megadott könyvtárból, és ha xml a kiterjesztése
 * hozzáadja az arraylisthez
 */
package hhqcs.tools;

import hhqcs.HHQCS;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Gabesz
 */
public class FilesInDir {

    /**
     * xml file-okat tartalmzó arraylist
     */
    private static ArrayList files = new ArrayList();

    /**
     *
     * @param path
     * @return files ArrayList
     */
    public static ArrayList files(String path) {
        try {
            /*
             * A file/könyvtár azonosítása az elérési út alapján
             */
            File inFolder = new File(path);
            /*
             * Könyvtár ???
             */
            if (inFolder.isDirectory()) {
                /*
                 * A könyvtárból kinyerjük a fájlokat
                 */
                String filesString[] = inFolder.list();
                /*
                 * A fájlok ellenőrzése
                 */
                for (int i = 0; i < filesString.length; i++) {
                    /*
                     * A fájl ellenőrzése, hogy xml a kiterjesztése vagy nem
                     */
                    String fileName[] = filesString[i].split("\\.");
                    if (fileName.length > 0) {
                        if (fileName[fileName.length - 1].equals("xml")) {
                            /*
                             * A fájl xml kiterjesztesű ezért hozzáadjuk az
                             * ArrayList -hez
                             */
                            files.add(filesString[i]);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            /*Kivétel történt ezért a kiírjuk a hibát*/
            HHQCS.debug.printDebugMsg(null, FilesInDir.class.getName(),
                    "Kivétel történt a beállítási könyvtár feldolgozása közben", ex);
            JOptionPane.showMessageDialog(null, "Kivétel történt a beállítási könyvtár feldolgozása közben:\n" + ex.getMessage() + "\nAlapértelmezett értékekkel fog futni a program.", "Hiba", JOptionPane.ERROR_MESSAGE);
            files = new ArrayList();
        }
        return files;
    }
}
