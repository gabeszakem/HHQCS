/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hhqcs.phpinterface;

import static hhqcs.HHQCS.debug;
import hhqcs.net.tcp.TCPConnectionServer;
import java.io.InputStream;

/**
 *
 * @author gkovacs02
 */
public class PHP {

    public void php() {

    }

    public void call(TCPConnectionServer tcp) {
        try {

            int mid = 0;
            switch (tcp.setup.SAPPLANTNAME) {
                case "HE":
                    mid = 1;
                    break;
                case "GH":
                    mid = 2;
                    break;
                case "NH":
                    mid = 3;
                    break;
            }
            ProcessBuilder pb = new ProcessBuilder("php", "-f", "/var/www/hhqcs/intf/store_sap_matrix.php",
                    "cid=" + tcp.ch.coilID, "mid=" + mid, "sapsys=1", "verbose");
            String message="\"php\", \"-f\", \"/var/www/hhqcs/intf/store_sap_matrix.php\",\n"
                    + "                                \"cid=" + tcp.ch.coilID + ", \"mid=" + mid + "\", \"sapsys=1\"";
            System.out.println(message);
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), message);
            Process p = pb.start();

            String stdOut = getInputAsString(p.getInputStream());
            String stdErr = getInputAsString(p.getErrorStream());

            System.out.println(stdOut);
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), stdOut);
            System.err.println(stdErr);
            //debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)" + stdErr);

          /*  if (stdOut.contains("ORA-12545")) {
                try {
                    p = pb.start();
                    stdOut = getInputAsString(p.getInputStream());
                    stdErr = getInputAsString(p.getErrorStream());
                    System.out.println(stdOut);
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), stdOut);
                    System.err.println(stdErr);
                    //debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)" + stdErr);
                } catch (Exception ex) {
                    debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), stdOut);
                    //debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)" + stdErr);
                }
            }*/
        } catch (Exception ex) {
            debug.printDebugMsg(tcp.setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)",ex);
        }
    }

    private String getInputAsString(InputStream is) {
        try (java.util.Scanner s = new java.util.Scanner(is)) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }

}
