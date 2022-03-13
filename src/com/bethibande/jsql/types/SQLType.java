package com.bethibande.jsql.types;

public enum SQLType {

    AUTO_DETECT(-1, -1, -1, null),
    CHAR(1, 255, 0, "CHAR"),
    VARCHAR(32, 65535, 0, "VARCHAR"),
    TINYBLOB(32, 255, 0, "TINYBLOB"),
    TINYTEXT(32, 255, 0, "TINYTEXT"),
    TEXT(32, 65535, 0, "TEXT"),
    BLOB(32, 65535, 0, "BLOB"),
    MEDIUMTEXT(32, 16777215, 0, "MEDIUMTEXT"),
    MEDIUMBLOB(32, 16777215, 0, "MEDIUMBLOB"),
    LONGTEXT(32, 4294967295L, 0, "LONGTEXT"),
    LONGBLOB(32, 4294967295L, 0, "LONGBLOB"),
    BIT(1, 64, 1, "BIT"),
    TINYINT(255, 255, 1, "TINYINT"),
    BOOL(-1, -1, -1, "BOOL"),
    BOOLEAN(-1, -1,- 1, "BOOLEAN"),
    SMALLINT(255, 255, 0, "SMALLINT"),
    MEDIUMINT(255, 255, 0, "MEDIUMINT"),
    INT(255, 255, 0, "INT"),
    INTEGER(255, 255, 0, "INTEGER"),
    BIGINT(255, 255, 0, "BIGINT"),
    FLOAT(24, 24, 0, "FLOAT"),
    DOUBLE(53, 53, 25, "FLOAT");

    private final long def;
    private final long max;
    private final long min;
    private final String type;

    SQLType(long def, long max, long min, String type) {
        this.def = def;
        this.max = max;
        this.min = min;
        this.type = type;
    }

    public long getDefaultSize() {
        return this.def;
    }

    public long getMaxSize() {
        return this.max;
    }

    public long getMinSize() {
        return this.min;
    }

    public String getType() {
        return type;
    }
}
