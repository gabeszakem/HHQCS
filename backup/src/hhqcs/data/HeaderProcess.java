/*
 *A telegramm és a tekercs fejrészét dplgozza fel
 */
package hhqcs.data;

import static hhqcs.HHQCS.debug;
import hhqcs.net.tcp.TCPConnectionServer;
import tools.ByteToHexa;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author gkovacs02
 */
public class HeaderProcess {

    /**
     * A telegram fejlécét állítja be
     *
     * @param tcp
     * @tcp TCPConnectionServer
     * @return TelegramHeader
     */
    public TelegramHeader telegramHeader(TCPConnectionServer tcp) {
        /* Telegram header */
        TelegramHeader th = new TelegramHeader();

        /* telegram azonositó */
        th.telegramID = ByteBuffer.wrap(tcp.receiveTelegram, 0, 2).getShort() & 0xffff;

        /* Tekercsszám */
        th.CoilGeneratedId = ByteBuffer.wrap(tcp.receiveTelegram, 2, 2).getShort() & 0xffff;

        /* Üzenet azonosító */
        th.MessageId = ByteBuffer.wrap(tcp.receiveTelegram, 4, 2).getShort() & 0xffff;

        return th;
    }

    /**
     * A tekercs fejlécét állítja be
     *
     * @param tcp TCPConnectionServer
     * @return CoilHeader
     */
    public CoilHeader coilHeader(TCPConnectionServer tcp) {

        CoilHeader ch = new CoilHeader();
        try {
            /* telegram azonosító */
            ch.telegramId = ByteBuffer.wrap(tcp.receiveTelegram, 6, 2).getShort() & 0xffff;
            /* telegram hossza */
            ch.telegramLength = ByteBuffer.wrap(tcp.receiveTelegram, 8, 2).getShort() & 0xffff;
            /*
             * Időbélyeg beállítása. Az Omron PLC az időbélyeget byte cserével menti el. 
             * A számokat fél byte -onként tárolja
             */
            byte[] tmp = new byte[8];
            System.arraycopy(tcp.receiveTelegram, 10, tmp, 0, 8);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(0);
            c.set(2000 + ByteToHexa.byteToHexa(tmp[4]), ByteToHexa.byteToHexa(tmp[5]) - 1,
                    ByteToHexa.byteToHexa(tmp[2]), ByteToHexa.byteToHexa(tmp[3]),
                    ByteToHexa.byteToHexa(tmp[0]), ByteToHexa.byteToHexa(tmp[1]));
            ch.timeStamp = new Timestamp(c.getTimeInMillis());

            /* Tekercsszám */
            byte[] coilID = new byte[16];
            System.arraycopy(tcp.receiveTelegram, 18, coilID, 0, 16);
            /**
             * space karakteret eltávolítása
             */
            for (int i = 0; i < coilID.length; i++) {
                if (coilID[i] < 48 || coilID[i] > 57) {
                    coilID[i] = 48;
                }
            }
            ch.coilID = new String(coilID, "UTF-8").trim();

            /* Tekercshossz */
            ch.coilLength = ByteBuffer.wrap(tcp.receiveTelegram, 34, 2).getShort() & 0xffff;
        } catch (Exception ex) {

            System.out.println("Hiba a coilHeader feldolgozásánál:");
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(),"(error)Hiba a coilHeader feldolgozásánál:", ex);
        }
        return ch;
    }
}
