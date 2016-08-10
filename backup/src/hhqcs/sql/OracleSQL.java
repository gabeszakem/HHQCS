package hhqcs.sql;

import hhqcs.setupTelegram.SapR3SetupData;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OracleSQL {

    @SuppressWarnings("FieldMayBeFinal")
    private String url;
    @SuppressWarnings("FieldMayBeFinal")
    private String user;
    @SuppressWarnings("FieldMayBeFinal")
    private String password;

    public OracleSQL() {
        //Éles rendszer
        //this.url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=tcp)(HOST=dvprod1vip.dunaferr.hu)(PORT=1527))(ADDRESS=(PROTOCOL=tcp)(HOST=dvprod2vip.dunaferr.hu)(PORT=1527))(LOAD_BALANCE=OFF)(FAILOVER=ON))(CONNECT_DATA =(SID = DVP)(GLOBAL_NAME = DVP.WORLD)))";

        this.url = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=off)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=dvprod1.dunaferr.hu)(PORT=1527))(ADDRESS=(PROTOCOL=TCP)(HOST=dvprod2.dunaferr.hu)(PORT=1527)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = DVP.WORLD)(GLOBAL_NAME=DVP.WORLD)(FAILOVER_MODE=(TYPE=SELECT)(METHOD = BASIC))))";
        //this.url="jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=tcp)(HOST=dvprod1vip)(PORT=1527))(ADDRESS=(PROTOCOL=tcp)(HOST=dvprod2vip)(PORT=1527))(LOAD_BALANCE=OFF)(FAILOVER=ON))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = DVP.WORLD )(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))"; 
