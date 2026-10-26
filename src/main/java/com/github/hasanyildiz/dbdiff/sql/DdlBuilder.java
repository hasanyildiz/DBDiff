package com.github.hasanyildiz.dbdiff.sql;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.hasanyildiz.dbdiff.compare.ColumnDiff;
import com.github.hasanyildiz.dbdiff.model.ConstraintType;
import com.github.hasanyildiz.dbdiff.model.DbColumn;
import com.github.hasanyildiz.dbdiff.model.DbConstraint;
import com.github.hasanyildiz.dbdiff.model.DbIndex;
import com.github.hasanyildiz.dbdiff.model.DbView;

/**
 * Hedef şemayı kaynağa eşitlemek için çalıştırılacak Oracle DDL ifadelerini üretir.
 */
public final class DdlBuilder {

    /** Uzunluğu parantez içinde yazılan veri tipleri. */
    private static final Set<String> LENGTH_TYPES = new HashSet<>(
            Arrays.asList("VARCHAR2", "VARCHAR", "NVARCHAR2", "CHAR", "NCHAR", "RAW"));

    private DdlBuilder() {
    }

    public static String createTable(String tableDdl) {
        return tableDdl.endsWith(";") ? tableDdl : tableDdl + ";";
    }

    public static String addColumn(DbColumn column) {
        StringBuilder sql = new StringBuilder("ALTER TABLE ")
                .append(column.getTableName()).append(" ADD ").append(column.getColumnName())
                .append(' ').append(typeDefinition(column));
        if (column.getDataDefault() != null) {
            sql.append(" DEFAULT ").append(column.getDataDefault());
        }
        if (!column.isNullable()) {
            sql.append(" NOT NULL");
        }
        return sql.append(';').toString();
    }

    public static String modifyColumn(DbColumn source, ColumnDiff diff) {
        StringBuilder sql = new StringBuilder("ALTER TABLE ")
                .append(source.getTableName()).append(" MODIFY ").append(source.getColumnName());
        if (diff.isTypeDefinitionChanged()) {
            sql.append(' ').append(typeDefinition(source));
        }
        if (diff.isDataDefaultChanged()) {
            sql.append(" DEFAULT ").append(source.getDataDefault() == null ? "NULL" : source.getDataDefault());
        }
        if (diff.isNullableChanged()) {
            sql.append(source.isNullable() ? " NULL" : " NOT NULL");
        }
        return sql.append(';').toString();
    }

    /** Veri tipi ve varsa uzunluk/hassasiyet bilgisi, ör. VARCHAR2(50) veya NUMBER(10,2). */
    static String typeDefinition(DbColumn column) {
        String dataType = column.getDataType();
        if ("NUMBER".equals(dataType)) {
            if (column.getDataPrecision() == DbColumn.NOT_SET) {
                return dataType;
            }
            int scale = column.getDataScale() == DbColumn.NOT_SET ? 0 : column.getDataScale();
            return dataType + "(" + column.getDataPrecision() + "," + scale + ")";
        }
        if (LENGTH_TYPES.contains(dataType)) {
            return dataType + "(" + column.getDataLength() + ")";
        }
        return dataType;
    }

    public static String createIndex(DbIndex index) {
        String columns = index.getColumnNames().stream()
                .map(column -> "\"" + column + "\"")
                .collect(Collectors.joining(","));
        return String.format("CREATE %sINDEX %s ON %s (%s);",
                index.isUnique() ? "UNIQUE " : "", index.getIndexName(), index.getTableName(), columns);
    }

    public static String dropIndex(DbIndex index) {
        return String.format("DROP INDEX %s;", index.getIndexName());
    }

    public static String addConstraint(DbConstraint constraint, ConstraintType type) {
        String definition = type == ConstraintType.CHECK
                ? constraint.getSearchCondition()
                : String.join(",", constraint.getColumnNames());
        return String.format("ALTER TABLE %s ADD CONSTRAINT %s %s (%s);",
                constraint.getTableName(), constraint.getConstraintName(), type.getKeyword(), definition);
    }

    public static String dropConstraint(DbConstraint constraint) {
        return String.format("ALTER TABLE %s DROP CONSTRAINT %s;",
                constraint.getTableName(), constraint.getConstraintName());
    }

    public static String createOrReplaceView(DbView view) {
        return String.format("CREATE OR REPLACE VIEW %s AS %s;", view.getViewName(), view.getText());
    }

    public static String compileSchema(String schemaName) {
        return String.format("EXEC DBMS_UTILITY.compile_schema(schema => '%s');", schemaName);
    }
}
