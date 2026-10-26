package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;
import java.util.List;
import com.github.hasanyildiz.dbdiff.db.SchemaReader;
import com.github.hasanyildiz.dbdiff.sql.DdlBuilder;

/**
 * Kaynakta olup hedefte olmayan tablolar için CREATE TABLE ifadesi üretir.
 *
 * @author EROLULKER
 */
public final class TableComparer implements SchemaComparer {

    private final SchemaReader source;
    private final SchemaReader target;

    public TableComparer(SchemaReader source, SchemaReader target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void compare(DiffListener listener) throws SQLException {
        listener.onProgress("Tablolar karşılaştırılıyor...");
        List<String> missingTables = source.readTableNames();
        missingTables.removeAll(target.readTableNames());
        for (String tableName : missingTables) {
            listener.onScriptLine(DdlBuilder.createTable(source.readTableDdl(tableName)));
        }
    }
}
