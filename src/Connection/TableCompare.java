/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EROLULKER
 */
public class TableCompare {
    private static Connection sourceConn=null;
    private static Connection targetConn=null;
    private List<String> sourceTableNameList;
    private List<String> targetTableNameList;
    public TableCompare() throws SQLException, ClassNotFoundException{
        sourceConn = SourceConnection.getConnection();
        targetConn = TargetConnection.getConnection();
    }
    private List<String> getSourceTableNameList() throws SQLException{
        String selectSql = "SELECT TABLE_NAME from USER_TABLES ORDER BY TABLE_NAME";
        if(sourceConn!=null){
            PreparedStatement sourceStmt = sourceConn.prepareStatement(selectSql);
            ResultSet sourceRS = sourceStmt.executeQuery();
            if(sourceRS!=null){
                sourceTableNameList = new ArrayList<>();
                while(sourceRS.next()){
                    sourceTableNameList.add(sourceRS.getString("TABLE_NAME"));
                }
            }
            sourceStmt.close();
        }
        return sourceTableNameList;
    }
    private List<String> getTargetTableNameList() throws SQLException{
        String selectSql = "SELECT TABLE_NAME from USER_TABLES ORDER BY TABLE_NAME";
        if(targetConn!=null){
            PreparedStatement targetStmt = targetConn.prepareStatement(selectSql);
            ResultSet sourceRS = targetStmt.executeQuery();
            if(sourceRS!=null){
                targetTableNameList = new ArrayList<>();
                while(sourceRS.next()){
                    targetTableNameList.add(sourceRS.getString("TABLE_NAME"));
                }
            }
            targetStmt.close();
        }
        return targetTableNameList;
    }
    public List<String> getExistingTables() throws SQLException{
        List<String> sourceTables = getSourceTableNameList();
        List<String> targetTables = getTargetTableNameList();
        sourceTables.retainAll(targetTables);
        return sourceTables;
    }
    private List<String> getMissingTables() throws SQLException{
        List<String> sourceTables = getSourceTableNameList();
        List<String> targetTables = getTargetTableNameList();
        sourceTables.removeAll(targetTables);
        return sourceTables;
    }
    private String getTableCreateSql(String tableName) throws SQLException{
        String selectSql = String.format("select dbms_metadata.get_ddl( 'TABLE', '%s', '%s' ) SQL_RESULT from dual", new Object[]{tableName,SourceConnection.getSchemaName()});
        if(sourceConn!=null){
            PreparedStatement stmt = sourceConn.prepareStatement(selectSql);
            ResultSet rs = stmt.executeQuery();
            if(rs!=null){
                if(rs.next()){
                    String createSql = rs.getString("SQL_RESULT");
                    return createSql;
                }
            }
            stmt.close();
        }
        return "";
    }
    public String[] compareTables() throws SQLException{
        List<String> missingTables = getMissingTables();
        String[] sqlList = new String[missingTables.size()];
        int i = 0;
        for(String st : missingTables){
            String res = getTableCreateSql(st);
            if(!res.endsWith(";"))
                res = res.concat(";");
            sqlList[i] = res;
            i++;
        }
        return sqlList;
    }
}
