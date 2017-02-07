/*
 *Kikészitői minősitő program 1 db berendezéshez
 * - Georghasító
 * - Húzvaegyengető
 * - 1550-es hasító
 */
package hhqcs;

import static hhqcs.HHQCS.debug;
import hhqcs.centralograf.CentalografMessage;
import hhqcs.centralograf.UDPConnectionServer;
import hhqcs.net.tcp.TCPConnectionServer;
import hhqcs.phpinterface.PHP;
import hhqcs.setup.Setup;
import hhqcs.setupTelegram.SapR3SetupData;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 *
 * @author Gabesz
 *
 */
public class HHQCSServer {

    /**
     * Beállitási adatok
     */
    public Setup setup;

    /**
     * TCP szerver az adatok fogadásához
     */
    public TCPConnectionServer tcpData;

    /**
     * TCP szerver az életjelhez
     */
    public TCPConnectionServer tcpLife;

    /**
     * TCP szerver a vastagsággi adatok fogadásához
     */
    public TCPConnectionServer tcpThickness;

    /**
     * TCP szerver a vastagsággi adatok fogadásához
     */
    public TCPConnectionServer tcpSetup;

    /**
     * UDP kapcsolat a centralograf terminállal.
     */
    public UDPConnectionServer udpCentralograf;

    /**
     * Centralográfnak küldött üzenet.
     */
    public CentalografMessage centralografMessage = new CentalografMessage();

    /**
     *
     */
    public SapR3SetupData sapR3SetupData = new SapR3SetupData();


    /*
     *sentCoilIdentification
     */
    public String sentCoilIdentification = "";

    public String sentGuId = "";

    public PHP php = new PHP();

    public int count = 0;

    /**
     * @param aSetup Beállítási adatok
     */
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public HHQCSServer(Setup aSetup) {
        this.setup = aSetup;
        try {

            /**
             * Az Adat kommunikációhoz használt port
             */
            int intPortData = Integer.parseInt(setup.PORTDATA);

            /**
             * Az életjel kommunikációhoz használt port
             */
            int intPortLife = Integer.parseInt(setup.PORTLIFE);

            /**
             * A PLC IP cime
             */
            InetAddress ipAddress = InetAddress.getByName(setup.IPADDRESS);

            /**
             * TCP szerver létrehozása az adatkapcsolathoz
             */
            tcpData = new TCPConnectionServer(intPortData, ipAddress, "data", this);
            tcpData.start();
            tcpData.setName("tcpServer " + setup.PLANTNAME + ":data");
            HHQCS.threads.add(tcpData);

            /**
             * TCP szerver létrehozása az életjel kapcsolathoz
             */
            tcpLife = new TCPConnectionServer(intPortLife, ipAddress, "life", this);
            tcpLife.start();
            tcpLife.setName("tcpServer " + setup.PLANTNAME + ":life");
            HHQCS.threads.add(tcpLife);

            //Beállítási adatok
            if (this.setup.setupDataMessageEnable) {
                int setupPort = setup.setupDataPort;
                tcpSetup = new TCPConnectionServer(setupPort, ipAddress, "setup", this);
                tcpSetup.start();
                tcpSetup.setName("tcpServer " + setup.PLANTNAME + ":setup");
                HHQCS.threads.add(tcpSetup);

                Thread timer = new Thread("Timer 10 sec " + setup.PLANTNAME) {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                synchronized (this) {
                                    wait(5000);
                                    sapData();
                                }
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
                    }
                };
                timer.start();


            }

            //Vastagság
            if (this.setup.thicknessMessageEnable) {
                /**
                 * A vastagsági adat kommunikációhoz használt port
                 */
                int intPortThickness = Integer.parseInt(setup.PORTTHICKNESS);
                tcpThickness = new TCPConnectionServer(intPortThickness, ipAddress, "thickness", this);
                tcpThickness.start();
                tcpThickness.setName("tcpServer " + setup.PLANTNAME + ":thickness");
            }

            /**
             * UDP szerver létrehozása . (Sor termel jel küldése a centralográf
             * terminálnak).
             */
            if (setup.centTerminalMessageEnable) {
                int centralografPort = setup.centTerminalPort;
                int bufferSize = setup.centTerminalbuffersize;
                InetAddress centralografIPAddress = InetAddress.getByName(setup.centTerminalIpAddress);
                udpCentralograf = new UDPConnectionServer(this, centralografPort, bufferSize, centralografIPAddress);
                udpCentralograf.start();
                udpCentralograf.setName("udpCentralograf " + setup.PLANTNAME);
            }

        } catch (UnknownHostException ex) {
            /* Hiba Kiirása */
            System.err.println(new Date().toString() + " " + this.getClass() + " " + ex);
            ex.printStackTrace(System.err);
            debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)Hiba történt a tcpConnectionServer létrehozása közben :", ex);
        } catch (IOException ex) {
            /* Hiba Kiirása */
            System.err.println(new Date().toString() + " " + this.getClass() + " " + ex);
            ex.printStackTrace(System.err);
            debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)Hiba történt a tcpConnectionServer létrehozása közben", ex);
        }
    }

    private void sapData() {
        try {

            // count = HHQCS.oraclesql.count();
            // System.out.println("count: "+count);
            // if (count != lastcount & count!=-1) {
            // lastcount=count;
            count = count + 1;
            if (count > 9999) {
                count = 0;
            }
            sapR3SetupData = HHQCS.oraclesql.getLastData(setup.SAPPLANTNAME,sapR3SetupData);
            // System.out.println("sapR3SetupData: "+sapR3SetupData.berendezesAzonosito+ " : "+sapR3SetupData.guid);
            if (!sapR3SetupData.sapAlapanyagAzonosito.equals(sentCoilIdentification)) {
                System.out.println(new Date().toString() + " " + setup.PLANTNAME + " - " + "Új tekercs érkezett az SAP-tól " + sapR3SetupData.sapAlapanyagAzonosito);
                debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "Új tekercs érkezett az SAP-tól " + sapR3SetupData.sapAlapanyagAzonosito);
                byte[] sendTelegram = new byte[22];
                ByteBuffer.wrap(sendTelegram, 0, 10).put(sapR3SetupData.sapAlapanyagAzonosito.getBytes());
                ByteBuffer.wrap(sendTelegram, 10, 2).putShort(sapR3SetupData.alapanyagVastagsag);
                ByteBuffer.wrap(sendTelegram, 12, 2).putShort(sapR3SetupData.szerzodottVastagsagTuresMinimum);
                ByteBuffer.wrap(sendTelegram, 14, 2).putShort(sapR3SetupData.szerzodottVastagsagTuresPlusz);
                ByteBuffer.wrap(sendTelegram, 16, 2).putShort(sapR3SetupData.alapanyagSzelesseg);
                ByteBuffer.wrap(sendTelegram, 18, 2).putShort(sapR3SetupData.szerzodottSzelesseg);
                ByteBuffer.wrap(sendTelegram, 20, 2).putShort(sapR3SetupData.alapanyagSuly);
                
                    tcpSetup.sendTelegram(sendTelegram);
                

                //}
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
