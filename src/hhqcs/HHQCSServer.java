/*
 *Kikészitői minősitő program 1 db berendezéshez
 * - Georghasító
 * - Húzvaegyengető
 * - 1550-es hasító
 */
package hhqcs;

import hhqcs.centralograf.CentalografMessage;
import hhqcs.centralograf.UDPConnectionServer;
import hhqcs.net.tcp.TCPConnectionServer;
import hhqcs.setup.Setup;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
     * UDP kapcsolat a centralograf terminállal.
     */
    public UDPConnectionServer udpCentralograf;

    /**
     * Centralográfnak küldött üzenet.
     */
    public CentalografMessage centralografMessage = new CentalografMessage();

    /**
     * @param aSetup
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
            HHQCS.debug.printDebugMsg(setup.PLANTNAME, HHQCSServer.class.getName(),
                    "Hiba történt a tcpConnectionServer létrehozása közben :", ex);
        } catch (IOException ex) {
            /* Hiba Kiirása */
            System.err.println(new Date().toString() + " " + this.getClass() + " " + ex);
            ex.printStackTrace(System.err);
            HHQCS.debug.printDebugMsg(setup.PLANTNAME, HHQCSServer.class.getName(),
                    ": Hiba történt a tcpConnectionServer létrehozása közben", ex);
        }
    }
}
