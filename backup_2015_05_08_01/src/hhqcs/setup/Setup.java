/*
 * A beállítási adatokat tartalmazó osztály
 */
package hhqcs.setup;

/**
 *
 * @author Gabesz
 */
public class Setup {

    /**
     * Berendezés neve.
     */
    public String PLANTNAME = "";

    /**
     * Berendezés azonosítója.
     */
    public int PLANTID;

    /**
     * Berendezéshez tartozó rekordokat tároló tábla neve.
     */
    public String PLANTTABLENAME;

    /**
     * Berendezéshez tartozó vastagsági adatokattároló tábla neve.
     */
    public String THICKNESSTABLENAME;
    /**
     * Berendezéshez tartozó vastagsági rekordokat tároló tábla neve.
     */
    public String THICKNESSBLOBTABLENAME;

    /**
     * A plc ip címe
     */
    public String IPADDRESS = "";

    /**
     * Az adatküldéshez tartozó port száma.
     */
    public String PORTDATA = "";

    /**
     * Az életjel küldéshez tartozó port száma.
     */
    public String PORTLIFE = "";

    /**
     * A vastagság küldéshez tartozó port száma.
     */
    public String PORTTHICKNESS = "";

    /**
     * A thickness üzenetküldésének engedélyezése.
     */
    public boolean thicknessMessageEnable = false;

    /**
     * A centralográf terminál portja.
     */
    public int centTerminalPort;

    /**
     * A centralográf terminál pc ip címe.
     */
    public String centTerminalIpAddress;

    /**
     * A centralograf terminál udp kapcsolathoz a buffer mérete.
     */
    public int centTerminalbuffersize = 2048;
    /**
     * A centralograf terminál üzenetküldésének engedélyezése.
     */
    public boolean centTerminalMessageEnable = false;

}
