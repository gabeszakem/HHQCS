/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.centralograf;

import hhqcs.HHQCSServer;
import java.io.IOException;
import java.net.InetAddress;
import tools.FillDataToBuffer;

/**
 *
 * @author gabesz
 */
public class UDPConnectionServer extends Thread {

    @SuppressWarnings("FieldMayBeFinal")
    private UDPNet udp;
    @SuppressWarnings("FieldMayBeFinal")
    private Object object;
    @SuppressWarnings("FieldMayBeFinal")
    private boolean rw;
    @SuppressWarnings("FieldMayBeFinal")
    private int bufferSize;

    /**
     *
     */
    public HHQCSServer hhqcsServer;
    
    @SuppressWarnings("FieldMayBeFinal")
    private FillDataToBuffer fdtb;
    /**
     *
     * @param hhqcsServer HHQCSServer
     * @param plcPort port száma
     * @param bufferSize buffer méret
     * @param ipAddress ip azonaosító
     * @throws IOException IOException
     */
    public UDPConnectionServer(HHQCSServer hhqcsServer, int plcPort, int bufferSize, InetAddress ipAddress) throws IOException {
        udp = new UDPNet(plcPort, bufferSize, ipAddress);  //Új UDP szerver inditása
        this.hhqcsServer = hhqcsServer;
        this.bufferSize = bufferSize;
        this.fdtb=new FillDataToBuffer();
    }
   
    /**
     *
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void sendMessage() {
        try {
            udp.sendTelegram(fdtb.load(hhqcsServer.centralografMessage, bufferSize));
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace(System.err);
        }
    }
}
