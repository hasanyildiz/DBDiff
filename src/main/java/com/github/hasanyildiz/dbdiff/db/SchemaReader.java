package com.github.hasanyildiz.dbdiff.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.github.hasanyildiz.dbdiff.model.DbColumn;
import com.github.hasanyildiz.dbdiff.model.DbConstraint;
import com.github.hasanyildiz.dbdiff.model.DbIndex;
import com.github.hasanyildiz.dbdiff.model.DbView;

/**
 * Bir şemadaki nesnelerin tanımlarını okur. Karşılaştırıcılar veritabanına doğrudan değil bu arayüze bağlıdır,
 * böylece testlerde sahte bir okuyucu kullanılabilir.
 */
public interface SchemaReader {

    String getSchemaName();

    /** Tablo adları, alfabetik sırada. */
    List<String> readTableNames() throws SQLException;

    /** Tablonun CREATE TABLE ifadesi. */
    String readTableDdl(String tableName) throws SQLException;

    /** Tablonun sütunları, tablodaki sırasıyla. */
    List<DbColumn> readColumns(String tableName) throws SQLException;

    /** Index adına göre index'ler, alfabetik sırada. */
    Map<String, DbIndex> readIndexes() throws SQLException;

    /** Constraint adına göre constraint'ler. */
    Map<String, DbConstraint> readConstraints() throws SQLException;

    /** View adına göre view'lar, alfabetik sırada. */
    Map<String, DbView> readViews() throws SQLException;
}
