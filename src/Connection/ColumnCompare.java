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


public class ColumnCompare {

    private static Connection sourceConn = null;
    private static Connection targetConn = null;
    private final String tableName;
    private List<DBColumn> sourceTableColumns;
    private List<DBColumn> targetTableColumns;

    public ColumnCompare(String tableName) throws SQLException, ClassNotFoundException {
        sourceConn = SourceConnection.getConnection();
        targetConn = TargetConnection.getConnection();
        this.tableName = tableName;
        getTargetColumns();
        getSourceColumns();
    }

    private List<DBColumn> getSourceColumns() throws SQLException {
        String selectSql = String.format("SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE,DATA_LENGTH,NVL(DATA_PRECISION,-1) DATA_PRECISION,NVL(DATA_SCALE,-1) DATA_SCALE,DATA_DEFAULT,NULLABLE FROM USER_TAB_COLS WHERE TABLE_NAME='%s'", new Object[]{tableName});
        PreparedStatement st = sourceConn.prepareStatement(selectSql);
        if (st != null) {
            ResultSet rs = st.executeQuery();
            if (rs != null) {
                sourceTableColumns = new ArrayList<>();
                DBColumn col;
                while (rs.next()) {
                    col = new DBColumn();
                    col.setTableName(rs.getString("TABLE_NAME"));
                    col.setColumnName(rs.getString("COLUMN_NAME"));
                    col.setDataType(rs.getString("DATA_TYPE"));
                    col.setDataLength(rs.getString("DATA_LENGTH"));
                    col.setDataDefault(rs.getString("DATA_DEFAULT"));
                    col.setNullable(rs.getString("NULLABLE"));
                    col.setDataPrecision(rs.getInt("DATA_PRECISION"));
                    col.setDataScale(rs.getInt("DATA_SCALE"));
                    sourceTableColumns.add(col);
                }
            }
        }
        return sourceTableColumns;
    }

    private List<DBColumn> getTargetColumns() throws SQLException {
        String selectSql = String.format("SELECT TABLE_NAME,COLUMN_NAME,DATA_TYPE,DATA_LENGTH,NVL(DATA_PRECISION,-1) DATA_PRECISION,NVL(DATA_SCALE,-1) DATA_SCALE,DATA_DEFAULT,NULLABLE FROM USER_TAB_COLS WHERE TABLE_NAME='%s'", new Object[]{tableName});
        PreparedStatement st = targetConn.prepareStatement(selectSql);
        if (st != null) {
            ResultSet rs = st.executeQuery();
            if (rs != null) {
                targetTableColumns = new ArrayList<>();
                DBColumn col;
                while (rs.next()) {
                    col = new DBColumn();
                    col.setTableName(rs.getString("TABLE_NAME"));
                    col.setColumnName(rs.getString("COLUMN_NAME"));
                    col.setDataType(rs.getString("DATA_TYPE"));
                    col.setDataLength(rs.getString("DATA_LENGTH"));
                    col.setDataDefault(rs.getString("DATA_DEFAULT"));
                    col.setNullable(rs.getString("NULLABLE"));
                    col.setDataPrecision(rs.getInt("DATA_PRECISION"));
                    col.setDataScale(rs.getInt("DATA_SCALE"));
                    targetTableColumns.add(col);
                }
            }
        }
        return targetTableColumns;
    }

    public List<String> compareColumns() {
        List<String> addSqlList = new ArrayList<>();
        List<String> modifySqlList = new ArrayList<>();
        for (DBColumn col : sourceTableColumns) {
            boolean found = false;
            for (DBColumn item : targetTableColumns) {
                if (!found) {
                    if (item.getColumnName().equals(col.getColumnName())) {
                        found = true;
                        if (!isColumnsEqual(col, item)) {
                            modifySqlList.add(DBColumn.generateModifySql(col, item));
                        }
                        break;
                    } else {
                        found = false;
                    }
                }
            }
            if (!found) {
                addSqlList.add(DBColumn.generateAddSql(col));
            }
        }
        modifySqlList.addAll(addSqlList);
        return modifySqlList;
    }

    private boolean isColumnsEqual(DBColumn sourceCol, DBColumn targetCol) {
        if (!sourceCol.getColumnName().equals(targetCol.getColumnName())) {
        }
        if ((sourceCol.getDataType() == null && targetCol.getDataType() != null) || (sourceCol.getDataType() != null && targetCol.getDataType() == null)) {
        }
        if (sourceCol.getDataType() != null && targetCol.getDataType() != null) {
            if (!sourceCol.getDataType().equals(targetCol.getDataType())) {
                sourceCol.setDataTypeChanged(true);
            }
        }
        if (!sourceCol.getDataLength().equals(targetCol.getDataLength())) {
            sourceCol.setDataLengthChanged(true);
        }
        if ((sourceCol.getDataDefault() == null && targetCol.getDataDefault() != null) || (sourceCol.getDataDefault() != null && targetCol.getDataDefault() == null)) {
            sourceCol.setDataDefaultChanged(true);
        }
        if (sourceCol.getDataDefault() != null && targetCol.getDataDefault() != null) {
            if (!sourceCol.getDataDefault().equals(targetCol.getDataDefault())) {
                sourceCol.setDataDefaultChanged(true);
            }
        }
        if(sourceCol.getDataScale()>0){
            if(sourceCol.getDataScale()!=targetCol.getDataScale()){
                sourceCol.setDataScaleChanged(true);
                sourceCol.setDataLengthChanged(true);
            }
        }
        if(targetCol.getDataScale()>0){
            if(targetCol.getDataScale()!=sourceCol.getDataScale()){
                sourceCol.setDataScaleChanged(true);
                sourceCol.setDataLengthChanged(true);
            }
        }
        
        if(sourceCol.getDataPrecision()>0){
            if(sourceCol.getDataPrecision()!=targetCol.getDataPrecision()){
                sourceCol.setDataPrecisionChanged(true);
                sourceCol.setDataLengthChanged(true);
            }
        }
        if(targetCol.getDataPrecision()>0){
            if(targetCol.getDataPrecision()!=sourceCol.getDataPrecision()){
                sourceCol.setDataPrecisionChanged(true);
                sourceCol.setDataLengthChanged(true);
            }
        }
        if (!sourceCol.getNullable().equals(targetCol.getNullable())) {
            sourceCol.setNullableChanged(true);
        }
        if(sourceCol.isDataDefaultChanged()||
                sourceCol.isDataLengthChanged()||
                sourceCol.isDataPrecisionChanged()||
                sourceCol.isDataScaleChanged()||
                sourceCol.isNullableChanged()||
                sourceCol.isDataTypeChanged()){
            return false;
        }
            
        return true;
    }
}
