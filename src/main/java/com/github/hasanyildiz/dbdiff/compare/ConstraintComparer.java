package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import com.github.hasanyildiz.dbdiff.db.SchemaReader;
import com.github.hasanyildiz.dbdiff.model.ConstraintType;
import com.github.hasanyildiz.dbdiff.model.DbConstraint;
import com.github.hasanyildiz.dbdiff.sql.DdlBuilder;

/**
 * {@link ConstraintType} içinde tanımlı tipteki constraint'leri karşılaştırır: hedefte olmayanları ekler, sütunları
 * farklı olanları silip yeniden ekler.
 *
 * @author EROLULKER
 */
public final class ConstraintComparer implements SchemaComparer {

    private final SchemaReader source;
    private final SchemaReader target;

    public ConstraintComparer(SchemaReader source, SchemaReader target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void compare(DiffListener listener) throws SQLException {
        listener.onProgress("Constraintler karşılaştırılıyor...");
        Map<String, DbConstraint> sourceConstraints = source.readConstraints();
        Map<String, DbConstraint> targetConstraints = target.readConstraints();

        for (DbConstraint sourceConstraint : sourceConstraints.values()) {
            Optional<ConstraintType> type = sourceConstraint.getType();
            if (type.isPresent() && !targetConstraints.containsKey(sourceConstraint.getConstraintName())) {
                listener.onScriptLine(DdlBuilder.addConstraint(sourceConstraint, type.get()));
            }
        }

        for (DbConstraint sourceConstraint : sourceConstraints.values()) {
            Optional<ConstraintType> type = sourceConstraint.getType();
            DbConstraint targetConstraint = targetConstraints.get(sourceConstraint.getConstraintName());
            if (type.isPresent() && targetConstraint != null && !hasSameColumns(sourceConstraint, targetConstraint)) {
                listener.onScriptLine(DdlBuilder.dropConstraint(sourceConstraint));
                listener.onScriptLine(DdlBuilder.addConstraint(sourceConstraint, type.get()));
            }
        }
    }

    private static boolean hasSameColumns(DbConstraint source, DbConstraint target) {
        return source.getColumnNames().size() == target.getColumnNames().size()
                && new HashSet<>(source.getColumnNames()).equals(new HashSet<>(target.getColumnNames()));
    }
}
