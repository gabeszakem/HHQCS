/*
 * Az életjel feldolgozása
 * 
 */
package hhqcs.life;

import hhqcs.HHQCS;
import static hhqcs.HHQCS.debug;
import hhqcs.HHQCSServer;
import hhqcs.net.tcp.TCPConnectionServer;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Gabesz
 */
public class LifeProcess {

    @SuppressWarnings("FieldMayBeFinal")
    private long lastMessageTime = 0;

    /**
     *
     * @param tcp TCPConnectionServer
     * @param hhqcsServer
     */
    public void process(TCPConnectionServer tcp, HHQCSServer hhqcsServer) {
        /*
         * Az életjel telegramm hossza megfelelő
         */
        if (tcp.receiveTelegram.length == 2 || tcp.receiveTelegram.length == 4) {

            /*
             * Lementjük az üzenet azonosítót az életjel megjelenítéséhez
             */
            tcp.ls.count = ByteBuffer.wrap(tcp.receiveTelegram, 0, 2).getShort() & 0xffff;
            /*
             * A válasz telegramm előállítása
             */
            byte[] st = sendTelegram(tcp);
            /*
             * Telegram küldése
             */
            tcp.sendTelegram(st);
            /**
             * Ha van termelés jelző bitünk is.
             */
            if (tcp.receiveTelegram.length == 4) {
                if ((lastMessageTime - System.currentTimeMillis() > 3000) & (lastMessageTime != 0)) {
                    System.err.println("(" + tcp.setup.PLANTNAME + ") Hiba: az utolsó életjel " + Long.toString(lastMessageTime - System.currentTimeMillis()) + " + idővel ezelött jött.");
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)az utolsó életjel " + Long.toString(lastMessageTime - System.currentTimeMillis()) + " + idővel ezelött jött.");
                }
                hhqcsServer.centralografMessage.plantStatus = ByteBuffer.wrap(tcp.receiveTelegram, 2, 2).getShort();
                if (hhqcsServer.setup.centTerminalMessageEnable) {
                    hhqcsServer.udpCentralograf.sendMessage();
                }
            }
        } else {
            /*
             * Hiba üzenet kiírása
             */
            System.out.println("Telegram hossza nem megfelelő (" + LifeProcess.class.getSimpleName() + ")");
            HHQCS.debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(warning)Telegram hossza nem megfelelő (" + LifeProcess.class.getSimpleName() + ")");
        }
    }

    /*
     * A küldendő üzenet előállítása
     */
    private static byte[] sendTelegram(TCPConnectionServer tcp) {
        /*
         * válaszüzenet : az üzenet azonositó + a dátum visszaküldése
         */
        byte[] sendTelegram = new byte[10];
        Date date = new Date();
        SimpleDateFormat second = new java.text.SimpleDateFormat("ss");
        SimpleDateFormat minute = new java.text.SimpleDateFormat("mm");
        SimpleDateFormat hour = new java.text.SimpleDateFormat("HH");
        SimpleDateFormat day = new java.text.SimpleDateFormat("dd");
        SimpleDateFormat month = new java.text.SimpleDateFormat("MM");
        SimpleDateFormat year = new java.text.SimpleDateFormat("yy");

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        try {
            sendTelegram[0] = tcp.receiveTelegram[0];
            sendTelegram[1] = tcp.receiveTelegram[1];
            /*
             * Az omron plc a dátumot mm,ss,dd,HH,yy,MM formában tárolja
             */
            sendTelegram[2] = hexToBCD(Integer.parseInt(minute.format(date)));
            sendTelegram[3] = hexToBCD(Integer.parseInt(second.format(date)));
            sendTelegram[4] = hexToBCD(Integer.parseInt(day.format(date)));
            sendTelegram[5] = hexToBCD(Integer.parseInt(hour.format(date)));
            sendTelegram[6] = hexToBCD(Integer.parseInt(year.format(date)));
            sendTelegram[7] = hexToBCD(Integer.parseInt(month.format(date)));
            sendTelegram[9] = (byte) (dayOfWeek - 1);
        } catch (Exception ex) {
            /*
             * Hiba kiírása
             */
            System.err.println(ex);
            debug.printDebugMsg(tcp.setup.PLANTNAME, LifeProcess.class.getName(), "(error)Hiba a válasz telegramm összeállítása közben", ex);
        }
        return sendTelegram;
    }

    /**
     * @param hex ascii code
     */
    private static byte hexToBCD(int hex) {
        /*
         * 0-99 -ig átalakitja az asci codot számá.
         */
        int a = hex / 10 * 16;
        int b = hex % 10;
        return (byte) (a + b);
    }
}
