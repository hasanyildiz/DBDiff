package com.github.hasanyildiz.dbdiff.compare;

/**
 * Karşılaştırma sırasında ilerleme mesajlarını ve üretilen script satırlarını alır.
 */
public interface DiffListener {

    /** Kullanıcıya gösterilecek durum mesajı, ör. "Tablolar karşılaştırılıyor...". */
    void onProgress(String message);

    /** Üretilen script'in bir satırı; boş metin boş satır anlamına gelir. */
    void onScriptLine(String line);
}
