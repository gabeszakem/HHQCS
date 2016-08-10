/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.centralograf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author gabesz
 */
public class UDPNet {

    DatagramSocket socket;
    byte[] receiveTelegram;
    InetAddress ipAddress;
    private final int port;

    /**
     * 
     * @param port portszám
     * @param buffersize buffer méret
     * @param ipAddress ipcím
     * @throws IOException IOException
     */
    public UDPNet(int port, int buffersize, InetAddress ipAddress) throws IOException {
        socket = new DatagramSocket(); // Az UDP számára Bind -olja a port-ot
        //socket.setReuseAddress(true);     // Bind hiba elkerülése miatt
        receiveTelegram = new byte[buffersize]; //byte tömb a telegram fogadásához
        this.ipAddress = ipAddress;             // A partner IP Címe
        this.port = port;                       //port szám
    }

    /**
     * 
     * @param sendtelegram Küldendő telegram
     * @throws IOException IOException
     */
    public void sendTelegram(byte[] sendtelegram) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(sendtelegram, sendtelegram.length, ipAddress, port);
        socket.send(sendPacket);
    }
}
