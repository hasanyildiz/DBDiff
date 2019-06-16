/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connection;


public class DBColumn {

    private String tableName;
    private String columnName;
    private String dataType;
    private String dataLength;
    private String dataDefault;
    private String nullable;
    private int dataPrecision;
    private int dataScale;
    private boolean dataLengthChanged;
    private boolean nullableChanged;
    private boolean dataDefaultChanged;
    private boolean dataPrecisionChanged;
    private boolean dataScaleChanged;
    private boolean dataTypeChanged;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public int getDataPrecision() {
        return dataPrecision;
    }

    public void setDataPrecision(int dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    public int getDataScale() {
        return dataScale;
    }

    public void setDataScale(int dataScale) {
        this.dataScale = dataScale;
    }

    public String getDataDefault() {
        return dataDefault;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public void setDataDefault(String dataDefault) {
        this.dataDefault = dataDefault;
    }

    public boolean isDataLengthChanged() {
        return dataLengthChanged;
    }

    public void setDataLengthChanged(boolean dataLengthChanged) {
        this.dataLengthChanged = dataLengthChanged;
    }

    public boolean isNullableChanged() {
        return nullableChanged;
    }

    public void setNullableChanged(boolean nullableChanged) {
        this.nullableChanged = nullableChanged;
    }

    public boolean isDataDefaultChanged() {
        return dataDefaultChanged;
    }

    public void setDataDefaultChanged(boolean dataDefaultChanged) {
        this.dataDefaultChanged = dataDefaultChanged;
    }

    public boolean isDataPrecisionChanged() {
        return dataPrecisionChanged;
    }

    public void setDataPrecisionChanged(boolean dataPrecisionChanged) {
        this.dataPrecisionChanged = dataPrecisionChanged;
    }

    public boolean isDataScaleChanged() {
        return dataScaleChanged;
    }

    public void setDataScaleChanged(boolean dataScaleChanged) {
        this.dataScaleChanged = dataScaleChanged;
    }

    public boolean isDataTypeChanged() {
        return dataTypeChanged;
    }

    public void setDataTypeChanged(boolean dataTypeChanged) {
        this.dataTypeChanged = dataTypeChanged;
    }

    public static String generateAddSql(DBColumn col) {
        String sql = "";
        if (col != null) {
            sql = String.format("ALTER TABLE %s ADD %s %s", new Object[]{col.getTableName(), col.getColumnName(), col.getDataType()});
            if (!(col.getDataType().equals("NUMBER") || col.getDataType().equals("DATE")||col.getDataType().equals("CLOB"))) {
                sql += String.format("(%s)", col.getDataLength());
            }
            if (col.getDataDefault() != null) {
                if (col.getDataType().equals("NUMBER")) {
                    sql += String.format(" DEFAULT %s", col.getDataDefault());
                } else {
                    sql += String.format(" DEFAULT %s", col.getDataDefault());
                }
            }
            if ("N".equals(col.getNullable())) {
                sql += " NOT NULL";
            }
            sql += ";";
        }
        return sql;
    }

    public static String generateModifySql(DBColumn col, DBColumn target) {
        String sql = "";
        if (col != null) {
            sql = "ALTER TABLE " + col.getTableName() + " MODIFY " + col.getColumnName() + " ";
            if (col.isDataTypeChanged()
                    || (col.isDataScaleChanged() || col.isDataPrecisionChanged())
                    || col.isDataLengthChanged()) {
                sql += col.getDataType();
                if ("NUMBER".equals(col.getDataType())) {
                    if (col.getDataPrecision() != -1 && col.getDataScale() != -1) {
                        sql += "(" + col.getDataPrecision() + "," + col.getDataScale() + ") ";
                    }
                } else if ("VARCHAR2".equals(col.getDataType())) {
                    sql += "(" + col.getDataLength() + ") ";
                }
            }
            if (col.isDataDefaultChanged()) {
                sql += " DEFAULT " + col.getDataDefault() + " ";
            }
            if (col.isNullableChanged()) {
                if ("N".equals(col.getNullable()) && ("Y".equals(target.getNullable()))) {
                    sql += " NOT NULL";
                }
            }
            sql += ";";
        }
        return sql;
    }
}
