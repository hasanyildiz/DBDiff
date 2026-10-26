package com.github.hasanyildiz.dbdiff.compare;

import java.sql.SQLException;

/**
 * Kaynak ve hedef şemada bir nesne türünü (tablo, index, view...) karşılaştırır ve hedefi kaynağa eşitleyecek
 * ifadeleri dinleyiciye yazar.
 */
public interface SchemaComparer {

    void compare(DiffListener listener) throws SQLException;
}
