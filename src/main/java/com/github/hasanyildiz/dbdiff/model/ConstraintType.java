package com.github.hasanyildiz.dbdiff.model;

import java.util.Optional;

/**
 * Karşılaştırılan constraint tipleri. Yeni bir tip eklemek için buraya bir değer eklemek yeterlidir.
 *
 * @author EROLULKER
 */
public enum ConstraintType {
    PRIMARY_KEY("P", "PRIMARY KEY"),
    CHECK("C", "CHECK");

    private final String code;
    private final String keyword;

    ConstraintType(String code, String keyword) {
        this.code = code;
        this.keyword = keyword;
    }

    /** ALL_CONSTRAINTS.CONSTRAINT_TYPE değeri. */
    public String getCode() {
        return code;
    }

    /** ALTER TABLE ... ADD CONSTRAINT içinde kullanılan anahtar kelime. */
    public String getKeyword() {
        return keyword;
    }

    public static Optional<ConstraintType> fromCode(String code) {
        for (ConstraintType type : values()) {
            if (type.code.equals(code)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
