/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.thickness;

/**
 *
 * @author gkovacs02
 */
public class ThicknessHeader {

    /**
     * A telegramm futó sorszáma
     */
    public int sorSzam;
    
    /**
     * Tekercs azonosító //A tekercset azonósítja (NEM A TEKERCS SZÁMA) 0 -
     * 65535
     */
    public int CoilGeneratedId=-1;
    
    /**
     * Tekercsszám 8 szó
     */
    public String coilID;
    
    /**
     * A lemez vastaggsága
     */
    public int setupThickness = -1;

    /**
     * A lemez pozitív tűrése
     */
    public int positivThicknessTolerance = -1;

    /**
     * A lemez negatív tűrése
     */
    public int negativThicknessTolerance =-1;

    /**
     * A küldött recordok száma
     */
    public int numberOfThicknessData=0;
    
    /**
     * A tekercs vége
     */
    public int endOfCoil=0;
    
    
    
    /**
     * A ThicknessHeader mérete byteban
     */
    
    public final int size = 30;
}
