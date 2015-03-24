/*
 * SQL lekérdezések
 */
package hhqcs.sql;

/**
 *
 * @author Gabesz
 */
import hhqcs.HHQCS;
import hhqcs.data.CoilHeader;
import hhqcs.setup.Setup;
import java.io.ByteArrayInputStream;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author gkovacs02
 */
public class SQL {

    @SuppressWarnings("FieldMayBeFinal")
    private String url;
    @SuppressWarnings("FieldMayBeFinal")
    private String user;
    @SuppressWarnings("FieldMayBeFinal")
    private String password;
    private String coilIdString;

    /**
     *
     */
    public SQL() {
        this.url = "jdbc:postgresql://allasido.dunaferr.hu:5432/hhqcs";
        this.user = "hhqcssrv";
        this.password = "hhqcssrv";
    }

    /**
     *
     * @param machineId
     * @return machineName String
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
            HHQCS.debug.printDebugMsg(null, SQL.class.getName(),
                    "minadat_ID(" + machineId + ") Hiba történt az adatbázis kapcsolódása közben", ex);
        } finally {
            closeConnection(con, st);
        }
        return machineName;
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

        try {
            con = DriverManager.getConnection(url, user, password);
            /*
             * SQL lekérdezés összeállítása
             */
            String query = "INSERT INTO " + setup.PLANTTABLENAME + " (tekercsszam, idobelyeg, tekercshossz, adat)VALUES (?,?,?,?)";
            st = con.prepareStatement(query);

            if (ch.coilID != null) {
                this.coilIdString = ch.coilID;
            } else {
                this.coilIdString = "-1";
            }
            st.setString(1, this.coilIdString);
            st.setTimestamp(2, ch.timeStamp);
            st.setInt(3, ch.coilLength);
            st.setBinaryStream(4, new ByteArrayInputStream(record), record.length);
            st.executeUpdate();
            String log = "INSERT INTO " + setup.PLANTTABLENAME + " (tekercsszam, idobelyeg, tekercshossz, adat)VALUES ("
                    + this.coilIdString + "," + ch.timeStamp + "," + ch.coilLength + ",BLOB)";
            System.out.println(setup.PLANTNAME + " - " + log);
            HHQCS.debug.printDebugMsg(setup.PLANTNAME, SQL.class.getName(), log);
        } catch (SQLException ex) {
            System.err.println(ex);
            HHQCS.debug.printDebugMsg(setup.PLANTNAME, SQL.class.getName(), "INSERT INTO " + setup.PLANTTABLENAME + " (tekercsszam, idobelyeg, tekercshossz, adat)VALUES ("
                    + this.coilIdString + "," + ch.timeStamp + "," + ch.coilLength + ",BLOB)", ex);
        } finally {
            closeConnection(con, st);
        }

    }
   
    /*
     * Kapcsolat lezárása
     */
    private void closeConnection(Connection con, Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                HHQCS.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                HHQCS.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
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
                HHQCS.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                HHQCS.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
    }
}
