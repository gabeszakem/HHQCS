/*
 * Az adatok felépitése
 * 1 rekord 14 byte
 */
package hhqcs.data;

/**
 *
 * @author Gabesz
 */
public class Data {

    /**
     * Az adat futó sorszáma
     */
    public short sorSzam;
    /**
     * A hibahely rögzitésének helye [m]
     */
    public short length;
    /**
     * Az állapotokat tároló adatmező 10 byte. Intenzitásonként 2 byte [1-15]
     * hibafajta 5 intenzitás Összesen 5*15 bit
     */
    public byte[] allapot;
}
