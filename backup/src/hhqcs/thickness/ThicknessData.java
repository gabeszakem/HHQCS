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
public class ThicknessData {

    /**
     * Az adat futó sorszáma
     */
    public short sorSzam;

    /**
     * A beállított vastagság
     */
    public short SetupThickness;

    /**
     * A vastagság eltérése
     */
    public short DiffThickness;

    /**
     * Tekercsszám 8 szó
     */
    public String coilID;

}
