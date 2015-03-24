/*
 * Az elkapott kivétel PrintStackTrace -el nyert hibaüzenetet alakítja 
 * String típusúvá
 */
package hhqcs.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Gabesz
 */
public class PrintStackTraceToString {

    /**
     *
     * @param e
     * @return String
     */
    public static String printStackTraceToString(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return e.getMessage() + "\n" + ex.getMessage();
            
        }
    }
}
