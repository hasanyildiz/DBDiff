package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;
import java.util.Map;
import com.github.hasanyildiz.dbdiff.db.SchemaReader;
import com.github.hasanyildiz.dbdiff.model.DbIndex;
import com.github.hasanyildiz.dbdiff.sql.DdlBuilder;

/**
 * Index'leri karşılaştırır: kaynakta olmayanları siler, hedefte olmayanları oluşturur, sütunları veya UNIQUE
 * özelliği farklı olanları yeniden oluşturur.
 *
 * @author EROLULKER
 */
public final class IndexComparer implements SchemaComparer {

    private final SchemaReader source;
    private final SchemaReader target;

    public IndexComparer(SchemaReader source, SchemaReader target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void compare(DiffListener listener) throws SQLException {
        listener.onProgress("İndexler karşılaştırılıyor...");
        Map<String, DbIndex> sourceIndexes = source.readIndexes();
        Map<String, DbIndex> targetIndexes = target.readIndexes();

        listener.onScriptLine("/**************Target eksik indexler******************/");
        for (DbIndex targetIndex : targetIndexes.values()) {
            if (!sourceIndexes.containsKey(targetIndex.getIndexName())) {
                reportProgress(listener, targetIndex);
                listener.onScriptLine(DdlBuilder.dropIndex(targetIndex));
            }
        }

        listener.onScriptLine("/**************Source eksik indexler******************/");
        for (DbIndex sourceIndex : sourceIndexes.values()) {
            if (!targetIndexes.containsKey(sourceIndex.getIndexName())) {
                reportProgress(listener, sourceIndex);
                listener.onScriptLine(DdlBuilder.createIndex(sourceIndex));
            }
        }

        listener.onScriptLine("/**************Source index karşılaştırması******************/");
        for (DbIndex sourceIndex : sourceIndexes.values()) {
            DbIndex targetIndex = targetIndexes.get(sourceIndex.getIndexName());
            if (targetIndex != null) {
                reportProgress(listener, sourceIndex);
                if (!isSameDefinition(sourceIndex, targetIndex)) {
                    listener.onScriptLine(DdlBuilder.dropIndex(targetIndex));
                    listener.onScriptLine(DdlBuilder.createIndex(sourceIndex));
                }
            }
        }
    }

    private static void reportProgress(DiffListener listener, DbIndex index) {
        listener.onProgress(index.getIndexName() + " index karşılaştırılıyor...");
    }

    /** Sütun sırası index için anlamlı olduğundan sütun listeleri sırasıyla karşılaştırılır. */
    private static boolean isSameDefinition(DbIndex source, DbIndex target) {
        return source.isUnique() == target.isUnique()
                && source.getColumnNames().equals(target.getColumnNames());
    }
}
