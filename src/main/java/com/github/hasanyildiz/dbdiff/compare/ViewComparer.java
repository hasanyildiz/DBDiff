package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;
import java.util.Map;
import com.github.hasanyildiz.dbdiff.db.SchemaReader;
import com.github.hasanyildiz.dbdiff.model.DbView;
import com.github.hasanyildiz.dbdiff.sql.DdlBuilder;

/**
 * Hedefte olmayan veya metni farklı olan view'lar için CREATE OR REPLACE VIEW ifadesi üretir.
 *
 * @author EROLULKER
 */
public final class ViewComparer implements SchemaComparer {

    private final SchemaReader source;
    private final SchemaReader target;

    public ViewComparer(SchemaReader source, SchemaReader target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void compare(DiffListener listener) throws SQLException {
        listener.onProgress("View'lar karşılaştırılıyor...");
        Map<String, DbView> sourceViews = source.readViews();
        Map<String, DbView> targetViews = target.readViews();

        for (DbView sourceView : sourceViews.values()) {
            if (!targetViews.containsKey(sourceView.getViewName())) {
                writeView(listener, sourceView);
            }
        }
        for (DbView sourceView : sourceViews.values()) {
            DbView targetView = targetViews.get(sourceView.getViewName());
            if (targetView != null && !sourceView.getText().equals(targetView.getText())) {
                writeView(listener, sourceView);
            }
        }
    }

    private static void writeView(DiffListener listener, DbView view) {
        listener.onScriptLine("");
        listener.onScriptLine(DdlBuilder.createOrReplaceView(view));
        listener.onScriptLine("");
    }
}
