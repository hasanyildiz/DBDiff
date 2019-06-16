/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class SourceConnection {

    private static Connection sourceConn;
    private static  String USERNAME = "";
    private static String PASSWORD = "";
    public static String DBNAME;
    public static String IP;
    public static String PORT = "1521";
    protected static String ConnectionString;

    public static void setUSERNAME(String USERNAME) {
        SourceConnection.USERNAME = USERNAME;
    }

    public static void setPASSWORD(String PASSWORD) {
        SourceConnection.PASSWORD = PASSWORD;
    }

    
    
    
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (sourceConn == null || sourceConn.isClosed()) {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            sourceConn = DriverManager.getConnection(createConnectionString(), USERNAME, PASSWORD);
            System.out.println("source connection success");
        }
        return sourceConn;
    }

    private static String createConnectionString() {
        ConnectionString = String.format("jdbc:oracle:thin:@%s:%s:%s", new Object[]{IP, PORT, DBNAME});
        return ConnectionString;
    }

    public static String getSchemaName() {
        if (sourceConn != null) {
            return USERNAME;
        }
        return "";
    }
/**
 * Girilen bağlantı parametlererinin doğru olup olmadığını denetlemek için dual tablosundan basit bir sorgu yapar ve sonuç değerini okur.
 * @return test bağlantısı başarılı olursa true, diğer durumlarda false
 * @throws SQLException
 * @throws ClassNotFoundException 
 */
    public static boolean testConnection() throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        if (conn != null) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 'TEST' TEST FROM DUAL");
            if(rs!=null){
                if(rs.next()){
                    return true;
                }
            }
        }
        return false;
    }
}
