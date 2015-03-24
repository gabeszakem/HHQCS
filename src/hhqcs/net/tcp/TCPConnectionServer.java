/*
 *Új thread tcp szerver kapcsolathoz
 */
package hhqcs.net.tcp;

import hhqcs.HHQCS;
import hhqcs.HHQCSServer;
import hhqcs.data.CoilHeader;
import hhqcs.data.Data;
import hhqcs.data.DataProcess;
import hhqcs.data.TelegramHeader;
import hhqcs.life.LifeProcess;
import hhqcs.life.LifeSignal;
import hhqcs.setup.Setup;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Gabesz
 */
public class TCPConnectionServer extends Thread {

    @SuppressWarnings("FieldMayBeFinal")
    private TCPNet tcp;
    @SuppressWarnings("FieldMayBeFinal")
    private String serverType;
    /**
     * fogadott üzenet
     */
    public byte[] receiveTelegram;
    /**
     * Életjel
     */
    public LifeSignal ls;
    /**
     * Telegram fejléc
     */
    public TelegramHeader th;
    /**
     * Tekercs fejrész
     */
    public CoilHeader ch;
    /**
     * Rekordok
     */
    public ArrayList<Data> record;
    /**
     * Setup beállítások
     */
    public Setup setup;

    private DataProcess dp;

    private LifeProcess lp;

    public HHQCSServer hhqcsServer;

    /**
     * @param plcPort : port
     * @param ipAddress : PLC IP címe
     * @param serverType Szerver típusa
     * @param hhqcsServer
     * @throws IOException
     */
    public TCPConnectionServer(int plcPort, InetAddress ipAddress, String serverType, HHQCSServer hhqcsServer) throws IOException {
        /*
         * Új TCP szerver indítása
         */
        tcp = new TCPNet(plcPort, ipAddress);
        this.serverType = serverType;
        this.ls = new LifeSignal();
        this.setup = hhqcsServer.setup;
        this.hhqcsServer = hhqcsServer;
        this.dp = new DataProcess();
        this.lp = new LifeProcess();
    }

    /**
     * Thread futása
     */
    @Override
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void run() {
        while (true) {
            try {
                /*
                 * Üzenet érkezett
                 */
                receiveTelegram = tcp.receiveTelegram();
                /*
                 * Életjel vagy adatot kaptunk
                 */
                if (receiveTelegram != null) {
                    if (this.serverType.equals("life")) {
                        /*
                         * Életjel feldolgozása
                         */
                        lp.process(this, hhqcsServer);
                    } else if (this.serverType.equals("data")) {
                        /*
                         * Adatok feldolgozása
                         */
                        dp.process(this);
                    }
                } else {
                    System.err.println(new Date().toString() + " " + this.setup.PLANTNAME + " " + this.getClass() + " receiveTelegram==null ");
                    HHQCS.debug.printDebugMsg(setup.PLANTNAME, TCPConnectionServer.class.getName(),
                            "Hiba történt receiveTelegram==0");
                }
            } catch (Exception ex) {
                /*
                 * Hiba, üzenet kiírása
                 */
                System.err.println(new Date().toString() + " " + this.setup.PLANTNAME + " " + this.getClass() + " " + ex);
                ex.printStackTrace(System.err);
                HHQCS.debug.printDebugMsg(setup.PLANTNAME, TCPConnectionServer.class.getName(),
                        "Hiba történt a tcpConnectionServer telegram fogadása közben", ex);
            }
        }
    }

    /**
     *
     * @param string küldendő üzenet
     */
    public void sendTelegram(String string) {
        try {
            /*
             * Üzenetek küldése
             */
            tcp.sendTelegram(string.getBytes());
        } catch (IOException ex) {
            /*
             * Hiba kiiratása
             */
            System.err.println(new Date().toString() + " " + this.setup.PLANTNAME + " " + this.getClass() + " " + ex);
            ex.printStackTrace(System.err);
            HHQCS.debug.printDebugMsg(setup.PLANTNAME, TCPConnectionServer.class.getName(),
                    "Hiba történt a tcpConnectionServer telegram küldése közben", ex);
        }
    }

    /**
     *
     * @param sendTelegramm küldendő üzenet
     */
    public void sendTelegram(byte[] sendTelegramm) {
        try {
            /*
             * Üzenet küldése
             */
            tcp.sendTelegram(sendTelegramm);
        } catch (IOException ex) {
            /*
             * Hiba kiiratása
             */
            System.err.println(new Date().toString() + " "  + this.setup.PLANTNAME + " "+  this.getClass() + "  Hiba történt a tcpConnectionServer telegram küldése közben " + ex);
            ex.printStackTrace(System.err);
            HHQCS.debug.printDebugMsg(setup.PLANTNAME, TCPConnectionServer.class.getName(),
                    "Hiba történt a tcpConnectionServer telegram küldése közben", ex);
        }
    }
}
