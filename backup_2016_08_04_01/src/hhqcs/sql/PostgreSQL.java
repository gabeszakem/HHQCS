/*
 * PostgreSQL lekérdezések
 */
package hhqcs.sql;

/**
 *
 * @author Gabesz
 */
import static hhqcs.HHQCS.debug;
import hhqcs.data.CoilHeader;
import hhqcs.setup.Setup;
import hhqcs.thickness.ThicknessHeader;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Calendar;
import javax.swing.JOptionPane;
import java.util.Date;

/**
 *
 * @author gkovacs02
 */
public class PostgreSQL {

    @SuppressWarnings("FieldMayBeFinal")
    private String url;
    @SuppressWarnings("FieldMayBeFinal")
    private String user;
    @SuppressWarnings("FieldMayBeFinal")
    private String password;
    
    private static int THICKNESSSDATALENGTH=8;

    /**
     *
     */
    public PostgreSQL() {
        //this.url = "jdbc:postgresql://localhost:5432/hhqcs";
        this.url = "jdbc:postgresql://10.3.10.203:5432/hhqcs";
        this.user = "hhqcssrv";
        this.password = "hhqcssrv";
    }

    /**
     *
     * @param machineId berendezés azonosító
     * @return sapBerAzon String
     */
    public String minadat_ID(int machineId) {
        /*
         * Adatbázisból az id alapján lekérdezzük a berendezés nevét
         */
        /*
         * Berendezés neve
         */
        String machineName = null;
        Connection con = null;
        Statement st = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT megnevezes FROM tadat_berendezesek WHERE id = " + machineId);
            while (rs.next()) {
                machineName = rs.getString(1);
            }
        } catch (SQLException ex) {
            /*
             * Hiba esetén a hibát kiírjuk
             */
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "Kivétel történt az SQL adatbázis kezelése közben:\n" + ex, "Hiba", JOptionPane.ERROR_MESSAGE);
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)minadat_ID(" + machineId + ") Hiba történt az adatbázis kapcsolódása közben", ex);
        } finally {
            closeConnection(con, st);
        }
        return machineName;
    }
    
      /**
     *
     * @param machineId berendezés azonosító
     * @return sapBerAzon String
     */
    public String minadat_SAP_ID(int machineId) {
        /*
         * Adatbázisból az id alapján lekérdezzük a berendezés nevét
         */
        /*
         * Berendezés neve
         */
        String sapBerAzon = null;
        Connection con = null;
        Statement st = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT sap_ber_azon FROM tadat_berendezesek WHERE id = " + machineId);
            while (rs.next()) {
                sapBerAzon = rs.getString(1);
            }
        } catch (SQLException ex) {
            /*
             * Hiba esetén a hibát kiírjuk
             */
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "Kivétel történt az SQL adatbázis kezelése közben:\n" + ex, "Hiba", JOptionPane.ERROR_MESSAGE);
            debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)minadat_ID(" + machineId + ") Hiba történt az adatbázis kapcsolódása közben", ex);
        } finally {
            closeConnection(con, st);
        }
        return sapBerAzon;
    }
    

    /**
     *
     * @param ch CoilHeader
     * @param record byte[]
     * @param setup Setup
     */
    public void record(CoilHeader ch, byte[] record, Setup setup) {
        /*
         * Az adatok letárolása
         */
        Connection con = null;
        PreparedStatement st = null;
        String coilIdString = "-1";

        try {
            con = DriverManager.getConnection(url, user, password);
            /*
             * PostgreSQL lekérdezés összeállítása
             */
            String query = "INSERT INTO " + setup.PLANTTABLENAME + " (tekercsszam, idobelyeg, tekercshossz, adat, generalt_tekercsszam )VALUES (?,?,?,?,?)";
            st = con.prepareStatement(query);

            if (ch.coilID != null) {
                coilIdString = ch.coilID;
            } else {
                coilIdString = "-1";
            }
            st.setString(1, coilIdString);
            st.setTimestamp(2, ch.timeStamp);
            st.setInt(3, ch.coilLength);
            st.setBinaryStream(4, new ByteArrayInputStream(record), record.length);
            st.setInt(5, ch.telegramId);
            st.executeUpdate();
            String log = "INSERT INTO " + setup.PLANTTABLENAME + " (tekercsszam, idobelyeg, tekercshossz, adat, generalt_tekercsszam)VALUES ("
                    + coilIdString + "," + ch.timeStamp + "," + ch.coilLength + ",BLOB," + ch.telegramId + ")";
            System.out.println(setup.PLANTNAME + " - " + log);
            debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), log);
        } catch (SQLException ex) {
            System.err.println(ex);
            debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "INSERT INTO " + setup.PLANTTABLENAME + " (tekercsszam, idobelyeg, tekercshossz, adat, generalt_tekercsszam)VALUES ("
                    + coilIdString + "," + ch.timeStamp + "," + ch.coilLength +  ",BLOB," + ch.telegramId , ex);
        } finally {
            closeConnection(con, st);
        }

    }

    @SuppressWarnings("null")
    public void thicknessRecord(ThicknessHeader thicknessHeader, byte[] record, Setup setup) {

        Connection con = null;
        PreparedStatement st = null;
        @SuppressWarnings("UnusedAssignment")
        String coilIdString = "-1";
        int returning = 0;
        try {
            if (record != null && record.length >=400) {
                String query;
                String log;
                try {
                    con = DriverManager.getConnection(url, user, password);

                    query = "INSERT INTO " + setup.THICKNESSBLOBTABLENAME + " (adat)VALUES (?) RETURNING id";
                    st = con.prepareStatement(query);
                    
                    st.setBinaryStream(1, new ByteArrayInputStream(record), record.length);

                    ResultSet rs = st.executeQuery();
                    while (rs.next()) {
                        returning = rs.getInt(1);
                    }
                    log = "INSERT INTO " + setup.THICKNESSBLOBTABLENAME + " (adat)VALUES(BLOB) RETURNING id = " + returning;
                    System.out.println(new Date().toString() + " " + setup.PLANTNAME + " - " + log);
                    debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), log);

                } catch (Exception ex) {
                    System.err.println(ex);
                    debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "Hiba a blob fájl írásánál (thicknessRecord)", ex);
                }

                /*
                 * PostgreSQL lekérdezés összeállítása
                 */
                query = "INSERT INTO " + setup.THICKNESSTABLENAME + " (tekercsszam, idobelyeg, nevleges_vastagsag,pozitiv_tures, negativ_tures, generalt_tekercsszam, blob_id)VALUES (?,?,?,?,?,?,?)";
                st = con.prepareStatement(query);

                if (thicknessHeader.coilID != null) {
                    coilIdString = thicknessHeader.coilID;
                } else {
                    coilIdString = "-1";
                }
                st.setString(1, coilIdString);

                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(0);
                calendar.setTime(date);
                Timestamp timeStamp = new Timestamp(calendar.getTimeInMillis());
                st.setTimestamp(2, timeStamp);

                st.setInt(3, thicknessHeader.setupThickness);
                st.setInt(4, thicknessHeader.positivThicknessTolerance);
                st.setInt(5, thicknessHeader.negativThicknessTolerance);
                st.setInt(6, thicknessHeader.CoilGeneratedId);
                st.setInt(7, returning);

                st.executeUpdate();

                log = "INSERT INTO " + setup.THICKNESSTABLENAME + " (tekercsszam, idobelyeg, nevleges_vastagsag,pozitiv_tures, negativ_tures, generalt_tekercsszam, blob_id)VALUES ("
                        + coilIdString + "," + timeStamp + "," + thicknessHeader.setupThickness + "," + thicknessHeader.positivThicknessTolerance + ","
                        + thicknessHeader.negativThicknessTolerance + "," + thicknessHeader.CoilGeneratedId + "," + returning + ")";
                System.out.println(new Date().toString() + " " + setup.PLANTNAME + " - " + log);
                debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), log);
            } else {
                System.out.println(new Date().toString() + " " + setup.PLANTNAME + " - " + "Nem érkezett elég üzenet a blob-ba");
                debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "(error)  Nem érkezett elég üzenet a blob-ba");
            }
        } catch (SQLException ex) {
            System.err.println(ex);
            debug.printDebugMsg(setup.PLANTNAME, this.getClass().getCanonicalName(), "(error) Hiba az sql írásánál (thicknessRecord)", ex);
        } finally {
            closeConnection(con, st);
        }
    }

    /**
     * #######################################################################################################################################
     * #######################################################################################################################################
     * #######################################################################################################################################
     */
    
    
    
    /*
     * Kapcsolat lezárása
     */
    private void closeConnection(Connection con, Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
    }

    /*
     * Kapcsolat lezárása
     */
    private void closeConnection(Connection con, PreparedStatement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                debug.printDebugMsg(null, this.getClass().getCanonicalName(), "(error)Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
    }

}
