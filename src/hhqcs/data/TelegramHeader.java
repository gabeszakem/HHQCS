/*
 * Telegram fejrész
 * 
 */
package hhqcs.data;

/**
 *
 * @author Gabesz
 * PLC minden üzenettel elküldi. 
 * Nyugtázáskor a telegramId-t kell visszaküldeni
 */
public class TelegramHeader {
    
    /**
     * Üzenet azonositó  //Telegramonként emelkedő futósorszám
     * 0-65535
     */
    public int telegramID;
    
    /**
     * Tekercs azonosító //A tekercset azonósítja (NEM A TEKERCS SZÁMA)
     * 0 - 65535
     */
    public int CoilGeneratedId;
    
    /**
     * Az üzenet a tekercsen belül hányadik rész üzenet
     * 1-8
     */
    public int MessageId;
    
    /**
     * TelegramHeader mérete
     */   
    public final int size = 6;
    
}
