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

/**
 *
 * @author EROLULKER
 */
public class TargetConnection {
    private static Connection targetConn;
    private static String USERNAME="";
    private static String PASSWORD="";
    public static String DBNAME;
    public static String IP;
    public static String PORT="1521";
    protected static String ConnectionString;

    public static void setUSERNAME(String USERNAME) {
        TargetConnection.USERNAME = USERNAME;
    }

    public static void setPASSWORD(String PASSWORD) {
        TargetConnection.PASSWORD = PASSWORD;
    }
    
    
    
    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if (targetConn == null || targetConn.isClosed()){
            Class.forName("oracle.jdbc.driver.OracleDriver");
            targetConn = DriverManager.getConnection(createConnectionString(), USERNAME, PASSWORD);
            System.out.println("target connection success");
            
        }
        return targetConn;
    }
    private static String createConnectionString(){
        ConnectionString = String.format("jdbc:oracle:thin:@%s:%s:%s", new Object[]{IP,PORT,DBNAME});
        return ConnectionString;
    }
        public static String getSchemaName() {
            if(targetConn!=null)
                return USERNAME;
        return "";
    }
        public static boolean testConnection() throws SQLException, ClassNotFoundException{
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
