/*
 * Az SAP álltal leadott adatokat tartalmazza
 */
package hhqcs.setupTelegram;

/**
 *
 * @author gkovacs02
 */
public class SapR3SetupData {

    /**
     * MANDT VARCHAR2 HOSSZ 3 DEFAULT '000' - mandt String
     */
    public String mandt = new String();
    /**
     * GUID RAW Hossz 16 -
     */
    //public byte[] guid = new byte[16];
    public String guid = new String();
    /**
     * BER_AZON Varchar2 Hossz 4 - berendezesAzonosito String;
     */
    public String berendezesAzonosito = new String();
    /**
     * CHARG Varchar2 Hossz 10 - sapAlapanyagAzonosito String
     */
    public String sapAlapanyagAzonosito = new String();
    /**
     * INSTIME VARCHAR2 Hossz 19 - idobelyeg String 
     */
    public String idobelyeg = "";
    /**
     * ALAPA_VAST NUMBER Hossz 5 tizedes 2 (mm) - alapanyagVastagsag short (um)
     */
    public short alapanyagVastagsag = 0;

    /**
     * SZERZ_VAST NUMBER Hossz 5 tizedes 2 (mm) - szerzodottVastagsag short
     * (um)
     */
    public short szerzodottVastagsag = 0;

    /**
     * SZERZ_VAST_TUR_MAX NUMBER Hossz 5 tizedes 2 (mm) -
     * szerzodottVastagsagTuresPlusz short (um)
     */
    public short szerzodottVastagsagTuresPlusz = 0;

    /**
     * SZERZ_VAST_TUR_Minimum NUMBER Hossz 5 tizedes 2 (mm) -
     * szerzodottVastagsagTuresMinimum short (um)
     */
    public short szerzodottVastagsagTuresMinimum = 0;

    /**
     * ALAPA_SZEL NUMBER Hossz 5 tizedes 1 (mm) - alapanyagSzelesseg short (mm)
     */
    public short alapanyagSzelesseg = 0;

    /**
     * SZERZ_SZEL NUMBER Hossz 4 - szerzodottSzelesseg short (mm)
     */
    public short szerzodottSzelesseg = 0;
    
     /**
     * ALAPA_SULY NUMBER Hossz 13 tizedes 3 - alapanyagSuly short (kg)
     */
    public short alapanyagSuly = 0;
    
    /**
     * ALAPA_BELSO_ATM NUMBER Hossz 4 - alapanyagBelsoAtmero short (mm)
     */
    public short alapanyagBelsoAtmero = 0;
    
    /**
     * ALAPA_KULSO_ATM NUMBER Hossz 4 - alapanyagBelsoAtmero short (mm)
     */
    public short alapanyagKulsoAtmero = 0;
    
    /**
     * LUSER VARCHAR2 Hossz 12 - luser String
     */
    public String luser = new String();
    
    /**
     * MEINS VARCHAR2 Hossz 3 - meins String
     */
    public String meins = new String();
}

