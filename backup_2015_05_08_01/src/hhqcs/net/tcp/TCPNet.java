/*
 * TCP üzenetek küldése, fogadása
 * 
 */
package hhqcs.net.tcp;

import static hhqcs.HHQCS.debug;
import hhqcs.HHQCSServer;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    public boolean restart = false;
    public long lastMessageTime = System.currentTimeMillis();
    public long CLIENTRESTARTTIME = 3600000;
    private HHQCSServer server;

    /**
     * @param port port
     * @param ipAddress PLC ipcíme
     * @param server
     * @throws IOException
     */
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public TCPNet(int port, InetAddress ipAddress, HHQCSServer server) throws IOException {

        initialisation(port, ipAddress, server);
    }
    
    public TCPNet(int port, InetAddress ipAddress, long clientRestartTime,HHQCSServer server) throws IOException {
        this.CLIENTRESTARTTIME = clientRestartTime;

        initialisation(port, ipAddress,server);
    }


    private void initialisation(int port1, InetAddress ipAddress1, HHQCSServer server1) throws SocketException, IOException {
        /*
         * Szerver socket létrehozása a port porton
         */
        this.server =server1;
        this.port = port1;
        this.ipAddress = ipAddress1;
        serverSocket = new ServerSocket(port1);
        //System.out.println(new Date().toString() + " TCP szerver létrehozása a " + port1 + " porton...");
        debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(conn)TCP szerver létrehozása a " + port1 + " porton...");
        /*
         * Szerver újrainditás utáni Bind error elkerülése miatt
         */
        serverSocket.setReuseAddress(true);
        //System.out.println(new Date().toString() + " TCP szerver figyel a " + port1 + " porton...");
        restart = false;
        serverRestart.start();
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
                //System.out.println(log);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), log);
            }

           // System.out.println(new Date().toString() + " " + "TCP szerver kliens kapcsolódásra vár a " + this.port + " porton...");
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(conn)TCP szerver kliens kapcsolódásra vár a " + this.port + " porton...");

            /*
             * Kliens socket létrehozása
             */
            this.clientSocket = serverSocket.accept();
            /*
             * Keepalive tiltása
             */
            this.clientSocket.setKeepAlive(false);
            String log = new Date().toString() + " Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolódott a " + this.port + " porton.";
            //System.out.println(log);
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(conn)"+log);
            restart = false;
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
                CharBuffer cbuf = CharBuffer.allocate(4096);
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
                System.out.println(new Date().toString() + " " + log);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(conn)"+log);
                log = new Date().toString() + " Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolat bezárása";
                System.out.println(log);
                receiveOk = false;
                clientSocket.close();
            }
        } catch (Exception ex) {
            //System.err.println(new Date().toString() + " " + this.getClass().getName() + " " + ex.getMessage());
            //ex.printStackTrace(System.err);
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Üzenetek fogadása nem megfelelő", ex);
            clientSocket = null;
            receiveOk = false;
            restart = true;

        }
        if (receiveOk) {
            lastMessageTime = System.currentTimeMillis();
            return receiveTelegram;
        } else {
            System.err.println(new Date().toString() + " " + ("Receive message = null a " + this.port + " porton"));
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Receive message = null a " + this.port + " porton");
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
                //System.out.println(new Date().toString() + " " + log);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(conn)"+log);
            }
            /*
             * kliens socket létrehozása
             */
            this.clientSocket = serverSocket.accept();
           // System.out.println(new Date().toString() + " " + "Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolódott a " + this.port + " porton.");
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(conn)Kliens (" + clientSocket.getRemoteSocketAddress() + ") kapcsolódott a " + this.port + " porton.");
            lastMessageTime = System.currentTimeMillis();
        } else {
            try {
                DataOutputStream outToClient = new DataOutputStream(this.clientSocket.getOutputStream());
                String str = new String(sendtelegram, "ISO-8859-1");
                outToClient.writeBytes(str);
                lastMessageTime = System.currentTimeMillis();
            } catch (Exception ex) {
                //System.err.println(new Date().toString() + " " + this.getClass() + " " + ex.getMessage());
                //ex.printStackTrace(System.err);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Üzenetek küldése nem megfelelő", ex);
                this.clientSocket = null;
                restart = true;
            }
        }
    }

    Thread serverRestart = new Thread("Szerver restart port:" + port) {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (this) {
                        if ((System.currentTimeMillis() - lastMessageTime) > CLIENTRESTARTTIME) {
                            restart = true;
                        }

                        if (restart || server.tcpLife.tcp.restart) {
                            try {

                                serverSocket.close();

                            } catch (Exception e) {
                                System.err.println(new Date().toString() + " " + TCPNet.class + " " + e.getMessage());
                                e.printStackTrace(System.err);
                                debug.printDebugMsg(null, TCPNet.class.getCanonicalName(), "(error)Szerver socket bezárása nem sikerült", e);
                            }

                            //System.out.println(new Date().toString() + " TCP szerver restart a " + port + " porton...");
                            debug.printDebugMsg(null, TCPNet.class.getCanonicalName(), "(conn)TCP szerver restart a " + port + " porton...");
                            /*
                             * Szerver socket létrehozása a port porton
                             */
                            serverSocket = new ServerSocket(port);
                            //System.out.println(new Date().toString() + "TCP szerver létrehozása a " + port + " porton...");
                            debug.printDebugMsg(null, TCPNet.class.getCanonicalName(), "(conn)TCP szerver létrehozása a " + port + " porton...");
                            /*
                             * Szerver újrainditás utáni Bind error elkerülése miatt
                             */
                            serverSocket.setReuseAddress(true);

                            //IP Address
                            //System.out.println(new Date().toString() + " TCP szerver figyel a " + port + " porton...");
                            restart = false;
                            lastMessageTime = System.currentTimeMillis();
                            try {
                                clientSocket.close();
                            } catch (Exception ex) {
                                clientSocket = null;
                            }
                            wait(300000);
                        }else{
                            wait(1000);
                        }
                        
                    }
                } catch (Exception ex) {
                    System.out.println(new Date().toString() + " TCP szerver restart nem sikerült a " + port + " porton..." + ex.getMessage());
                    debug.printDebugMsg(null, TCPNet.class.getCanonicalName(), "(error) TCP szerver restart nem sikerült a " + port + " porton...", ex);
                    //System.out.println(new Date().toString() + " Rendszer leállítás");
                    //debug.printDebugMsg(null, TCPNet.class.getCanonicalName(), "(error)Rendszer leállítás");

                    try {
                        if (restart) {
                            serverSocket.close();
                        }
                    } catch (Exception e) {
                        System.err.println(new Date().toString() + " " + TCPNet.class + " " + e.getMessage());
                        e.printStackTrace(System.err);
                        debug.printDebugMsg(null, TCPNet.class.getCanonicalName(), "(error)Szerver socket bezárása nem sikerült", e);
                    }
                }
            }
        }
    };

}
