package com.github.hasanyildiz.dbdiff.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ALL_INDEXES görünümündeki bir index ve sırasıyla sütunları.
 *
 * @author EROLULKER
 */
public final class DbIndex {

    private final String indexName;
    private final String tableName;
    private final boolean unique;
    private final List<String> columnNames;

    public DbIndex(String indexName, String tableName, boolean unique, List<String> columnNames) {
        this.indexName = indexName;
        this.tableName = tableName;
        this.unique = unique;
        this.columnNames = Collections.unmodifiableList(new ArrayList<>(columnNames));
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isUnique() {
        return unique;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }
}
