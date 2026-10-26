package com.github.hasanyildiz.dbdiff.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.github.hasanyildiz.dbdiff.model.ConstraintType;
import com.github.hasanyildiz.dbdiff.model.DbColumn;
import com.github.hasanyildiz.dbdiff.model.DbConstraint;
import com.github.hasanyildiz.dbdiff.model.DbIndex;
import com.github.hasanyildiz.dbdiff.model.DbView;

/**
 * Şema bilgisini Oracle veri sözlüğü görünümlerinden (USER_* ve ALL_*) okur.
 *
 * <p>LONG tipindeki sütunlar (DATA_DEFAULT, SEARCH_CONDITION, TEXT) Oracle sürücüsünde akış olarak okunduğu için
 * sorgularda en sona konur ve en son okunur.
 */
public final class OracleSchemaReader implements SchemaReader {

    private static final int FETCH_SIZE = 1000;

    private static final String TABLE_NAMES_SQL =
            "SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME";
    private static final String TABLE_DDL_SQL =
            "SELECT DBMS_METADATA.GET_DDL('TABLE', ?, ?) SQL_RESULT FROM DUAL";
    private static final String COLUMNS_SQL =
            "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NVL(DATA_PRECISION, -1) DATA_PRECISION,"
            + " NVL(DATA_SCALE, -1) DATA_SCALE, NULLABLE, DATA_DEFAULT"
            + " FROM USER_TAB_COLS WHERE TABLE_NAME = ? ORDER BY COLUMN_ID";
    private static final String INDEXES_SQL =
            "SELECT I.INDEX_NAME, I.TABLE_NAME, I.UNIQUENESS, C.COLUMN_NAME"
            + " FROM ALL_INDEXES I"
            + " LEFT JOIN ALL_IND_COLUMNS C ON C.INDEX_OWNER = I.OWNER AND C.INDEX_NAME = I.INDEX_NAME"
            + " WHERE I.OWNER = ? ORDER BY I.INDEX_NAME, C.COLUMN_POSITION";
    private static final String CONSTRAINTS_SQL =
            "SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE, TABLE_NAME, SEARCH_CONDITION"
            + " FROM ALL_CONSTRAINTS WHERE OWNER = ?";
    private static final String CONSTRAINT_COLUMNS_SQL =
            "SELECT CONSTRAINT_NAME, COLUMN_NAME FROM ALL_CONS_COLUMNS"
            + " WHERE OWNER = ? ORDER BY CONSTRAINT_NAME, POSITION";
    private static final String VIEWS_SQL =
            "SELECT VIEW_NAME, TEXT FROM ALL_VIEWS WHERE OWNER = ? ORDER BY VIEW_NAME";

    private final Connection connection;
    private final String schemaName;

    public OracleSchemaReader(DatabaseConnection databaseConnection) {
        this.connection = databaseConnection.getConnection();
        this.schemaName = databaseConnection.getSchemaName();
    }

    @Override
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public List<String> readTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(TABLE_NAMES_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tableNames.add(resultSet.getString("TABLE_NAME"));
            }
        }
        return tableNames;
    }

    @Override
    public String readTableDdl(String tableName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(TABLE_DDL_SQL)) {
            statement.setString(1, tableName);
            statement.setString(2, schemaName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("SQL_RESULT") : "";
            }
        }
    }

    @Override
    public List<DbColumn> readColumns(String tableName) throws SQLException {
        List<DbColumn> columns = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(COLUMNS_SQL)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.add(new DbColumn(
                            resultSet.getString("TABLE_NAME"),
                            resultSet.getString("COLUMN_NAME"),
                            resultSet.getString("DATA_TYPE"),
                            resultSet.getInt("DATA_LENGTH"),
                            resultSet.getInt("DATA_PRECISION"),
                            resultSet.getInt("DATA_SCALE"),
                            resultSet.getString("DATA_DEFAULT"),
                            "Y".equals(resultSet.getString("NULLABLE"))));
                }
            }
        }
        return columns;
    }

    @Override
    public Map<String, DbIndex> readIndexes() throws SQLException {
        Map<String, String> tableNames = new LinkedHashMap<>();
        Map<String, Boolean> uniqueness = new HashMap<>();
        Map<String, List<String>> columnNames = new HashMap<>();
        try (PreparedStatement statement = prepareForSchema(INDEXES_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                tableNames.put(indexName, resultSet.getString("TABLE_NAME"));
                uniqueness.put(indexName, "UNIQUE".equals(resultSet.getString("UNIQUENESS")));
                List<String> columns = columnNames.computeIfAbsent(indexName, name -> new ArrayList<>());
                String columnName = resultSet.getString("COLUMN_NAME");
                if (columnName != null) {
                    columns.add(columnName);
                }
            }
        }
        Map<String, DbIndex> indexes = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : tableNames.entrySet()) {
            String indexName = entry.getKey();
            indexes.put(indexName, new DbIndex(indexName, entry.getValue(),
                    uniqueness.get(indexName), columnNames.get(indexName)));
        }
        return indexes;
    }

    @Override
    public Map<String, DbConstraint> readConstraints() throws SQLException {
        Map<String, List<String>> columnNames = readConstraintColumns();
        Map<String, DbConstraint> constraints = new LinkedHashMap<>();
        try (PreparedStatement statement = prepareForSchema(CONSTRAINTS_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String constraintName = resultSet.getString("CONSTRAINT_NAME");
                String typeCode = resultSet.getString("CONSTRAINT_TYPE");
                String tableName = resultSet.getString("TABLE_NAME");
                String searchCondition = ConstraintType.CHECK.getCode().equals(typeCode)
                        ? readLongAsString(resultSet, "SEARCH_CONDITION")
                        : null;
                constraints.put(constraintName, new DbConstraint(constraintName, tableName, typeCode, searchCondition,
                        columnNames.getOrDefault(constraintName, Collections.emptyList())));
            }
        }
        return constraints;
    }

    private Map<String, List<String>> readConstraintColumns() throws SQLException {
        Map<String, List<String>> columnNames = new HashMap<>();
        try (PreparedStatement statement = prepareForSchema(CONSTRAINT_COLUMNS_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                columnNames.computeIfAbsent(resultSet.getString("CONSTRAINT_NAME"), name -> new ArrayList<>())
                        .add(resultSet.getString("COLUMN_NAME"));
            }
        }
        return columnNames;
    }

    @Override
    public Map<String, DbView> readViews() throws SQLException {
        Map<String, DbView> views = new LinkedHashMap<>();
        try (PreparedStatement statement = prepareForSchema(VIEWS_SQL);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String viewName = resultSet.getString("VIEW_NAME");
                views.put(viewName, new DbView(viewName, readLongAsString(resultSet, "TEXT")));
            }
        }
        return views;
    }

    private PreparedStatement prepareForSchema(String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            statement.setFetchSize(FETCH_SIZE);
            statement.setString(1, schemaName);
            return statement;
        } catch (SQLException ex) {
            statement.close();
            throw ex;
        }
    }

    /** LONG tipindeki bir sütunu metin olarak okur; değer null ise boş metin döner. */
    private static String readLongAsString(ResultSet resultSet, String columnName) throws SQLException {
        try (InputStream inputStream = resultSet.getBinaryStream(columnName)) {
            if (inputStream == null) {
                return "";
            }
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException ex) {
            throw new SQLException(columnName + " sütunu okunamadı", ex);
        }
    }
}
