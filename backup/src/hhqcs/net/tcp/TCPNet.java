/*
 * TCP üzenetek küldése, fogadása
 * 
 */
package hhqcs.net.tcp;

import static hhqcs.HHQCS.debug;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Date;

/**
 *
 * @author Gabesz
 */
public class TCPNet {

    @SuppressWarnings("FieldMayBeFinal")
    private ServerSocket serverSocket;
    private byte[] receiveTelegram;
    @SuppressWarnings("FieldMayBeFinal")
    private InetAddress ipAddress;
    @SuppressWarnings("FieldMayBeFinal")
    private int port;
    private Socket clientSocket = null;

    /**
     * @param port port
     * @param ipAddress PLC ipcíme
     * @throws IOException
     */
    public TCPNet(int port, InetAddress ipAddress) throws IOException {
        /*
         * Szerver socket létrehozása a port porton
         */
        serverSocket = new ServerSocket(port);
        System.out.println(new Date().toString() + " TCP szerver létrehozása a " + port + " porton...");
        debug.printDebugMsg(null, this.getClass().getCanonicalName(), "TCP szerver létrehozása a " + port + " porton...");
        /*
         * Szerver újrainditás utáni Bind error elkerülése miatt
         */
        serverSocket.setReuseAddress(true);

        this.port = port;                          //port szám
        this.ipAddress = ipAddress;              //IP Address
        System.out.println(new Date().toString() + " TCP szerver figyel a " + port + " porton...");
    }

    /**
     *
     * @return receiveTelegram
     * @throws IOException
     */
    public byte[] receiveTelegram() throws IOException {
        /*
         * Ha a klienssel nincs felépítve a socket
         */
        if (clientSocket == null || clientSocket.isClosed()) {
            if (clientSocket != null) {
                String log = new Date().toString() + " Kliens (" + clientSocket.getRemoteSocketAddress() + ") lekapcsolódott a " + this.port + " porton.";
                System.out.println(log);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), log);
            }

            System.out.println("TCP szerver kliens kapcsolódásra vár");
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "TCP szerver kliens kapcsolódásra vár");

            /*
             * Kliens socket létrehozása
             */
            this.clientSocket = serverSocket.accept();
            /*
             * Keepalive tiltása
             */
            this.clientSocket.setKeepAlive(false);
            String log = new Date().toString() + " Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolódott a " + this.port + " porton.";
            System.out.println(log);
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), log);
        }
        boolean receiveOk = false;
        try {
            /**
             * !!!! A "serverSocket.receive(receivePacket)" -nek meg kell
             * előznie a "receivePacket.getAddress()" -t , mert különben null
             * pointer exception kivétel történik
             */
            /*
             * Ellenőrizzük hogy jó ip címről érkezet az üzenet
             */

            if (clientSocket.getInetAddress().equals(ipAddress)) {

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "ISO-8859-1"));
                CharBuffer cbuf = CharBuffer.allocate(2048);
                int bufsize = inFromClient.read(cbuf);

                String retString = "";
                if (bufsize == -1) {
                    this.clientSocket.close();
                } else {

                    for (int i = 0; i < bufsize; i++) {

                        retString = retString + cbuf.get(i);
                    }/*
                     * if (bufsize != 2) { System.out.println(bufsize + "B
                     * mennyiségű adat érkezett"); }
                     */

                    // Adatok bemásolása a byte bufferbe
                    receiveTelegram = retString.getBytes("ISO-8859-1");
                    receiveOk = true;
                }

            } else {
                //Nem jó helyről érkeztek az adatok
                String log = "(" + clientSocket.getRemoteSocketAddress() + ") ip cimről jött adat, de ilyen ip cím nincs a "
                        + "listában";
                System.out.println(log);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), log);
                log = new Date().toString() + " Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolat bezárása";
                System.out.println(log);
                receiveOk = false;
            }
        } catch (Exception ex) {
            System.err.println(new Date().toString() + " " + this.getClass().getName() + " " + ex.getMessage());
            ex.printStackTrace(System.err);
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Üzenetek fogadása nem megfelelő", ex);
            clientSocket=null;
        }
        if (receiveOk) {
            return receiveTelegram;
        } else {
            return null;
        }
    }

    /**
     *
     * @param sendtelegram
     * @throws IOException
     */
    public void sendTelegram(byte[] sendtelegram) throws IOException {
        /*
         * kliens socket nyitva van ?
         */
        if (clientSocket == null || clientSocket.isClosed()) {
            if (clientSocket != null) {
                String log = "Kliens (" + clientSocket.getRemoteSocketAddress() + ") lekapcsolódott a " + this.port + " porton.";
                System.out.println(new Date().toString() + " " + log);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), log);
            }
            /*
             * kliens socket létrehozása
             */
            this.clientSocket = serverSocket.accept();
            System.out.println("Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolódott a " + this.port + " porton.");
        } else {
            try {
                DataOutputStream outToClient = new DataOutputStream(this.clientSocket.getOutputStream());
                String str = new String(sendtelegram, "ISO-8859-1");
                outToClient.writeBytes(str);
            } catch (Exception ex) {
                System.err.println(new Date().toString() + " " + this.getClass() + " " + ex.getMessage());
                ex.printStackTrace(System.err);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Üzenetek küldése nem megfelelő", ex);
                this.clientSocket.close();
            }
        }
    }
}