//this.url = "jdbc:oracle:thin:@(DESCRIPTION=(ENABLE=BROKEN)(LOAD_BALANCE=off)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=hpsapdvp.dunaferr.hu)(PORT=1527))(ADDRESS=(PROTOCOL=TCP)(HOST=dvprod2.dunaferr.hu)(PORT=1527)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = DVP.WORLD)(GLOBAL_NAME=DVP.WORLD)(FAILOVER_MODE=(TYPE=SELECT)(METHOD = BASIC))))";           
       // this.url = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=off)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=dvprod1.dunaferr.hu)(PORT=1527))(ADDRESS=(PROTOCOL=TCP)(HOST=dvprod2.dunaferr.hu)(PORT=1527)))(CONNECT_DATA=(SERVICE_NAME=DVP.WORLD)))";
        //Teszt rendszer
        // this.url = "jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=off)(ADDRESS_LIST=(ADDRESS=(COMMUNITY = SAP.WORLD)(PROTOCOL=TCP)(HOST=hptest.dunaferr.hu)(PORT=1512)))(CONNECT_DATA=(SID=DVQ)(GLOBAL_NAME = DVQ.WORLD)))";
        this.user = "kmruser";
        this.password = "df567hgh7";
    }

    public void checkConnection() {
        System.out.println("-------- Oracle JDBC Connection Testing ------");
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace(System.err);
            System.out.println(e);
            return;
        }
        System.out.println("Oracle JDBC Driver Registered!");
        Connection connection = null;
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:mkyong", "username",	"password");
            connection = DriverManager.getConnection(this.url, this.user, this.password);

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace(System.err);
            System.out.println(e);
            return;

        } finally {
            if (connection != null) {
                System.out.println("You made it, take control your database now!");
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println("Connection Close Failed!");
                    ex.printStackTrace(System.err);
                }
            } else {
                System.out.println("Failed to make connection!");
            }

        }

    }

    public int count() {
        
        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        int count = -1;
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            connection = DriverManager.getConnection(this.url, this.user, this.password);
            String query = "SELECT COUNT(*) FROM SAPR3.ZHP_SAP_KMR";
            //System.out.println(query);
            st = connection.prepareStatement(query);
            rs = st.executeQuery();
            BigDecimal Thousand = new BigDecimal(1000);

            if (rs.next()) {

                try {
                    count = rs.getInt("COUNT(*)");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    System.out.println(ex);
                }
            }

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                    ex.printStackTrace(System.err);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                    ex.printStackTrace(System.err);
                }
            }
        }
        return count;
    }

    public ArrayList<SapR3SetupData> selectQuery(String query) {

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<SapR3SetupData> setupData = new ArrayList<>();
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            connection = DriverManager.getConnection(this.url, this.user, this.password);
            System.out.println(query);
            st = connection.prepareStatement(query);
            rs = st.executeQuery();
            BigDecimal Thousand = new BigDecimal(1000);

            while (rs.next()) {
                SapR3SetupData sd = new SapR3SetupData();
                try {
                    sd.mandt = rs.getString("MANDT");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.guid = rs.getString("GUID");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.sapAlapanyagAzonosito = rs.getString("CHARG");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.berendezesAzonosito = rs.getString("BER_AZON");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.idobelyeg = rs.getString("INSTIME");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagVastagsag = rs.getBigDecimal("ALAPA_VAST").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottVastagsag = rs.getBigDecimal("SZERZ_VAST").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottVastagsagTuresPlusz = rs.getBigDecimal("SZERZ_VAST_TUR_MAX").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottVastagsagTuresMinimum = rs.getBigDecimal("SZERZ_VAST_TUR_MIN").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagSzelesseg = rs.getShort("ALAPA_SZEL");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottSzelesseg = rs.getShort("SZERZ_SZEL");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagSuly = rs.getShort("ALAPA_SULY");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagBelsoAtmero = rs.getShort("ALAPA_BELSO_ATM");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagKulsoAtmero = rs.getShort("ALAPA_KULSO_ATM");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.luser = rs.getString("LUSER");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.meins = rs.getString("MEINS");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                setupData.add(sd);
            }

        } catch (Exception ex) {
            System.err.println(ex);
            System.out.println(ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                    ex.printStackTrace(System.err);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace(System.err);
                    System.out.println(ex);
                }
            }
        }
        return setupData;
    }

    public SapR3SetupData selectQueryObject(String query) {

        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        SapR3SetupData sd = new SapR3SetupData();
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            connection = DriverManager.getConnection(this.url, this.user, this.password);
           // System.out.println(query);
            st = connection.prepareStatement(query);
            rs = st.executeQuery();
            BigDecimal Thousand = new BigDecimal(1000);

            if (rs.next()) {

                try {
                    sd.mandt = rs.getString("MANDT");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.guid = rs.getString("GUID");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.sapAlapanyagAzonosito = rs.getString("CHARG");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.berendezesAzonosito = rs.getString("BER_AZON");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.idobelyeg = rs.getString("INSTIME");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagVastagsag = rs.getBigDecimal("ALAPA_VAST").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottVastagsag = rs.getBigDecimal("SZERZ_VAST").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottVastagsagTuresPlusz = rs.getBigDecimal("SZERZ_VAST_TUR_MAX").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottVastagsagTuresMinimum = rs.getBigDecimal("SZERZ_VAST_TUR_MIN").multiply(Thousand).shortValue();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagSzelesseg = rs.getShort("ALAPA_SZEL");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.szerzodottSzelesseg = rs.getShort("SZERZ_SZEL");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagSuly = rs.getShort("ALAPA_SULY");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagBelsoAtmero = rs.getShort("ALAPA_BELSO_ATM");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.alapanyagKulsoAtmero = rs.getShort("ALAPA_KULSO_ATM");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.luser = rs.getString("LUSER");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

                try {
                    sd.meins = rs.getString("MEINS");
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }

            }

        } catch (Exception ex) {

        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {

                    ex.printStackTrace(System.err);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
        return sd;
    }

    public ArrayList<SapR3SetupData> getAllSetupData() {
        return selectQuery("SELECT * FROM SAPR3.ZHP_SAP_KMR ORDER BY INSTIME DESC");
    }

    public ArrayList<SapR3SetupData> getAllSetupDataOnlyOneMachine(String berendezesAzonosito) {
        return selectQuery("SELECT * FROM SAPR3.ZHP_SAP_KMR WHERE BER_AZON LIKE '"
                + berendezesAzonosito + "'  ORDER BY INSTIME DESC");
    }

    public SapR3SetupData getLastData(String berendezesAzonosito) {
        // System.out.println("SELECT * FROM (SELECT * FROM SAPR3.ZHP_SAP_KMR WHERE BER_AZON LIKE '"+berendezesAzonosito + "'  ORDER BY INSTIME DESC) WHERE ROWNUM = 1");
        return selectQueryObject("SELECT * FROM (SELECT * FROM SAPR3.ZHP_SAP_KMR WHERE BER_AZON LIKE '"
                + berendezesAzonosito + "'  ORDER BY INSTIME DESC) WHERE ROWNUM = 1");
    }

    public SapR3SetupData getLastData() {
        return selectQueryObject("SELECT * FROM (SELECT * FROM SAPR3.ZHP_SAP_KMR ORDER BY INSTIME DESC) WHERE ROWNUM = 1");
    }

}
