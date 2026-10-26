package com.github.hasanyildiz.dbdiff.model;

/**
 * USER_TAB_COLS görünümündeki bir tablo sütunu.
 *
 * @author EROLULKER
 */
public final class DbColumn {

    /** Hassasiyet (precision) veya ölçek (scale) tanımlı değilse kullanılan değer. */
    public static final int NOT_SET = -1;

    private final String tableName;
    private final String columnName;
    private final String dataType;
    private final int dataLength;
    private final int dataPrecision;
    private final int dataScale;
    private final String dataDefault;
    private final boolean nullable;

    public DbColumn(String tableName, String columnName, String dataType, int dataLength,
            int dataPrecision, int dataScale, String dataDefault, boolean nullable) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
        this.dataLength = dataLength;
        this.dataPrecision = dataPrecision;
        this.dataScale = dataScale;
        this.dataDefault = dataDefault;
        this.nullable = nullable;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int getDataPrecision() {
        return dataPrecision;
    }

    public int getDataScale() {
        return dataScale;
    }

    /** Sütunun varsayılan değeri, baştaki ve sondaki boşluklar atılmış olarak; tanımlı değilse null. */
    public String getDataDefault() {
        return dataDefault == null ? null : dataDefault.trim();
    }

    public boolean isNullable() {
        return nullable;
    }
}
