/*
 * PLC-től kapott adatok feldolgozása
 */
package hhqcs.data;

import static hhqcs.HHQCS.debug;
import hhqcs.net.tcp.TCPConnectionServer;
import tools.ByteArrayConcat;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author Gabesz
 */
public class DataProcess {

    /**
     * plc-től kapott rész telegrammok összesége
     */
    private byte[] collectedReceiveTelegram = null;
    /**
     * Az előző üzenet azonositója
     */
    private int messageId = -1;
    /**
     * Az előző üzenet tekercsazonositója
     */
    private int coilGeneratedId = -1;
    /**
     * Telegram hossza
     */
    private static final int TELEGRAMLENGTH = 1030;
    /**
     * Egy adat hossza
     */
    private static final int ONEDATALENGTH = 14;
    /**
     * Részüzenetek száma
     */
    private static final int MESSAGENUMBERS = 8;
    /**
     * Temp változó a telegram darabok fogadásához
     */
    private byte[] tempReceiveTelegramm;

    HeaderProcess hp = new HeaderProcess();

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
                    + tcp.setup.PLANTNAME + " Telegram hossza nem megfelelő (" + tcp.receiveTelegram.length + ") (" + DataProcess.class.getSimpleName() + ")");
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)A telegram hossza nem megfelelő: (" + tcp.receiveTelegram.length + ")");
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
                        + tcp.setup.PLANTNAME + " Telegram helyreállítása sikerült: " + tcp.receiveTelegram.length + " (" + DataProcess.class.getSimpleName() + ")");
                tempReceiveTelegramm = null;
            }
        }
        if (tcp.receiveTelegram.length == TELEGRAMLENGTH) {
            /*
             * Telegram fejrészének másolása
             */
            tcp.th = hp.telegramHeader(tcp);

            /*
             * Ellenőrzése, hogy az üzenetek megfelelő sorban érkeztek -e meg
             */
            if (messageId > 0 && messageId != MESSAGENUMBERS && tcp.th.MessageId == 1) {

                /*
                 * Nem megfelelő sorrendben érkeztek meg az üzenetek, ezért
                 * kiírjuk a hibát
                 */
                System.err.println(new Date().toString() + " "
                        + tcp.setup.PLANTNAME + " Hiba az üzenet azonositónál: (" + messageId + " : " + tcp.th.MessageId + ")");
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)Hiba az üzenet azonositónál: (" + messageId + " : " + tcp.th.MessageId + ")");

                if (collectedReceiveTelegram != null) {

                    /*
                     * Az eldobandó byte[] tartalmát kiirjuk
                     */
                    System.err.println(Arrays.toString(collectedReceiveTelegram));
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)Az eldobandó byte tartalma:", Arrays.toString(collectedReceiveTelegram));
                }
            }

            /*
             * Következő vagy az első üzenet érkezett meg, ha nem akkor hiba van
             */
            if ((((messageId + 1) == tcp.th.MessageId) && (tcp.th.CoilGeneratedId == coilGeneratedId))
                    || (tcp.th.MessageId == 1)) {

                /*
                 * Az első üzenet érkezett meg
                 */
                if (tcp.th.MessageId == 1) {

                    /*
                     * Tekercshez tartozó fejrész mentése
                     */
                    HeaderProcess h = new HeaderProcess();
                    tcp.ch = hp.coilHeader(tcp);
                    /*
                     * Elkezdjük letárolni az adatokat. 1. üzenet |coil header|
                     * Adat1, Adat2...AdatX| Az üzenetek 1024 byte adatból
                     * állnak
                     */
                    collectedReceiveTelegram = Arrays.copyOfRange(tcp.receiveTelegram, tcp.th.size + tcp.ch.size, tcp.receiveTelegram.length);
                } else {
                    /*
                     * Nem az első üzenetrész érkezet. Az adatokat hozzáadjuk az
                     * adatgyűjtőnkhöz
                     */
                    byte[] tempByte = Arrays.copyOfRange(tcp.receiveTelegram, tcp.th.size, tcp.receiveTelegram.length);
                    collectedReceiveTelegram = ByteArrayConcat.concat(collectedReceiveTelegram, tempByte);

                }
                try {
                    System.out.println(new Date().toString() + " "
                            + tcp.setup.PLANTNAME + " " + tcp.ch.coilID + " " + tcp.th.CoilGeneratedId
                            + " tekercshez részüzenet érkezett (" + tcp.th.MessageId + ")");
                   // debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), " " + tcp.ch.coilID + " " + tcp.th.CoilGeneratedId + " tekercshez részüzenet érkezett (" + tcp.th.MessageId + ")");
                } catch (Exception ex) {
                    System.out.println(new Date().toString() + " "
                            + tcp.setup.PLANTNAME + " " + tcp.th.CoilGeneratedId
                            + " tekercshez részüzenet érkezett (" + tcp.th.MessageId + ")");
                  //  debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), " " + tcp.th.CoilGeneratedId + " tekercshez részüzenet érkezett (" + tcp.th.MessageId + ")");
                }
                /*
                 * Az utolsó telegramm érkezett-e meg
                 */
                if (tcp.th.MessageId == MESSAGENUMBERS) {
                    /*
                     * Az összes részüzenet megérkezett, ezért feldolgozzuk
                     *
                     * Az összes üzenetben érkezett adatok hosszának
                     * meghatározása (A telegramLength tartalmazza a tekercs
                     * üzenet fejrészét is)
                     */
                    int dataLength = tcp.ch.telegramLength - tcp.ch.size;
                    /*
                     * Az adatok hosszának 1 adat hosszának a többszörösének
                     * kell lennie!! Ha az adat hossza nem nagyobb mint nulla,
                     * nincs mit lementeni
                     */
                    if (dataLength % ONEDATALENGTH == 0 && dataLength >= 0) {
                        byte[] usedByte;
                        if (dataLength > 0) {
                            /*
                             * A byte tömbökből kivesszük a hasznos byte-okat (A
                             * fejrészben található hossz szerint)
                             */
                            usedByte = Arrays.copyOfRange(collectedReceiveTelegram, 0, dataLength);
                        } else {
                            /*
                             * Nincs Hasznos adatunk, ezért az üres tömböt
                             * fogjuk letárolni
                             */
                            usedByte = new byte[]{};
                        }
                        /*
                         * Az adatokat lementjük adatbázisba
                         */
                        hhqcs.HHQCS.sql.record(tcp.ch, usedByte, tcp.setup);
                    } else {
                        /*
                         * Üzenet hossza nem megfelelő hiba kiírása
                         */
                        System.out.println(new Date().toString() + " "
                                + tcp.setup.PLANTNAME + " Az üzenet hossza nem megfelelő ");
                        debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)Az üzenet hossza nem megfelelő",
                                "datalength (" + dataLength + ") % ONEDATALENGTH (" + ONEDATALENGTH + ")=" + dataLength % ONEDATALENGTH);
                    }
                }
                /*
                 * Tekercsszámhoz tartozó azonosító lementése
                 */
                coilGeneratedId = tcp.th.CoilGeneratedId;

                /*
                 * Üzenet azonositó lementése
                 */
                messageId = tcp.th.MessageId;

                /*
                 * Az üzenetet rendben megkaptuk, ezért visszaküldjük az üzenet
                 * azonosítót
                 */
                tcp.sendTelegram(Arrays.copyOfRange(tcp.receiveTelegram, 0, 2));

            } else {
                /*
                 * Hibaüzenet kiírása
                 */
                System.out.println(new Date().toString() + " ( " + tcp.setup.PLANTNAME + " ) " + "Telegram üzenetazonosítója, vagy a tekercs azomnosítója nem megfelelő (" + messageId + " : " + tcp.th.MessageId + ")("
                        + DataProcess.class.getSimpleName() + ")");
                debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)Telegram üzenetazonosítója, vagy a tekercs azomnosítója nem megfelelő: "
                        + "(" + messageId + " : " + tcp.th.MessageId + ")");
            }
        }
    }
}
