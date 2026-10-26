package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.github.hasanyildiz.dbdiff.db.SchemaReader;
import com.github.hasanyildiz.dbdiff.sql.DdlBuilder;

/**
 * Karşılaştırıcıları sırayla çalıştırarak hedef şemayı kaynağa eşitleyen tam script'i üretir.
 */
public final class SchemaDiff {

    private final SchemaReader target;
    private final List<SchemaComparer> comparers;

    public SchemaDiff(SchemaReader source, SchemaReader target) {
        this(target, Arrays.asList(
                new TableComparer(source, target),
                new ColumnComparer(source, target),
                new IndexComparer(source, target),
                new ConstraintComparer(source, target),
                new ViewComparer(source, target)));
    }

    public SchemaDiff(SchemaReader target, List<SchemaComparer> comparers) {
        this.target = target;
        this.comparers = new ArrayList<>(comparers);
    }

    public void run(DiffListener listener) throws SQLException {
        for (SchemaComparer comparer : comparers) {
            comparer.compare(listener);
        }
        listener.onScriptLine(DdlBuilder.compileSchema(target.getSchemaName()));
    }
}
