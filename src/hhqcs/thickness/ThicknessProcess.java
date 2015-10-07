/*
 * To change this license thHeader, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.thickness;

import static hhqcs.HHQCS.debug;
import hhqcs.compress.Compressor;
import hhqcs.net.tcp.TCPConnectionServer;
import java.util.Arrays;
import java.util.Date;
import tools.ByteArrayConcat;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author gkovacs02
 */
public class ThicknessProcess {

    /**
     * Telegram hossza
     */
    private static final int TELEGRAMLENGTH = 1630;

    /**
     * Temp változó a telegram darabok fogadásához
     */
    private byte[] tempReceiveTelegramm;

    private ThicknessHeader thicknessHeader;

    private ThicknessHeader previousThicknessHeader = new ThicknessHeader();

    //private int coilID = -1;
    // private ArrayList<ThicknessData> tD = new ArrayList<>();
    /**
     * Az előző üzenet azonositója
     */
    private int messageId = 0;

    /**
     * plc-től kapott rész telegrammok összesége
     */
    private byte[] collectedReceiveTelegram = null;

    public void process(TCPConnectionServer tcp) {

        /*
         * Telegram hosszának ellenőrzése
         */
        if (tcp.receiveTelegram.length == TELEGRAMLENGTH) {
            /**
             * Annak ellenőrzése, hogy van-e adat az átmeneti tárolóban
             */
            if (tempReceiveTelegramm != null) {
                /**
                 * Ha egész hosszúságú telegramm érkezett, akkor az átmeneti
                 * tárolót töröljük
                 */
                System.out.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " (tempReceiveTelegramm!=null): Az eldobandó byte hossza: "+tempReceiveTelegramm.length+" Az eldobandó byte tartalma: "
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
                    + tcp.setup.PLANTNAME + " Telegram hossza nem megfelelő (" + tcp.receiveTelegram.length + ") (" + ThicknessProcess.class.getSimpleName() + ")");
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)A telegram hossza nem megfelelő: (" + tcp.receiveTelegram.length + ")");
            /**
             * Az átmeneti tárolóhoz hozzáadjuk a fogadott üzenetet
             */
            tempReceiveTelegramm = ByteArrayConcat.concat(tempReceiveTelegramm, tcp.receiveTelegram);
            /**
             * Telegram hosszának ellenőrzése.
             */
            if (tempReceiveTelegramm.length % TELEGRAMLENGTH == 0 && tempReceiveTelegramm.length / TELEGRAMLENGTH == 1) {
                /**
                 * A telegram helyreállítása sikeres volt
                 */
                tcp.receiveTelegram = ByteArrayConcat.concat(null, tempReceiveTelegramm);
                System.out.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " Telegram helyreállítása sikerült: " + tcp.receiveTelegram.length + " (" + ThicknessProcess.class.getSimpleName() + ")");
                tempReceiveTelegramm = null;
            } else if (tempReceiveTelegramm.length >= TELEGRAMLENGTH) {
                /**
                 * A telegramban résztelegram található
                 */
                try {
                    System.arraycopy(tempReceiveTelegramm, 0, tcp.receiveTelegram, 0, TELEGRAMLENGTH);
                    System.arraycopy(tempReceiveTelegramm, TELEGRAMLENGTH, tcp.receiveTelegram, 0, tempReceiveTelegramm.length - TELEGRAMLENGTH);

                } catch (Exception ex) {
                    System.out.println(new Date().toString() + " "
                            + tcp.setup.PLANTNAME + " A hosszabb telegram "+tempReceiveTelegramm.length +"darabolása nem sikerült ("+ ThicknessProcess.class.getSimpleName() + ")"+ex);
                    
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)A hosszabb telegram "+tempReceiveTelegramm.length +"darabolása nem sikerült ("+ ThicknessProcess.class.getSimpleName() + ")",ex);
                }

            }
        }

        /**
         * -------------------------------------------------------------------------------------------------------------------------------------
         * Telegramm feldolgozása
         */
        //System.out.println("telegram feldolgozás....");
        if (tcp.receiveTelegram.length == TELEGRAMLENGTH) {

            /*
             * Telegram fejrészének másolása
             */
            thicknessHeader = thicknessHeaderProcess(tcp);
            /* System.out.println("SORSZAM: "+thicknessHeader.sorSzam + 
             " COILGENERATEDID: "+thicknessHeader.CoilGeneratedId
             +" TEKERCSSZAM: "+thicknessHeader.coilID
             +" SIZE: "+thicknessHeader.size + " ENDOFCOIL: "+
             thicknessHeader.endOfCoil);*/

            /*#################################################################################################################################
             * Ellenőrzése, hogy az üzenetek megfelelő sorban érkeztek -e meg
             */
            if (thicknessHeader.sorSzam - messageId != 1) {

                /*
                 * Nem megfelelő sorrendben érkeztek meg az üzenetek, ezért
                 * kiírjuk a hibát
                 */
                System.err.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " Hiba az üzenet azonositónál: (" + messageId + " : " + thicknessHeader.sorSzam + ")");
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)Hiba az üzenet azonositónál: (" + messageId + " : " + thicknessHeader.sorSzam + ")");
            }

            if (!thicknessHeader.coilID.equals(previousThicknessHeader.coilID) || thicknessHeader.CoilGeneratedId != previousThicknessHeader.CoilGeneratedId
                    || thicknessHeader.endOfCoil != previousThicknessHeader.endOfCoil || thicknessHeader.negativThicknessTolerance != previousThicknessHeader.negativThicknessTolerance
                    || thicknessHeader.positivThicknessTolerance != previousThicknessHeader.positivThicknessTolerance || thicknessHeader.size != previousThicknessHeader.size) {
                System.out.println(new Date().toString() + " " + tcp.setup.PLANTNAME + " !!!!! A fejléc megváltozott : ===================================================");
                System.out.println("\t sorszám:                  " + previousThicknessHeader.sorSzam + " ----> " + thicknessHeader.sorSzam);
                System.out.println("\t coilgenerated id:         " + previousThicknessHeader.CoilGeneratedId + " ----> " + thicknessHeader.CoilGeneratedId);
                System.out.println("\t coil id:                  " + previousThicknessHeader.coilID + " ----> " + thicknessHeader.coilID);
                System.out.println("\t setup thicness:           " + previousThicknessHeader.setupThickness + " ----> " + thicknessHeader.setupThickness);
                System.out.println("\t + thickness tolarence:    " + previousThicknessHeader.positivThicknessTolerance + " ----> " + thicknessHeader.positivThicknessTolerance);
                System.out.println("\t - thickness tolarence:    " + previousThicknessHeader.negativThicknessTolerance + " ----> " + thicknessHeader.negativThicknessTolerance);
                System.out.println("\t number of thickness data: " + previousThicknessHeader.numberOfThicknessData + " ----> " + thicknessHeader.numberOfThicknessData);
                System.out.println("\t tekercs vége jel        : " + previousThicknessHeader.endOfCoil + " ----> " + thicknessHeader.endOfCoil);
            }

            /* Új tekercs kezdődik */
            if (thicknessHeader.CoilGeneratedId != previousThicknessHeader.CoilGeneratedId) {
                System.out.println(new Date().toString() + "!!!!! Új tekercs Kezdődik --> thicknessHeader.CoilGeneratedId = " + thicknessHeader.CoilGeneratedId + " coilID=" + previousThicknessHeader.CoilGeneratedId);
                /**
                 * Volt hiba??? Mielött új generált tekercsszám érkezik kell
                 * kapnunk "tekercs vége" jelet. Ha valamiért elmaradt a jel,
                 * akkor is fel kell dolgozni az adatokat
                 */
                if (messageId != 0) {
                    System.err.println(new Date().toString() + " "
                            + tcp.setup.PLANTNAME + " Nem érkezett meg a tekercs vége jel: (" + messageId + " : " + previousThicknessHeader.sorSzam + ")");
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)Nem érkezett meg a tekercs vége jel:: (" + messageId + " : " + previousThicknessHeader.sorSzam + ")");

                    /**
                     * ####################################### Adatok letárolása
                     * ########################################
                     */
                    hhqcs.HHQCS.sql.thicknessRecord(previousThicknessHeader, Compressor.compress(collectedReceiveTelegram), tcp.setup);
                    messageId = 0;
                    collectedReceiveTelegram = null;
                }

                collectedReceiveTelegram = null;
                //tD = new ArrayList<>();
            }

            if (thicknessHeader.numberOfThicknessData > 0) {
                // System.out.println("!!!!! tekercsadat nagyobb mint nulla: "+thicknessHeader.numberOfThicknessData);

                if (tcp.receiveTelegram.length >= thicknessHeader.size + (thicknessHeader.numberOfThicknessData) * 8) {

                    /*
                     * Nem az első üzenetrész érkezet. Az adatokat hozzáadjuk az
                     * adatgyűjtőnkhöz
                     */
                    byte[] tempByte = Arrays.copyOfRange(tcp.receiveTelegram, thicknessHeader.size, thicknessHeader.size + (thicknessHeader.numberOfThicknessData) * 8);;
                    collectedReceiveTelegram = ByteArrayConcat.concat(collectedReceiveTelegram, tempByte);
                    //System.out.println("!!!!! Adat összefűzése ...    Az adat hossza: "+collectedReceiveTelegram.length);
                } else {
                    System.out.println(new Date().toString() + " "
                            + tcp.setup.PLANTNAME + "hiba az adatok számában: numberOfThicknessData=" + thicknessHeader.numberOfThicknessData);
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)" + tcp.setup.PLANTNAME + "hiba az adatok számában: numberOfThicknessData=" + thicknessHeader.numberOfThicknessData);

                }

                // tD.addAll(thicknessDataCollect(tcp, thicknessHeader));
            }

            if (thicknessHeader.endOfCoil == 1) {
                // System.out.println("!!!!! Tekercs vége jel jött");
                /**
                 * ####################################### Adatok letárolása
                 * ########################################
                 */

                hhqcs.HHQCS.sql.thicknessRecord(thicknessHeader, Compressor.compress(collectedReceiveTelegram), tcp.setup);
                messageId = 0;
                collectedReceiveTelegram = null;
            } else {
                //System.out.println("!!!!! Nem jött tekercs vége jel !!!!!");
                messageId = thicknessHeader.sorSzam;
            }
            previousThicknessHeader = thicknessHeader;

        }

    }

    private ArrayList<ThicknessData> thicknessDataCollect(TCPConnectionServer tcp, ThicknessHeader thicknessHeader) {
        ArrayList<ThicknessData> thicknessData = new ArrayList();
        int position = thicknessHeader.size;
        for (int i = 0; i < thicknessHeader.numberOfThicknessData; i++) {
            try {
                thicknessData.add(thicknessDataProcess(position, tcp));
            } catch (Exception ex) {
                System.err.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " Hiba az üzenet feldolgozás közben: (position = " + position + " : )");
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error) Hiba az üzenet feldolgozás közben: (position = " + position + " : )", ex);
            }
            position += 8;
        }
        return thicknessData;
    }

    private ThicknessData thicknessDataProcess(int position, TCPConnectionServer tcp) {
        ThicknessData thicknessData = new ThicknessData();
        /**
         * Az aktuális hossz [mm]
         */
        thicknessData.actualLength = ByteBuffer.wrap(tcp.receiveTelegram, position, 4).getInt();
        /**
         * Az aktuális vastagság [um]
         */
        thicknessData.actualThickness = ByteBuffer.wrap(tcp.receiveTelegram, position + 4, 2).getShort();

        /**
         * A vastagság eltérése
         */
        thicknessData.diffThickness = ByteBuffer.wrap(tcp.receiveTelegram, position + 6, 2).getShort();

        return thicknessData;
    }

    private ThicknessHeader thicknessHeaderProcess(TCPConnectionServer tcp) {
        /* thickness thHeader */
        ThicknessHeader thHeader = new ThicknessHeader();
        try {
            /* telegram azonositó */
            thHeader.sorSzam = ByteBuffer.wrap(tcp.receiveTelegram, 0, 2).getShort() & 0xffff;

            /* tekercs generált sorszám */
            thHeader.CoilGeneratedId = ByteBuffer.wrap(tcp.receiveTelegram, 2, 2).getShort() & 0xffff;

            /* -------------------------- Tekercsszám -----------------------------------------------------------*/
            byte[] coilID_ = new byte[16];
            System.arraycopy(tcp.receiveTelegram, 4, coilID_, 0, 16);
            /**
             * space karakteret eltávolítása
             */
            for (int i = 0; i < coilID_.length; i++) {
                if (coilID_[i] < 48 || coilID_[i] > 57) {
                    coilID_[i] = 48;
                }
            }
            thHeader.coilID = new String(coilID_, "UTF-8").trim();

            /* ----------------------------Tekercsszám ------------------------------------------------------------*/
            /**
             * Az adatok száma.
             */
            thHeader.setupThickness = ByteBuffer.wrap(tcp.receiveTelegram, 20, 2).getShort() & 0xffff;

            /**
             * Az adatok száma.
             */
            thHeader.positivThicknessTolerance = ByteBuffer.wrap(tcp.receiveTelegram, 22, 2).getShort() & 0xffff;

            thHeader.negativThicknessTolerance = ByteBuffer.wrap(tcp.receiveTelegram, 24, 2).getShort() & 0xffff;

            thHeader.numberOfThicknessData = ByteBuffer.wrap(tcp.receiveTelegram, 26, 2).getShort() & 0xffff;

            thHeader.endOfCoil = ByteBuffer.wrap(tcp.receiveTelegram, 28, 2).getShort() & 0xffff;

        } catch (Exception ex) {

            System.out.println("Hiba a thicknessHeader feldolgozásánál:");
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)Hiba a coilHeader feldolgozásánál:", ex);
        }
        return thHeader;
    }
}
