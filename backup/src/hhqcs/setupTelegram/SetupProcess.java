/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.setupTelegram;

import static hhqcs.HHQCS.debug;
import hhqcs.net.tcp.TCPConnectionServer;
import java.util.Arrays;
import java.util.Date;
import tools.ByteArrayConcat;

/**
 *
 * @author gkovacs02
 */
public class SetupProcess {

    private static final int TELEGRAMLENGTH = 10;
    /**
     * Egy adat hossza
     */
    
    private String lastReceivedCoilNumber= new String();

    private byte[] tempReceiveTelegramm;

    // ------------------------------------ -------------------------- ---------------------------- -------------------------
    /**
     *
     * @param tcp TCPConnectionServer
     */
    public void process(TCPConnectionServer tcp) {
        /*
         * Telegram hosszának ellenőrzése
         */
        if (tcp.receiveTelegram.length == TELEGRAMLENGTH) {
            if (tempReceiveTelegramm != null) {
                /**
                 * Ha egész hosszúságú telegramm érkezett, akkor az átmeneti
                 * tárolót töröljük
                 */
                System.out.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " (tempReceiveTelegramm!=null): Az eldobandó byte tartalma: "
                        + Arrays.toString(tempReceiveTelegramm));
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)(tempReceiveTelegramm!=null): Az eldobandó byte tartalma:",
                        Arrays.toString(tempReceiveTelegramm));
                tempReceiveTelegramm = null;

            }
        } else {
            /*
             * Hibaüzenet kiirása
             */
            System.out.println(new Date().toString() + " "
                    + tcp.setup.PLANTNAME + " Telegram hossza nem megfelelő (" + tcp.receiveTelegram.length + ") (" + SetupProcess.class.getSimpleName() + ")");
          //  debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)A telegram hossza nem megfelelő: (" + tcp.receiveTelegram.length + ")");
            /**
             * Az átmeneti tárolóhoz hozzáadjuk a fogadott üzenetet
             */
            tempReceiveTelegramm = ByteArrayConcat.concat(tempReceiveTelegramm, tcp.receiveTelegram);
            if (tempReceiveTelegramm.length % TELEGRAMLENGTH == 0 && tempReceiveTelegramm.length / TELEGRAMLENGTH == 1) {
                /**
                 * A telegram helyreállítása sikeres volt
                 */
                tcp.receiveTelegram = ByteArrayConcat.concat(null, tempReceiveTelegramm);
                System.out.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " Telegram helyreállítása sikerült: " + tcp.receiveTelegram.length + " (" + SetupProcess.class.getSimpleName() + ")");
                tempReceiveTelegramm = null;
            }
        }

        /**
         * ----------------------------------------------------------------------------------------------------------------------------
         */
        if (tcp.receiveTelegram.length == TELEGRAMLENGTH) {
            String receivedCoilNumber = new String(tcp.receiveTelegram);
            System.out.println(new Date().toString() + " " + tcp.setup.PLANTNAME + " - " + "Új tekercs megérkezett a PLC-be " + receivedCoilNumber);
            if(!receivedCoilNumber.equals(lastReceivedCoilNumber)){
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "Új tekercs lett leküldve a PLC-be " + receivedCoilNumber);
            
            }
            if (receivedCoilNumber.equals(tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito)) {
                tcp.hhqcsServer.sentCoilIdentification = tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito;
                tcp.hhqcsServer.sentGuId = tcp.hhqcsServer.sapR3SetupData.guid;
            }
            lastReceivedCoilNumber=receivedCoilNumber;

        } else {
            /*
             * Hibaüzenet kiírása
             */
            System.out.println(new Date().toString() + " ( " + tcp.setup.PLANTNAME + " ) " + "Telegram üzenetazonosítója, vagy a tekercs azomnosítója nem megfelelő("
                    + SetupProcess.class.getSimpleName() + ")");
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)Telegram üzenetazonosítója, vagy a tekercs azomnosítója nem megfelelő: "
                    + ")");
        }
    }
}
