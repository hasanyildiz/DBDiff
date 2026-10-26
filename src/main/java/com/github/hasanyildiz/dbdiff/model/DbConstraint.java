package com.github.hasanyildiz.dbdiff.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ALL_CONSTRAINTS görünümündeki bir constraint ve sütunları.
 *
 * @author EROLULKER
 */
public final class DbConstraint {

    private final String constraintName;
    private final String tableName;
    private final String typeCode;
    private final String searchCondition;
    private final List<String> columnNames;

    public DbConstraint(String constraintName, String tableName, String typeCode,
            String searchCondition, List<String> columnNames) {
        this.constraintName = constraintName;
        this.tableName = tableName;
        this.typeCode = typeCode;
        this.searchCondition = searchCondition;
        this.columnNames = Collections.unmodifiableList(new ArrayList<>(columnNames));
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getTableName() {
        return tableName;
    }

    /** Constraint tipi; karşılaştırılmayan bir tipse (ör. foreign key) boş döner. */
    public Optional<ConstraintType> getType() {
        return ConstraintType.fromCode(typeCode);
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }
}
