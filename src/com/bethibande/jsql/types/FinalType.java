package com.bethibande.jsql.types;

public class FinalType {
    
    private SQLType sqlType;
    private long size;

    public FinalType(SQLType sqlType, long size) {
        this.sqlType = sqlType;
        this.size = size;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    public long getSize() {
        return size;
    }
    
    public static FinalType of(SQLType type, long size) {
        return new FinalType(type, size);
    }
}
