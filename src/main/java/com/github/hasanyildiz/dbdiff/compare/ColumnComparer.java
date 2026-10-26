package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.github.hasanyildiz.dbdiff.db.SchemaReader;
import com.github.hasanyildiz.dbdiff.model.DbColumn;
import com.github.hasanyildiz.dbdiff.sql.DdlBuilder;

/**
 * Her iki şemada da bulunan tabloların sütunlarını karşılaştırır: değişen sütunlar için MODIFY, hedefte olmayan
 * sütunlar için ADD ifadesi üretir.
 *
 * @author EROLULKER
 */
public final class ColumnComparer implements SchemaComparer {

    private final SchemaReader source;
    private final SchemaReader target;

    public ColumnComparer(SchemaReader source, SchemaReader target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void compare(DiffListener listener) throws SQLException {
        List<String> commonTables = source.readTableNames();
        commonTables.retainAll(target.readTableNames());
        for (String tableName : commonTables) {
            listener.onProgress(tableName + " tablosunun sütunları karşılaştırılıyor...");
            List<String> statements = compareTable(tableName);
            if (!statements.isEmpty()) {
                statements.forEach(listener::onScriptLine);
                listener.onScriptLine("");
            }
        }
    }

    private List<String> compareTable(String tableName) throws SQLException {
        Map<String, DbColumn> targetColumns = target.readColumns(tableName).stream()
                .collect(Collectors.toMap(DbColumn::getColumnName, Function.identity()));
        List<String> modifyStatements = new ArrayList<>();
        List<String> addStatements = new ArrayList<>();
        for (DbColumn sourceColumn : source.readColumns(tableName)) {
            DbColumn targetColumn = targetColumns.get(sourceColumn.getColumnName());
            if (targetColumn == null) {
                addStatements.add(DdlBuilder.addColumn(sourceColumn));
                continue;
            }
            ColumnDiff diff = ColumnDiff.between(sourceColumn, targetColumn);
            if (!diff.isEmpty()) {
                modifyStatements.add(DdlBuilder.modifyColumn(sourceColumn, diff));
            }
        }
        modifyStatements.addAll(addStatements);
        return modifyStatements;
    }
}
