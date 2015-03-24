/*
 * A PLC álltal küldött 1 db tekercsadatának a fejrésze. 
 */
package hhqcs.data;

import java.sql.Timestamp;

/**
 *
 * @author Gabesz
 */
public class CoilHeader {
    /**
     * Telegram azonositó
     */
    public int telegramId=-1;
    /**
     * A telegram hossza a coilHeaderrel együtt
     */
    public int telegramLength;
    /**
     * A tekercs befelyezésének az időpontja. 4 szó
     */
    public Timestamp timeStamp;
    /**
     * Tekercsszám 8 szó
     */
    public String coilID;
    /**
     *A kész tekercs hossza
     */
    public int coilLength;
    /**
     *A CoilHeader mérete 
     */
    public final int size = 30;
}
