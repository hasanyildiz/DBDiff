package com.github.hasanyildiz.dbdiff.compare;

import java.util.Objects;
import com.github.hasanyildiz.dbdiff.model.DbColumn;

/**
 * Aynı isimli bir sütunun kaynak ve hedefteki tanımları arasındaki farklar.
 */
public final class ColumnDiff {

    private final boolean dataTypeChanged;
    private final boolean dataLengthChanged;
    private final boolean dataPrecisionChanged;
    private final boolean dataScaleChanged;
    private final boolean dataDefaultChanged;
    private final boolean nullableChanged;

    private ColumnDiff(DbColumn source, DbColumn target) {
        dataTypeChanged = !Objects.equals(source.getDataType(), target.getDataType());
        dataLengthChanged = source.getDataLength() != target.getDataLength();
        dataPrecisionChanged = isSetOnEitherSide(source.getDataPrecision(), target.getDataPrecision())
                && source.getDataPrecision() != target.getDataPrecision();
        dataScaleChanged = isSetOnEitherSide(source.getDataScale(), target.getDataScale())
                && source.getDataScale() != target.getDataScale();
        dataDefaultChanged = !Objects.equals(source.getDataDefault(), target.getDataDefault());
        nullableChanged = source.isNullable() != target.isNullable();
    }

    public static ColumnDiff between(DbColumn source, DbColumn target) {
        return new ColumnDiff(source, target);
    }

    private static boolean isSetOnEitherSide(int sourceValue, int targetValue) {
        return sourceValue > 0 || targetValue > 0;
    }

    public boolean isEmpty() {
        return !isTypeDefinitionChanged() && !dataDefaultChanged && !nullableChanged;
    }

    /** Veri tipi, uzunluk, hassasiyet veya ölçekten herhangi biri değiştiyse true. */
    public boolean isTypeDefinitionChanged() {
        return dataTypeChanged || dataLengthChanged || dataPrecisionChanged || dataScaleChanged;
    }

    public boolean isDataDefaultChanged() {
        return dataDefaultChanged;
    }

    public boolean isNullableChanged() {
        return nullableChanged;
    }
}
