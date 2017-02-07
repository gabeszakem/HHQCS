/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.setupTelegram;

import static hhqcs.HHQCS.debug;
import hhqcs.net.tcp.TCPConnectionServer;
import java.nio.ByteBuffer;
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

    private String lastReceivedCoilNumber = new String();

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
            if (!receivedCoilNumber.equals(lastReceivedCoilNumber)) {
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "Új tekercs lett leküldve a PLC-be " + receivedCoilNumber);

            }
            if (receivedCoilNumber.equals(tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito)) {
                tcp.hhqcsServer.sentCoilIdentification = tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito;
                tcp.hhqcsServer.sentGuId = tcp.hhqcsServer.sapR3SetupData.guid;
            }
            if (!tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito.equals(tcp.hhqcsServer.sentCoilIdentification)) {
                System.out.println(new Date().toString() + " " + tcp.setup.PLANTNAME + " - " + "Új tekercs érkezett az SAP-tól " + tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito);
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "Új tekercs érkezett az SAP-tól " + tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito);
                byte[] sendTelegram = new byte[22];
                ByteBuffer.wrap(sendTelegram, 0, 10).put(tcp.hhqcsServer.sapR3SetupData.sapAlapanyagAzonosito.getBytes());
                ByteBuffer.wrap(sendTelegram, 10, 2).putShort(tcp.hhqcsServer.sapR3SetupData.alapanyagVastagsag);
                ByteBuffer.wrap(sendTelegram, 12, 2).putShort(tcp.hhqcsServer.sapR3SetupData.szerzodottVastagsagTuresMinimum);
                ByteBuffer.wrap(sendTelegram, 14, 2).putShort(tcp.hhqcsServer.sapR3SetupData.szerzodottVastagsagTuresPlusz);
                ByteBuffer.wrap(sendTelegram, 16, 2).putShort(tcp.hhqcsServer.sapR3SetupData.alapanyagSzelesseg);
                ByteBuffer.wrap(sendTelegram, 18, 2).putShort(tcp.hhqcsServer.sapR3SetupData.szerzodottSzelesseg);
                ByteBuffer.wrap(sendTelegram, 20, 2).putShort(tcp.hhqcsServer.sapR3SetupData.alapanyagSuly);

                tcp.hhqcsServer.tcpSetup.sendTelegram(sendTelegram);

                //}
            }
            lastReceivedCoilNumber = receivedCoilNumber;

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
